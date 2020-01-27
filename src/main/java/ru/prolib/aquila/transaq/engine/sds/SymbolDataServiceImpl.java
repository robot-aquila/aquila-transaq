package ru.prolib.aquila.transaq.engine.sds;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.MDBuilder;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.MDUpdate;
import ru.prolib.aquila.core.BusinessEntities.MDUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.MDUpdateType;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrCounter;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrCounter.Field;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrRepository;
import ru.prolib.aquila.core.BusinessEntities.TickType;
import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainer;
import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainerImpl;
import ru.prolib.aquila.data.DFGroupRepo;
import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.entity.SecurityBoardParams;
import ru.prolib.aquila.transaq.entity.SecurityParams;
import ru.prolib.aquila.transaq.entity.SecurityQuotations;
import ru.prolib.aquila.transaq.impl.TQDirectory;
import ru.prolib.aquila.transaq.impl.TQFieldAssembler;
import ru.prolib.aquila.transaq.impl.TQStateUpdate;
import ru.prolib.aquila.transaq.impl.TransaqException;
import ru.prolib.aquila.transaq.remote.ISecIDF;
import ru.prolib.aquila.transaq.remote.ISecIDG;
import ru.prolib.aquila.transaq.remote.ISecIDT;
import ru.prolib.aquila.transaq.remote.MessageFields.FQuotation;
import ru.prolib.aquila.transaq.remote.MessageFields.FTrade;
import ru.prolib.aquila.transaq.remote.entity.Quote;

public class SymbolDataServiceImpl implements SymbolDataService {
	private static final Map<Integer, FeedID> token_to_feed;
	private static final Logger logger;
	private static final ZoneId zoneID = ZoneId.of("Europe/Moscow");
	
	static {
		logger = LoggerFactory.getLogger(SymbolDataServiceImpl.class);
		token_to_feed = new LinkedHashMap<>();
		token_to_feed.put(Field.NUM_L0, FeedID.SYMBOL_PRIMARY);
		token_to_feed.put(Field.NUM_L1_BBO, FeedID.SYMBOL_QUOTATIONS);
		token_to_feed.put(Field.NUM_L1, FeedID.SYMBOL_ALLTRADES);
		token_to_feed.put(Field.NUM_L2, FeedID.SYMBOL_QUOTES);
	}
	
	private final ServiceLocator services;
	private final SymbolSubscrRepository subscrCounters;
	private final Map<Symbol, MDBuilder> mdbuilderMap;
	private final DFGroupRepo<ISecIDT, FeedID> groups;
	
	private boolean connected = false;
	
	public SymbolDataServiceImpl(ServiceLocator services, SymbolSubscrRepository subscrCounters) {
		this.services = services;
		this.subscrCounters = subscrCounters;
		this.mdbuilderMap = new Hashtable<>();
		this.groups = new DFGroupRepo<>(new StateOfDataFeedsFactory());
	}
	
	private TQDirectory getDir() {
		return services.getDirectory();
	}
	
	private EditableSecurity getSecurity(Symbol symbol) {
		return services.getTerminal().getEditableSecurity(symbol);
	}
	
	private boolean hasSubscribers(Symbol symbol) {
		return subscrCounters.contains(symbol) && subscrCounters.getOrThrow(symbol).getNumL0() > 0; 
	}
	
	private List<Symbol> getKnownSymbols(GSymbol gid) {
		return getDir().getKnownSymbols(gid);
	}
	
	private boolean isKnownSymbol(Symbol symbol) {
		return getDir().isKnownSymbol(symbol);
	}
	
	private DeltaUpdateBuilder toUpdate(DeltaUpdateBuilder builder,
			SecurityParams general_params,
			SecurityBoardParams board_params)
	{
		TQFieldAssembler asm = services.getAssembler();
		asm.toSecDisplayName(general_params, getDir().getBoardName(board_params.getBoardCode()), builder);
		asm.toSecInitialMargin(general_params, builder);
		asm.toSecUpperPriceLimit(general_params, builder);
		asm.toSecLowerPriceLimit(general_params, builder);
		asm.toSecSettlementPrice(general_params, builder);
		asm.toSecExpirationTime(general_params, builder);
		asm.toSecLotSize(board_params, builder);
		asm.toSecTickSize(board_params, builder);
		asm.toSecTickValue(board_params, builder);
		return builder;
	}
	
	private DeltaUpdateBuilder toUpdate(SecurityParams general_params,
			SecurityBoardParams board_params)
	{
		return toUpdate(new DeltaUpdateBuilder(), general_params, board_params);
	}
	
	private DeltaUpdateBuilder toUpdate(DeltaUpdateBuilder builder, SecurityQuotations params) {
		TQFieldAssembler asm = services.getAssembler();
		asm.toSecOpenPrice(params, builder);
		asm.toSecHighPrice(params, builder);
		asm.toSecLowPrice(params, builder);
		asm.toSecClosePrice(params, builder);
		return builder;
	}
	
	private DeltaUpdateBuilder toUpdate(SecurityQuotations params) {
		return toUpdate(new DeltaUpdateBuilder(), params);
	}
	
	private boolean toLevel1Update(L1UpdateBuilder builder,
			Security security,
			TickType type,
			CDecimal price,
			Integer size,
			Instant time)
	{
		if ( price == null || size == null ) {
			return false;
		}
		builder.withType(type)
			.withPrice(security.isAvailable() ? security.round(price) : price)
			.withSize((long) size)
			.withSymbol(security.getSymbol())
			.withTime(time);
		return true;
	}
	
	private boolean toLevel1Update(L1UpdateBuilder builder,
			Security security,
			TickType type,
			CDecimal price,
			Integer size)
	{
		return toLevel1Update(builder, security, type, price, size, security.getTerminal().getCurrentTime());
	}
	
	private boolean toBidUpdate(L1UpdateBuilder builder, Security security, SecurityQuotations params) {
		return toLevel1Update(builder, security, TickType.BID, params.getBid(), params.getBidDepth());
	}
	
	private boolean toAskUpdate(L1UpdateBuilder builder, Security security, SecurityQuotations params) {
		return toLevel1Update(builder, security, TickType.ASK, params.getOffer(), params.getOfferDepth());
	}
	
	/**
	 * Determine and mark required data feed to subscribe or unsubscribe
	 * for specified symbol. This work based on current subscription counters.
	 * <p> 
	 * @param idt - key
	 * @param subscr - subscription counters
	 * @return true if at least one feed has pending subscription or pending
	 * unsubscription state.
	 */
	private boolean syncSubscrState(ISecIDT key, SymbolSubscrCounter subscr) {
		int x = 0;
		Iterator<Map.Entry<Integer, FeedID>> it = token_to_feed.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<Integer, FeedID> entry = it.next();
			if ( subscr.getInteger(entry.getKey()) > 0 ) {
				if ( groups.haveToSubscribe(key, entry.getValue()) ) {
					x ++;
				}
			} else {
				if ( groups.haveToUnsubscribe(key, entry.getValue()) ) {
					x ++;
				}
			}
		}
		return x > 0;
	}
	
	private void applyPendingChanges() throws TransaqException {
		// 1) Make 6 lists of TQSecID2:
		//    - to subscribe for quotations
		//    - to subscribe for alltrades
		//    - to subscribe for quotes
		//	  - to unsubscribe of quotations
		//	  - to unsubscribe of alltrades
		//	  - to unsubscribe of quotes
		TQDirectory dir = getDir();
		Set<ISecIDT> quotat_subscr = new LinkedHashSet<>(groups.getPendingSubscr(FeedID.SYMBOL_QUOTATIONS));
		Set<ISecIDT> trades_subscr = new LinkedHashSet<>(groups.getPendingSubscr(FeedID.SYMBOL_ALLTRADES));
		Set<ISecIDT> quotes_subscr = new LinkedHashSet<>(groups.getPendingSubscr(FeedID.SYMBOL_QUOTES));
		Set<ISecIDT> quotat_unsubs = new LinkedHashSet<>(groups.getPendingUnsubscr(FeedID.SYMBOL_QUOTATIONS));
		Set<ISecIDT> trades_unsubs = new LinkedHashSet<>(groups.getPendingUnsubscr(FeedID.SYMBOL_ALLTRADES));
		Set<ISecIDT> quotes_unsubs = new LinkedHashSet<>(groups.getPendingUnsubscr(FeedID.SYMBOL_QUOTES));
		for ( ISecIDT x : quotes_subscr ) {
			mdbuilderMap.remove(dir.toSymbol(dir.toSymbolTID(x)));
		}
		
		// 2) Call subscribe for all required feeds
		// 3) Change feed status of all subscribed feeds
		services.getConnector().subscribe(trades_subscr, quotat_subscr, quotes_subscr);
		groups.subscribed(trades_subscr, FeedID.SYMBOL_ALLTRADES);
		groups.subscribed(quotat_subscr, FeedID.SYMBOL_QUOTATIONS);
		groups.subscribed(quotes_subscr, FeedID.SYMBOL_QUOTES);
		
		// 4) Call unsubscribe of all feeds which aren't required anymore
		// 5) Change feed status of all unsubscribed feeds
		services.getConnector().unsubscribe(trades_unsubs, quotat_unsubs, quotes_unsubs);
		groups.unsubscribed(trades_unsubs, FeedID.SYMBOL_ALLTRADES);
		groups.unsubscribed(quotat_unsubs, FeedID.SYMBOL_QUOTATIONS);
		groups.unsubscribed(quotes_unsubs, FeedID.SYMBOL_QUOTES);		
	}
	
	private void _applyPendingChanges() {
		try {
			applyPendingChanges();
		} catch ( TransaqException e ) {
			// TODO: Have to do more.
			logger.error("Error applying pending changes: ", e);
		}
	}
	
	private ISecIDT toSecIDT(Symbol symbol) {
		return services.getDirectory().toSecIDT(symbol, true);
	}

	@Override
	public void onSubscribe(Symbol symbol, MDLevel level) {
		SymbolSubscrCounter subscr = subscrCounters.subscribe(symbol, level);
		if ( ! connected ) {
			return;
		}

		ISecIDT idt = toSecIDT(symbol);
		if ( idt == null || groups.isNotAvailable(idt) ) {
			return;
		}
		
		initialUpdateSecurity(symbol);
		
		if ( syncSubscrState(idt, subscr) ) {
			_applyPendingChanges();
		}
	}

	@Override
	public void onUnsubscribe(Symbol symbol, MDLevel level) {
		SymbolSubscrCounter subscr = subscrCounters.unsubscribe(symbol, level);
		if ( ! connected ) {
			return;
		}
		
		ISecIDT idt = toSecIDT(symbol);
		if ( idt == null || groups.isNotAvailable(idt) ) {
			return;
		}
		
		if ( syncSubscrState(idt, subscr) ) {
			_applyPendingChanges();
		}
	}
	
	@Override
	public void onConnectionStatusChange(boolean connected) {
		if ( this.connected = connected ) {
			// Connection established: it need to synchronize
			// subscription counters with state of data feeds
			// for each existing counter. Then apply pending
			// changes.
			boolean x = false;
			TQDirectory dir = services.getDirectory();
			for ( SymbolSubscrCounter subscr : subscrCounters.getEntities() ) {
				Symbol symbol = subscr.getSymbol();
				if ( isKnownSymbol(symbol) ) {
					initialUpdateSecurity(symbol);
					
					ISecIDT idt = dir.toSecIDT(symbol, true);
					if ( idt != null ) {
						if ( syncSubscrState(idt, subscr) ) {
							x = true;
						}
					}
				}
			}
			if ( x ) {
				_applyPendingChanges();
			}
			
		} else {
			// Connection lost: for each TRANSAQ/MOEX security ID
			// mark the state of data feeds as disconnected
			groups.unsubscribed();
			mdbuilderMap.clear();
		}
	}
	
	/**
	 * Does not check anything. Just does update up to maximum available data.
	 * <p>
	 * @param symbol - symbol of security
	 */
	private void initialUpdateSecurity(Symbol symbol) {
		TQDirectory dir = getDir();
		TSymbol tid = dir.toSymbolTID(symbol);
		EditableSecurity security = getSecurity(symbol);
		DeltaUpdateBuilder builder = new DeltaUpdateBuilder();
		if ( dir.isExistsSecurityParams(tid) && dir.isExistsSecurityBoardParams(tid) ) {
			builder = toUpdate(builder, dir.getSecurityParams(tid), dir.getSecurityBoardParams(tid));
		}
		SecurityQuotations quotations = null;
		if ( dir.isExistsSecurityQuotations(tid) ) {
			builder = toUpdate(builder, quotations = dir.getSecurityQuotations(tid));
		}
		if ( builder.hasTokens() ) {
			security.consume(builder.buildUpdate());
		}
		
		if ( quotations != null ) {
			L1UpdateBuilder l1_builder = null;
			if ( toAskUpdate(l1_builder = new L1UpdateBuilder(), security, quotations) ) {
				security.consume(l1_builder.buildL1Update());
			}
			if ( toBidUpdate(l1_builder = new L1UpdateBuilder(), security, quotations) ) {
				security.consume(l1_builder.buildL1Update());
			}
		}		
	}
	
	private void cascadeSecurityUpdate(GSymbol gid, SecurityParams general_params) {
		TQDirectory dir = getDir();
		List<Symbol> symbol_list = getKnownSymbols(gid);
		for ( Symbol symbol : symbol_list ) {
			TSymbol tid = dir.toSymbolTID(symbol);
			if ( hasSubscribers(symbol) && dir.isExistsSecurityBoardParams(tid) ) {
				EditableSecurity security = getSecurity(symbol);
				DeltaUpdateBuilder builder = toUpdate(general_params, dir.getSecurityBoardParams(tid));
				if ( builder.hasTokens() ) {
					security.consume(builder.buildUpdate());
				}
			}
		}
	}

	@Override
	public void onSecurityUpdateF(TQStateUpdate<ISecIDF> update) {
		TQDirectory dir = getDir();
		SecurityParams params = dir.updateSecurityParamsF(update);
		if ( ! connected ) {
			return;
		}
		cascadeSecurityUpdate(dir.toSymbolGID(update.getID()), params);
	}

	@Override
	public void onSecurityUpdateG(TQStateUpdate<ISecIDG> update) {
		TQDirectory dir = getDir();
		SecurityParams params = dir.updateSecurityParamsP(update);
		if ( ! connected ) {
			return;
		}
		cascadeSecurityUpdate(dir.toSymbolGID(update.getID()), params);
	}

	@Override
	public void onSecurityBoardUpdate(TQStateUpdate<ISecIDT> update) {
		TQDirectory dir = getDir();
		SecurityBoardParams params = dir.updateSecurityBoardParams(update);
		if ( ! connected ) {
			return;
		}
		TSymbol tid = dir.toSymbolTID(update.getID());
		Symbol symbol = dir.toSymbol(tid);
		if ( hasSubscribers(symbol) && dir.isExistsSecurityParams(tid) ) {
			EditableSecurity security = getSecurity(symbol);
			DeltaUpdateBuilder builder = toUpdate(dir.getSecurityParams(tid), params);
			if ( builder.hasTokens() ) {
				security.consume(builder.buildUpdate());
			}
		}
	}

	@Override
	public void onSecurityQuotationUpdate(TQStateUpdate<ISecIDT> update) {
		TQDirectory dir = getDir();
		SecurityQuotations params = dir.updateSecurityQuotations(update);
		if ( ! connected ) {
			return;
		}
		TSymbol tid = dir.toSymbolTID(update.getID());
		Symbol symbol = dir.toSymbol(tid);
		if ( hasSubscribers(symbol) ) {
			EditableSecurity security = getSecurity(symbol);
			DeltaUpdateBuilder builder = toUpdate(params);
			if ( builder.hasTokens() ) {
				security.consume(builder.buildUpdate());
			}
			L1UpdateBuilder l1_builder = null;
			int ask_fields[] = { FQuotation.OFFER, FQuotation.OFFER_DEPTH };
			if ( params.atLeastOneHasChanged(ask_fields)
			  && toAskUpdate(l1_builder = new L1UpdateBuilder(), security, params) )
			{
				security.consume(l1_builder.buildL1Update());
			}
			int bid_fields[] = { FQuotation.BID, FQuotation.BID_DEPTH };
			if ( params.atLeastOneHasChanged(bid_fields)
			  && toBidUpdate(l1_builder = new L1UpdateBuilder(), security, params) )
			{
				security.consume(l1_builder.buildL1Update());
			}
		}
	}

	@Override
	public void onSecurityTrade(TQStateUpdate<ISecIDT> update) {
		if ( ! connected ) {
			return;
		}
		TSymbol tid = getDir().toSymbolTID(update.getID());
		Symbol symbol = getDir().toSymbol(tid);
		if ( hasSubscribers(symbol) ) {
			UpdatableStateContainer cont = new UpdatableStateContainerImpl("XXX");
			cont.consume(update.getUpdate());
			int expected_fields[] = { FTrade.TIME, FTrade.PRICE, FTrade.QUANTITY };
			if ( cont.isDefined(expected_fields) ) {
				EditableSecurity security = getSecurity(symbol);
				CDecimal price = cont.getCDecimal(FTrade.PRICE);
				price = price.withScale(Math.max(security.getScale(), price.getScale()));
				L1UpdateBuilder builder = new L1UpdateBuilder(symbol)
						.withTrade()
						.withTime(((LocalDateTime)cont.getObject(FTrade.TIME)).atZone(zoneID).toInstant())
						.withPrice(price)
						.withSize(cont.getCDecimal(FTrade.QUANTITY));
				security.consume(builder.buildL1Update());
			}
		}
	}

	@Override
	public void onSecurityQuotes(List<Quote> quotes) {
		Map<ISecIDT, List<Quote>> map = new HashMap<>();
		for ( Quote quote : quotes ) {
			ISecIDT sec_id = quote.getID();
			List<Quote> sub_list = map.get(sec_id);
			if ( sub_list == null ) {
				map.put(sec_id, sub_list = new LinkedList<>());
			}
			sub_list.add(quote);
		}
		
		Instant time = services.getTerminal().getCurrentTime();
		Iterator<Map.Entry<ISecIDT, List<Quote>>> it = map.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<ISecIDT, List<Quote>> entry = it.next();
			updateMD(entry.getKey(), entry.getValue(), time);
		}
	}
	
	private MDBuilder getMDBuilder(Symbol symbol) {
		MDBuilder md_builder = mdbuilderMap.get(symbol);
		if ( md_builder == null ) {
			mdbuilderMap.put(symbol, md_builder = new MDBuilder(symbol));
		}
		return md_builder;
	}
	
	private boolean isMDBuilderExists(Symbol symbol) {
		return mdbuilderMap.containsKey(symbol);
	}
	
	private void updateMD(ISecIDT sec_id, List<Quote> quotes, Instant time) {
		TSymbol tid = getDir().toSymbolTID(sec_id);
		Symbol symbol = getDir().toSymbol(tid);
		MDUpdateBuilder mdu_builder = new MDUpdateBuilder(symbol)
				.withTime(time)
				.withType(isMDBuilderExists(symbol) ? MDUpdateType.UPDATE : MDUpdateType.REFRESH);
		for ( Quote quote : quotes ) {
			Long qty_b = quote.getBuy(), qty_s = quote.getSell();
			CDecimal price = quote.getPrice();
			if ( qty_b != null ) {
				if ( qty_b == -1 ) {
					mdu_builder.deleteBid(price);
				} else if ( qty_b > 0 ) {
					mdu_builder.replaceBid(price, CDecimalBD.of(qty_b));
				} else {
					logger.warn("Incorrect buy value of {} quote: {}", symbol, qty_b);
				}
			}
			if ( qty_s != null ) {
				if ( qty_s == -1 ) {
					mdu_builder.deleteAsk(price);
				} else if ( qty_s > 0 ) {
					mdu_builder.replaceAsk(price, CDecimalBD.of(qty_s));
				} else {
					logger.warn("Incorrect sell value of {} quote: {}", symbol, qty_s);
				}
			}
		}
		MDUpdate md_update = mdu_builder.buildMDUpdate();
		getSecurity(symbol).consume(md_update);
		
		// Actually, we do not need this right now.
		MDBuilder md_builder = getMDBuilder(symbol);
		md_builder.consume(md_update);
	}

}

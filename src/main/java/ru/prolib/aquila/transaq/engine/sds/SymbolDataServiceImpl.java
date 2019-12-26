package ru.prolib.aquila.transaq.engine.sds;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrCounter;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrCounter.Field;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrRepository;
import ru.prolib.aquila.core.BusinessEntities.TickType;
import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainer;
import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainerImpl;
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
	private final StateOfDataFeedsFactory feedStateFactory;
	private final Map<ISecIDT, StateOfDataFeeds> feedStateMap;
	private final SymbolSubscrRepository subscrCounters;
	
	private boolean connected = false;
	
	public SymbolDataServiceImpl(
			ServiceLocator services,
			StateOfDataFeedsFactory sodf_factory,
			SymbolSubscrRepository subscrCounters,
			Map<ISecIDT, StateOfDataFeeds> feed_state_map)
	{
		this.services = services;
		this.feedStateFactory = sodf_factory;
		this.subscrCounters = subscrCounters;
		this.feedStateMap = feed_state_map;
	}
	
	public SymbolDataServiceImpl(
			ServiceLocator services,
			StateOfDataFeedsFactory sodf_factory,
			SymbolSubscrRepository subscrCounters)
	{
		this(services, sodf_factory, subscrCounters, new LinkedHashMap<>());
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
	 * @param state - symbol state handler
	 * @param subscr - subscription counters
	 * @return true if at least one feed has pending subscription or pending
	 * unsubscription state.
	 */
	private boolean syncSubscrState(StateOfDataFeeds state, SymbolSubscrCounter subscr) {
		int x = 0;
		Iterator<Map.Entry<Integer, FeedID>> it = token_to_feed.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<Integer, FeedID> entry = it.next();
			if ( subscr.getInteger(entry.getKey()) > 0 ) {
				if ( state.markToSubscribe(entry.getValue()) ) {
					x ++;
				}
			} else {
				if ( state.markToUnsubscribe(entry.getValue()) ) {
					x ++;
				}
			}
		}
		return x > 0;
	}
	
	private StateOfDataFeeds getStateOfDataFeeds(ISecIDT idt) {
		StateOfDataFeeds state = feedStateMap.get(idt);
		if ( state == null ) {
			state = feedStateFactory.produce(idt);
			feedStateMap.put(idt, state);
		}
		return state;
	}
	
	/**
	 * Get state of data feeds for the symbol.
	 * <p>
	 * @param symbol - local symbol
	 * @return state of data feeds or null if appropriate TRANSAQ/MOEX identifier currently mapped with another symbol.
	 * This is all about problem with MOEX repeating security codes like for futures. For example, RTS-12.9 and
	 * RTS-12.19 are of same security codes RIZ9. If current symbol under RIZ9@FUT is RTS-12.19 then calling this method
	 * with symbol of RTS-12.9@FUT will give null. Because that symbol is not actual and current feeds under RIZ9
	 * belong to RTS-12.19.
	 */
	private StateOfDataFeeds getStateOfDataFeeds(Symbol symbol) {
		ISecIDT idt = services.getDirectory().toSecIDT(symbol, true);
		return idt == null ? null : getStateOfDataFeeds(idt);
	}
	
	private void applyPendingChanges() throws TransaqException {
		// 1) Make 6 lists of TQSecID2:
		//    - to subscribe for quotations
		//    - to subscribe for alltrades
		//    - to subscribe for quotes
		//	  - to unsubscribe of quotations
		//	  - to unsubscribe of alltrades
		//	  - to unsubscribe of quotes

		Map<FeedID, Pair<Set<ISecIDT>, Set<ISecIDT>>> cache = new LinkedHashMap<>();
		cache.put(FeedID.SYMBOL_QUOTATIONS, Pair.of(new LinkedHashSet<>(), new LinkedHashSet<>()));
		cache.put(FeedID.SYMBOL_ALLTRADES, Pair.of(new LinkedHashSet<>(), new LinkedHashSet<>()));
		cache.put(FeedID.SYMBOL_QUOTES, Pair.of(new LinkedHashSet<>(), new LinkedHashSet<>()));
		for ( StateOfDataFeeds state : feedStateMap.values() ) {
			for ( FeedID feed_id : cache.keySet() ) {
				switch ( state.getFeedStatus(feed_id) ) {
				case PENDING_SUBSCR:
					cache.get(feed_id).getLeft().add(state.getSecIDT());
					break;
				case PENDING_UNSUBSCR:
					cache.get(feed_id).getRight().add(state.getSecIDT());
					break;
				default:
					break;
				}
			}
		}
		
		// 2) Call subscribe for all required feeds
		// 3) Change feed status of all subscribed feeds
		services.getConnector().subscribe(
				cache.get(FeedID.SYMBOL_ALLTRADES).getLeft(),
				cache.get(FeedID.SYMBOL_QUOTATIONS).getLeft(),
				cache.get(FeedID.SYMBOL_QUOTES).getLeft()
			);
		for ( FeedID feed_id : cache.keySet() ) {
			for ( ISecIDT sec_id : cache.get(feed_id).getLeft() ) {
				feedStateMap.get(sec_id).setFeedStatus(feed_id, SubscrStatus.SUBSCR);
			}
		}
		
		// 4) Call unsubscribe of all feeds which aren't required anymore
		// 5) Change feed status of all unsubscribed feeds
		services.getConnector().unsubscribe(
				cache.get(FeedID.SYMBOL_ALLTRADES).getRight(),
				cache.get(FeedID.SYMBOL_QUOTATIONS).getRight(),
				cache.get(FeedID.SYMBOL_QUOTES).getRight()
			);
		for ( FeedID feed_id : cache.keySet() ) {
			for ( ISecIDT sec_id : cache.get(feed_id).getRight() ) {
				feedStateMap.get(sec_id).setFeedStatus(feed_id, SubscrStatus.NOT_SUBSCR);
			}
		}
	}
	
	private void _applyPendingChanges() {
		try {
			applyPendingChanges();
		} catch ( TransaqException e ) {
			// TODO: Have to do more.
			logger.error("Error applying pending changes: ", e);
		}
	}

	@Override
	public void onSubscribe(Symbol symbol, MDLevel level) {
		SymbolSubscrCounter subscr = subscrCounters.subscribe(symbol, level);
		if ( ! connected ) {
			return;
		}

		StateOfDataFeeds state = getStateOfDataFeeds(symbol);
		if ( state == null || state.isNotFound() ) {
			return;
		}
		
		initialUpdateSecurity(symbol);
		
		if ( syncSubscrState(state, subscr) ) {
			_applyPendingChanges();
		}
	}

	@Override
	public void onUnsubscribe(Symbol symbol, MDLevel level) {
		SymbolSubscrCounter subscr = subscrCounters.unsubscribe(symbol, level);
		if ( ! connected ) {
			return;
		}
		
		StateOfDataFeeds state = getStateOfDataFeeds(symbol);
		if ( state == null || state.isNotFound() ) {
			return;
		}
		
		if ( syncSubscrState(state, subscr) ) {
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
						if ( syncSubscrState(getStateOfDataFeeds(idt), subscr) ) {
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
			for ( StateOfDataFeeds state : feedStateMap.values() ) {
				state.markAllNotSubscribed();
			}

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
	public void onSecurityQuotes(List<TQStateUpdate<ISecIDT>> update_list) {
		// TODO Auto-generated method stub
		
	}

}

package ru.prolib.aquila.transaq.engine.sds;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrCounter;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrCounter.Field;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrRepository;
import ru.prolib.aquila.transaq.engine.SymbolDataService;
import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.impl.TQDirectory;
import ru.prolib.aquila.transaq.impl.TransaqException;
import ru.prolib.aquila.transaq.remote.ISecIDT;

public class SymbolDataServiceImpl implements SymbolDataService {
	private static final Map<Integer, FeedID> token_to_feed;
	private static final Logger logger;
	
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
		this(services, sodf_factory, subscrCounters, new Hashtable<>());
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
	
	private boolean syncSubscrStates() {
		boolean x = false;
		TQDirectory dir = services.getDirectory();
		for ( SymbolSubscrCounter subscr : subscrCounters.getEntities() ) {
			Symbol symbol = subscr.getSymbol();
			ISecIDT idt = dir.toSecIDT(symbol, true);
			if ( idt != null ) {
				if ( syncSubscrState(getStateOfDataFeeds(idt), subscr) ) {
					x = true;
				}
			}
		}
		return x;
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
		cache.put(FeedID.SYMBOL_QUOTATIONS, Pair.of(new HashSet<>(), new HashSet<>()));
		cache.put(FeedID.SYMBOL_ALLTRADES, Pair.of(new HashSet<>(), new HashSet<>()));
		cache.put(FeedID.SYMBOL_QUOTES, Pair.of(new HashSet<>(), new HashSet<>()));
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
				cache.get(FeedID.SYMBOL_QUOTATIONS).getLeft(),
				cache.get(FeedID.SYMBOL_ALLTRADES).getLeft(),
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
				cache.get(FeedID.SYMBOL_QUOTATIONS).getRight(),
				cache.get(FeedID.SYMBOL_ALLTRADES).getRight(),
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
			if ( syncSubscrStates() ) {
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

}

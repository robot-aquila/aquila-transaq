package ru.prolib.aquila.transaq.engine.sds;

import java.util.HashSet;
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
	private final SymbolStateFactory stateFactory;
	private final SymbolStateRepository stateRepository;
	private final SymbolSubscrRepository subscrCounters;
	private boolean connected = false;
	
	public SymbolDataServiceImpl(
			ServiceLocator services,
			SymbolStateFactory factory,
			SymbolStateRepository repository,
			SymbolSubscrRepository subscrCounters)
	{
		this.stateFactory = factory;
		this.services = services;
		this.stateRepository = repository;
		this.subscrCounters = subscrCounters;
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
	private boolean syncSubscrState(SymbolStateHandler state, SymbolSubscrCounter subscr) {
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
	
	private SymbolStateHandler getStateBySymbol(Symbol symbol) {
		SymbolStateHandler state = stateRepository.getBySymbol(symbol);
		if ( state == null ) {
			state = stateFactory.produce(symbol);
			stateRepository.register(state);
		}
		return state;
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
		for ( SymbolStateHandler state : stateRepository.getAll() ) {
			for ( FeedID feed_id : cache.keySet() ) {
				switch ( state.getFeedState(feed_id) ) {
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
				stateRepository.getBySecIDT(sec_id).setFeedState(feed_id, FeedSubscrStatus.SUBSCR);
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
				stateRepository.getBySecIDT(sec_id).setFeedState(feed_id, FeedSubscrStatus.NOT_SUBSCR);
			}
		}
	}
	
	private void _applyPendingChanges() {
		try {
			applyPendingChanges();
		} catch ( TransaqException e ) {
			// TODO: Have to be done more.
			logger.error("Error applying pending changes: ", e);
		}
	}

	@Override
	public void subscribe(Symbol symbol, MDLevel level) {
		SymbolSubscrCounter subscr = subscrCounters.subscribe(symbol, level);
		if ( ! connected ) {
			return;
		}

		SymbolStateHandler state = getStateBySymbol(symbol);
		if ( state.isMarkedAsNotFound() ) {
			return;
		}
		
		if ( syncSubscrState(state, subscr) ) {
			_applyPendingChanges();
		}
	}

	@Override
	public void unsubscribe(Symbol symbol, MDLevel level) {
		SymbolSubscrCounter subscr = subscrCounters.unsubscribe(symbol, level);
		if ( ! connected ) {
			return;
		}
		
		SymbolStateHandler state = getStateBySymbol(symbol);
		if ( state.isMarkedAsNotFound() ) {
			return;
		}
		
		if ( syncSubscrState(state, subscr) ) {
			_applyPendingChanges();
		}
	}

}

package ru.prolib.aquila.transaq.engine.sds;

import java.util.Hashtable;
import java.util.Map;

import ru.prolib.aquila.data.DFGroup;
import ru.prolib.aquila.data.DFSubscrState;
import ru.prolib.aquila.data.DFSubscrStatus;
import ru.prolib.aquila.transaq.remote.ISecIDT;

/**
 * Representation of security data feeds state.
 * <p>
 * Security data feeds aren't directly related with the Aquila's symbol.
 * It is because TRANSAQ/MOEX security codes can point different securities in different time.
 * This class represents state of data feeds for security in terms of TRANSAQ/MOEX
 * and shouldn't be linked with local (AQUILA) symbols or securities.
 */
public class StateOfDataFeeds extends DFGroup<ISecIDT, FeedID> {
	
	public StateOfDataFeeds(ISecIDT idt, Map<FeedID, DFSubscrState> feed_states) {
		super(idt, feed_states);
	}
	
	public StateOfDataFeeds(ISecIDT idt) {
		this(idt, new Hashtable<>());
		this.states.put(FeedID.SYMBOL_PRIMARY, new DFSubscrState(DFSubscrStatus.NOT_SUBSCR));
		this.states.put(FeedID.SYMBOL_QUOTATIONS, new DFSubscrState(DFSubscrStatus.NOT_SUBSCR));
		this.states.put(FeedID.SYMBOL_ALLTRADES, new DFSubscrState(DFSubscrStatus.NOT_SUBSCR));
		this.states.put(FeedID.SYMBOL_QUOTES, new DFSubscrState(DFSubscrStatus.NOT_SUBSCR));
	}
	
	public ISecIDT getSecIDT() {
		return key;
	}
	
	public boolean isNotFound() {
		return getState(FeedID.SYMBOL_PRIMARY).getStatus() == DFSubscrStatus.NOT_AVAILABLE;
	}

}

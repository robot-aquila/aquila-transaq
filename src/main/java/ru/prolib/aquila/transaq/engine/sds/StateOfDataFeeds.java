package ru.prolib.aquila.transaq.engine.sds;

import java.util.Hashtable;
import java.util.Map;

import ru.prolib.aquila.transaq.remote.ISecIDT;

/**
 * Representation of security data feeds state.
 * <p>
 * Security data feeds aren't directly related with the Aquila's symbol.
 * It is because TRANSAQ/MOEX security codes can point different securities in different time.
 * This class represents state of data feeds for security in terms of TRANSAQ/MOEX
 * and shouldn't be linked with local (AQUILA) symbols or securities.
 */
public class StateOfDataFeeds {
	private final ISecIDT idt;
	private final Map<FeedID, FeedSubscrState> feedStates;
	
	public StateOfDataFeeds(ISecIDT idt, Map<FeedID, FeedSubscrState> feed_states) {
		this.idt = idt;
		this.feedStates = feed_states;
	}
	
	public StateOfDataFeeds(ISecIDT idt) {
		this.idt = idt;
		this.feedStates = new Hashtable<>();
		this.feedStates.put(FeedID.SYMBOL_PRIMARY, new FeedSubscrState(SubscrStatus.NOT_SUBSCR));
		this.feedStates.put(FeedID.SYMBOL_QUOTATIONS, new FeedSubscrState(SubscrStatus.NOT_SUBSCR));
		this.feedStates.put(FeedID.SYMBOL_ALLTRADES, new FeedSubscrState(SubscrStatus.NOT_SUBSCR));
		this.feedStates.put(FeedID.SYMBOL_QUOTES, new FeedSubscrState(SubscrStatus.NOT_SUBSCR));
	}
	
	private FeedSubscrState getState(FeedID feed_id) {
		FeedSubscrState state = feedStates.get(feed_id);
		if ( state == null ) {
			throw new NullPointerException("Feed state not found: " + idt + "#" + feed_id);
		}
		return state;
	}
	
	public ISecIDT getSecIDT() {
		return idt;
	}
	
	public boolean isNotFound() {
		return getState(FeedID.SYMBOL_PRIMARY).getStatus() == SubscrStatus.NOT_AVAILABLE;
	}
	
	public boolean markToSubscribe(FeedID feed_id) {
		FeedSubscrState state = getState(feed_id);
		switch ( state.getStatus() ) {
		case NOT_SUBSCR:
		case PENDING_SUBSCR:
			state.switchTo(SubscrStatus.PENDING_SUBSCR);
			return true;
		case PENDING_UNSUBSCR:
			// Have to switch back to subscribed status.
			// And do nothing with actual feed - it is already subscribed as expected.
			state.switchTo(SubscrStatus.SUBSCR);
			return false;
		case SUBSCR:
			// Already subscribed - nothing to do.
			return false;
		case NOT_AVAILABLE:
			// This data feed is not available.
			// Nothing to do.
			return false;
		default:
			throw new IllegalStateException("Unexpected status: " + state.getStatus());
		}
	}
	
	public boolean markToUnsubscribe(FeedID feed_id) {
		FeedSubscrState state = getState(feed_id);
		switch ( state.getStatus() ) {
		case SUBSCR:
		case PENDING_UNSUBSCR:
			state.switchTo(SubscrStatus.PENDING_UNSUBSCR);
			return true;
		case PENDING_SUBSCR:
			state.switchTo(SubscrStatus.NOT_SUBSCR);
			return false;
		case NOT_SUBSCR:
			return false;
		case NOT_AVAILABLE:
			return false;
		default:
			throw new IllegalStateException("Unexpected status: " + state.getStatus());
		}
	}
	
	public SubscrStatus getFeedStatus(FeedID feed_id) {
		return getState(feed_id).getStatus();
	}
	
	public void setFeedStatus(FeedID feed_id, SubscrStatus new_status) {
		getState(feed_id).switchTo(new_status);
	}

}

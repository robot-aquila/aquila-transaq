package ru.prolib.aquila.transaq.engine.sds;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.remote.TQSecIDT;

public class StateOfDataFeedsTest {
	private StateOfDataFeeds service;
	private  Map<FeedID, FeedSubscrState> feed_states;

	@Before
	public void setUp() throws Exception {
		feed_states = new LinkedHashMap<>();
		feed_states.put(FeedID.SYMBOL_PRIMARY, new FeedSubscrState());
		feed_states.put(FeedID.SYMBOL_QUOTATIONS, new FeedSubscrState());
		feed_states.put(FeedID.SYMBOL_ALLTRADES, new FeedSubscrState());
		feed_states.put(FeedID.SYMBOL_QUOTES, new FeedSubscrState());
		service = new StateOfDataFeeds(new TQSecIDT("SBER", "EQTB"), feed_states);
	}
	
	@Test
	public void testCtor() {
		assertEquals(new TQSecIDT("SBER", "EQTB"), service.getSecIDT());
	}
	
	@Test
	public void testIsNotFound() {
		assertFalse(service.isNotFound());
		feed_states.put(FeedID.SYMBOL_PRIMARY, new FeedSubscrState(SubscrStatus.NOT_AVAILABLE));		
		assertTrue(service.isNotFound());
	}
	
	@Test
	public void testMarkToSubscribe_FromNotSubscr() {
		FeedID feed_id = FeedID.SYMBOL_QUOTATIONS;
		feed_states.put(feed_id, new FeedSubscrState(SubscrStatus.NOT_SUBSCR));
		
		assertTrue(service.markToSubscribe(feed_id));
		
		assertEquals(SubscrStatus.PENDING_SUBSCR, feed_states.get(feed_id).getStatus());
	}

	@Test
	public void testMarkToSubscribe_FromPendingSubscr() {
		FeedID feed_id = FeedID.SYMBOL_QUOTES;
		feed_states.put(feed_id, new FeedSubscrState(SubscrStatus.PENDING_SUBSCR));
		
		assertTrue(service.markToSubscribe(feed_id));
		
		assertEquals(SubscrStatus.PENDING_SUBSCR, feed_states.get(feed_id).getStatus());
	}
	
	@Test
	public void testMarkToSubscribe_FromPendingUnsubscr() {
		FeedID feed_id = FeedID.SYMBOL_PRIMARY;
		feed_states.put(feed_id, new FeedSubscrState(SubscrStatus.PENDING_UNSUBSCR));
		
		assertFalse(service.markToSubscribe(feed_id));
		
		assertEquals(SubscrStatus.SUBSCR, feed_states.get(feed_id).getStatus());
	}
	
	@Test
	public void testMarkToSubscribe_FromSubscr() {
		FeedID feed_id = FeedID.SYMBOL_QUOTATIONS;
		feed_states.put(feed_id, new FeedSubscrState(SubscrStatus.SUBSCR));
		
		assertFalse(service.markToSubscribe(feed_id));
		
		assertEquals(SubscrStatus.SUBSCR, feed_states.get(feed_id).getStatus());
	}
	
	@Test
	public void testMarkToSubscribe_FromNotAvailable() {
		FeedID feed_id = FeedID.SYMBOL_ALLTRADES;
		feed_states.put(feed_id, new FeedSubscrState(SubscrStatus.NOT_AVAILABLE));
		
		assertFalse(service.markToSubscribe(feed_id));
		
		assertEquals(SubscrStatus.NOT_AVAILABLE, feed_states.get(feed_id).getStatus());
	}

	@Test
	public void testMarkToUnsubscribe_FromNotSubscr() {
		FeedID feed_id = FeedID.SYMBOL_QUOTES;
		feed_states.put(feed_id, new FeedSubscrState(SubscrStatus.NOT_SUBSCR));
		
		assertFalse(service.markToUnsubscribe(feed_id));
		
		assertEquals(SubscrStatus.NOT_SUBSCR, feed_states.get(feed_id).getStatus());
	}
	
	@Test
	public void testMarkToUnsubscribe_FromPendingSubscr() {
		FeedID feed_id = FeedID.SYMBOL_PRIMARY;
		feed_states.put(feed_id, new FeedSubscrState(SubscrStatus.PENDING_SUBSCR));
		
		assertFalse(service.markToUnsubscribe(feed_id));
		
		assertEquals(SubscrStatus.NOT_SUBSCR, feed_states.get(feed_id).getStatus());
	}
	
	@Test
	public void testMarkToUnsubscribe_FromPendingUnsubscr() {
		FeedID feed_id = FeedID.SYMBOL_ALLTRADES;
		feed_states.put(feed_id, new FeedSubscrState(SubscrStatus.PENDING_UNSUBSCR));
		
		assertTrue(service.markToUnsubscribe(feed_id));
		
		assertEquals(SubscrStatus.PENDING_UNSUBSCR, feed_states.get(feed_id).getStatus());
	}
	
	@Test
	public void testMarkToUnsubscribe_FromSubscr() {
		FeedID feed_id = FeedID.SYMBOL_PRIMARY;
		feed_states.put(feed_id, new FeedSubscrState(SubscrStatus.SUBSCR));
		
		assertTrue(service.markToUnsubscribe(feed_id));
		
		assertEquals(SubscrStatus.PENDING_UNSUBSCR, feed_states.get(feed_id).getStatus());
	}

	@Test
	public void testMarkToUnsubscribe_FromNotAvailable() {
		FeedID feed_id = FeedID.SYMBOL_QUOTATIONS;
		feed_states.put(feed_id, new FeedSubscrState(SubscrStatus.NOT_AVAILABLE));
		
		assertFalse(service.markToUnsubscribe(feed_id));
		
		assertEquals(SubscrStatus.NOT_AVAILABLE, feed_states.get(feed_id).getStatus());
	}
	
	@Test
	public void testMarkAllNotSubscribed() {
		feed_states.put(FeedID.SYMBOL_PRIMARY, new FeedSubscrState(SubscrStatus.SUBSCR));
		feed_states.put(FeedID.SYMBOL_QUOTATIONS, new FeedSubscrState(SubscrStatus.PENDING_UNSUBSCR));
		feed_states.put(FeedID.SYMBOL_ALLTRADES, new FeedSubscrState(SubscrStatus.PENDING_SUBSCR));
		feed_states.put(FeedID.SYMBOL_QUOTES, new FeedSubscrState(SubscrStatus.NOT_AVAILABLE));
		
		service.markAllNotSubscribed();
		
		assertEquals(SubscrStatus.NOT_SUBSCR, feed_states.get(FeedID.SYMBOL_PRIMARY).getStatus());
		assertEquals(SubscrStatus.NOT_SUBSCR, feed_states.get(FeedID.SYMBOL_QUOTATIONS).getStatus());
		assertEquals(SubscrStatus.NOT_SUBSCR, feed_states.get(FeedID.SYMBOL_ALLTRADES).getStatus());
		assertEquals(SubscrStatus.NOT_AVAILABLE, feed_states.get(FeedID.SYMBOL_QUOTES).getStatus());
	}
	
	@Test
	public void testGetFeedStatus() {
		feed_states.put(FeedID.SYMBOL_PRIMARY, new FeedSubscrState(SubscrStatus.SUBSCR));
		feed_states.put(FeedID.SYMBOL_QUOTATIONS, new FeedSubscrState(SubscrStatus.PENDING_UNSUBSCR));
		feed_states.put(FeedID.SYMBOL_ALLTRADES, new FeedSubscrState(SubscrStatus.PENDING_SUBSCR));
		feed_states.put(FeedID.SYMBOL_QUOTES, new FeedSubscrState(SubscrStatus.NOT_AVAILABLE));
		
		assertEquals(SubscrStatus.SUBSCR, service.getFeedStatus(FeedID.SYMBOL_PRIMARY));
		assertEquals(SubscrStatus.PENDING_UNSUBSCR, service.getFeedStatus(FeedID.SYMBOL_QUOTATIONS));
		assertEquals(SubscrStatus.PENDING_SUBSCR, service.getFeedStatus(FeedID.SYMBOL_ALLTRADES));
		assertEquals(SubscrStatus.NOT_AVAILABLE, service.getFeedStatus(FeedID.SYMBOL_QUOTES));
	}

	@Test
	public void testSetFeedStatus() {
		assertNotEquals(SubscrStatus.PENDING_SUBSCR, feed_states.get(FeedID.SYMBOL_PRIMARY).getStatus());
		assertNotEquals(SubscrStatus.NOT_AVAILABLE, feed_states.get(FeedID.SYMBOL_QUOTATIONS).getStatus());
		
		service.setFeedStatus(FeedID.SYMBOL_PRIMARY, SubscrStatus.PENDING_SUBSCR);
		service.setFeedStatus(FeedID.SYMBOL_QUOTATIONS, SubscrStatus.NOT_AVAILABLE);
		
		assertEquals(SubscrStatus.PENDING_SUBSCR, feed_states.get(FeedID.SYMBOL_PRIMARY).getStatus());
		assertEquals(SubscrStatus.NOT_AVAILABLE, feed_states.get(FeedID.SYMBOL_QUOTATIONS).getStatus());
	}

}

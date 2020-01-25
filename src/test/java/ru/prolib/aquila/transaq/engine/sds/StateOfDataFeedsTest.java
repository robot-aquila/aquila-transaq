package ru.prolib.aquila.transaq.engine.sds;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.data.DFSubscrState;
import ru.prolib.aquila.data.DFSubscrStatus;
import ru.prolib.aquila.transaq.remote.TQSecIDT;

public class StateOfDataFeedsTest {
	private StateOfDataFeeds service;
	private  Map<FeedID, DFSubscrState> feed_states;

	@Before
	public void setUp() throws Exception {
		feed_states = new LinkedHashMap<>();
		feed_states.put(FeedID.SYMBOL_PRIMARY, new DFSubscrState());
		feed_states.put(FeedID.SYMBOL_QUOTATIONS, new DFSubscrState());
		feed_states.put(FeedID.SYMBOL_ALLTRADES, new DFSubscrState());
		feed_states.put(FeedID.SYMBOL_QUOTES, new DFSubscrState());
		service = new StateOfDataFeeds(new TQSecIDT("SBER", "EQTB"), feed_states);
	}
	
	@Test
	public void testCtor() {
		assertEquals(new TQSecIDT("SBER", "EQTB"), service.getSecIDT());
	}
	
	@Test
	public void testIsNotFound() {
		assertFalse(service.isNotFound());
		feed_states.put(FeedID.SYMBOL_PRIMARY, new DFSubscrState(DFSubscrStatus.NOT_AVAILABLE));		
		assertTrue(service.isNotFound());
	}
	
	@Test
	public void testMarkToSubscribe_FromNotSubscr() {
		FeedID feed_id = FeedID.SYMBOL_QUOTATIONS;
		feed_states.put(feed_id, new DFSubscrState(DFSubscrStatus.NOT_SUBSCR));
		
		assertTrue(service.markToSubscribe(feed_id));
		
		assertEquals(DFSubscrStatus.PENDING_SUBSCR, feed_states.get(feed_id).getStatus());
	}

	@Test
	public void testMarkToSubscribe_FromPendingSubscr() {
		FeedID feed_id = FeedID.SYMBOL_QUOTES;
		feed_states.put(feed_id, new DFSubscrState(DFSubscrStatus.PENDING_SUBSCR));
		
		assertTrue(service.markToSubscribe(feed_id));
		
		assertEquals(DFSubscrStatus.PENDING_SUBSCR, feed_states.get(feed_id).getStatus());
	}
	
	@Test
	public void testMarkToSubscribe_FromPendingUnsubscr() {
		FeedID feed_id = FeedID.SYMBOL_PRIMARY;
		feed_states.put(feed_id, new DFSubscrState(DFSubscrStatus.PENDING_UNSUBSCR));
		
		assertFalse(service.markToSubscribe(feed_id));
		
		assertEquals(DFSubscrStatus.SUBSCR, feed_states.get(feed_id).getStatus());
	}
	
	@Test
	public void testMarkToSubscribe_FromSubscr() {
		FeedID feed_id = FeedID.SYMBOL_QUOTATIONS;
		feed_states.put(feed_id, new DFSubscrState(DFSubscrStatus.SUBSCR));
		
		assertFalse(service.markToSubscribe(feed_id));
		
		assertEquals(DFSubscrStatus.SUBSCR, feed_states.get(feed_id).getStatus());
	}
	
	@Test
	public void testMarkToSubscribe_FromNotAvailable() {
		FeedID feed_id = FeedID.SYMBOL_ALLTRADES;
		feed_states.put(feed_id, new DFSubscrState(DFSubscrStatus.NOT_AVAILABLE));
		
		assertFalse(service.markToSubscribe(feed_id));
		
		assertEquals(DFSubscrStatus.NOT_AVAILABLE, feed_states.get(feed_id).getStatus());
	}

	@Test
	public void testMarkToUnsubscribe_FromNotSubscr() {
		FeedID feed_id = FeedID.SYMBOL_QUOTES;
		feed_states.put(feed_id, new DFSubscrState(DFSubscrStatus.NOT_SUBSCR));
		
		assertFalse(service.markToUnsubscribe(feed_id));
		
		assertEquals(DFSubscrStatus.NOT_SUBSCR, feed_states.get(feed_id).getStatus());
	}
	
	@Test
	public void testMarkToUnsubscribe_FromPendingSubscr() {
		FeedID feed_id = FeedID.SYMBOL_PRIMARY;
		feed_states.put(feed_id, new DFSubscrState(DFSubscrStatus.PENDING_SUBSCR));
		
		assertFalse(service.markToUnsubscribe(feed_id));
		
		assertEquals(DFSubscrStatus.NOT_SUBSCR, feed_states.get(feed_id).getStatus());
	}
	
	@Test
	public void testMarkToUnsubscribe_FromPendingUnsubscr() {
		FeedID feed_id = FeedID.SYMBOL_ALLTRADES;
		feed_states.put(feed_id, new DFSubscrState(DFSubscrStatus.PENDING_UNSUBSCR));
		
		assertTrue(service.markToUnsubscribe(feed_id));
		
		assertEquals(DFSubscrStatus.PENDING_UNSUBSCR, feed_states.get(feed_id).getStatus());
	}
	
	@Test
	public void testMarkToUnsubscribe_FromSubscr() {
		FeedID feed_id = FeedID.SYMBOL_PRIMARY;
		feed_states.put(feed_id, new DFSubscrState(DFSubscrStatus.SUBSCR));
		
		assertTrue(service.markToUnsubscribe(feed_id));
		
		assertEquals(DFSubscrStatus.PENDING_UNSUBSCR, feed_states.get(feed_id).getStatus());
	}

	@Test
	public void testMarkToUnsubscribe_FromNotAvailable() {
		FeedID feed_id = FeedID.SYMBOL_QUOTATIONS;
		feed_states.put(feed_id, new DFSubscrState(DFSubscrStatus.NOT_AVAILABLE));
		
		assertFalse(service.markToUnsubscribe(feed_id));
		
		assertEquals(DFSubscrStatus.NOT_AVAILABLE, feed_states.get(feed_id).getStatus());
	}
	
	@Test
	public void testMarkAllNotSubscribed() {
		feed_states.put(FeedID.SYMBOL_PRIMARY, new DFSubscrState(DFSubscrStatus.SUBSCR));
		feed_states.put(FeedID.SYMBOL_QUOTATIONS, new DFSubscrState(DFSubscrStatus.PENDING_UNSUBSCR));
		feed_states.put(FeedID.SYMBOL_ALLTRADES, new DFSubscrState(DFSubscrStatus.PENDING_SUBSCR));
		feed_states.put(FeedID.SYMBOL_QUOTES, new DFSubscrState(DFSubscrStatus.NOT_AVAILABLE));
		
		service.markAllNotSubscribed();
		
		assertEquals(DFSubscrStatus.NOT_SUBSCR, feed_states.get(FeedID.SYMBOL_PRIMARY).getStatus());
		assertEquals(DFSubscrStatus.NOT_SUBSCR, feed_states.get(FeedID.SYMBOL_QUOTATIONS).getStatus());
		assertEquals(DFSubscrStatus.NOT_SUBSCR, feed_states.get(FeedID.SYMBOL_ALLTRADES).getStatus());
		assertEquals(DFSubscrStatus.NOT_AVAILABLE, feed_states.get(FeedID.SYMBOL_QUOTES).getStatus());
	}
	
	@Test
	public void testGetFeedStatus() {
		feed_states.put(FeedID.SYMBOL_PRIMARY, new DFSubscrState(DFSubscrStatus.SUBSCR));
		feed_states.put(FeedID.SYMBOL_QUOTATIONS, new DFSubscrState(DFSubscrStatus.PENDING_UNSUBSCR));
		feed_states.put(FeedID.SYMBOL_ALLTRADES, new DFSubscrState(DFSubscrStatus.PENDING_SUBSCR));
		feed_states.put(FeedID.SYMBOL_QUOTES, new DFSubscrState(DFSubscrStatus.NOT_AVAILABLE));
		
		assertEquals(DFSubscrStatus.SUBSCR, service.getFeedStatus(FeedID.SYMBOL_PRIMARY));
		assertEquals(DFSubscrStatus.PENDING_UNSUBSCR, service.getFeedStatus(FeedID.SYMBOL_QUOTATIONS));
		assertEquals(DFSubscrStatus.PENDING_SUBSCR, service.getFeedStatus(FeedID.SYMBOL_ALLTRADES));
		assertEquals(DFSubscrStatus.NOT_AVAILABLE, service.getFeedStatus(FeedID.SYMBOL_QUOTES));
	}

	@Test
	public void testSetFeedStatus() {
		assertNotEquals(DFSubscrStatus.PENDING_SUBSCR, feed_states.get(FeedID.SYMBOL_PRIMARY).getStatus());
		assertNotEquals(DFSubscrStatus.NOT_AVAILABLE, feed_states.get(FeedID.SYMBOL_QUOTATIONS).getStatus());
		
		service.setFeedStatus(FeedID.SYMBOL_PRIMARY, DFSubscrStatus.PENDING_SUBSCR);
		service.setFeedStatus(FeedID.SYMBOL_QUOTATIONS, DFSubscrStatus.NOT_AVAILABLE);
		
		assertEquals(DFSubscrStatus.PENDING_SUBSCR, feed_states.get(FeedID.SYMBOL_PRIMARY).getStatus());
		assertEquals(DFSubscrStatus.NOT_AVAILABLE, feed_states.get(FeedID.SYMBOL_QUOTATIONS).getStatus());
	}

}

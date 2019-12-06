package ru.prolib.aquila.transaq.engine.sds;

import static org.junit.Assert.*;
import static ru.prolib.aquila.transaq.engine.sds.SubscrStatus.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.engine.sds.FeedSubscrState;
import ru.prolib.aquila.transaq.engine.sds.SubscrStatus;

public class FeedSubscrStateTest {
	private FeedSubscrState service;

	@Before
	public void setUp() throws Exception {
		service = new FeedSubscrState();
	}
	
	private static List<SubscrStatus> tol(SubscrStatus... to_status) {
		List<SubscrStatus> list = new ArrayList<>();
		for ( SubscrStatus status : to_status ) {
			list.add(status);
		}
		return list;
	}
	
	private void testAllowedCombs(SubscrStatus from_status, List<SubscrStatus> to_status) {
		for ( SubscrStatus status : to_status ) {
			//System.out.println("ALLOWED? From " + from_status + " To " + status);
			service = new FeedSubscrState(from_status);
			try {
				service.switchTo(status);
				assertEquals(status, service.getStatus());
			} catch ( IllegalStateException e ) {
				fail("Expected without exceptions while switching from " + from_status + " to " + status);
			}
		}
	}
	
	private void testProhibitedCombs(SubscrStatus from_status, List<SubscrStatus> to_status) {
		for ( SubscrStatus status : to_status ) {
			//System.out.println("PROHIBITED? From " + from_status + " To " + status);
			service = new FeedSubscrState(from_status);
			try {
				service.switchTo(status);
				fail("Expected expection while switching from " + from_status + " to " + status);
			} catch ( IllegalStateException e ) {
				assertEquals("Cannot switch from " + from_status + " to " + status, e.getMessage());
				assertEquals(from_status, service.getStatus());
			}
		}
	}
	
	private void testSwitchings(SubscrStatus from_status, List<SubscrStatus> allowed_to) {
		List<SubscrStatus> prohibited_to = new ArrayList<>();
		for ( SubscrStatus status : tol(SubscrStatus.values()) ) {
			if ( ! allowed_to.contains(status) ) {
				prohibited_to.add(status);
			}
		}
		testAllowedCombs(from_status, allowed_to);
		testProhibitedCombs(from_status, prohibited_to);
	}
	
	@Test
	public void testCtor1() {
		service = new FeedSubscrState(SubscrStatus.PENDING_SUBSCR);
		assertEquals(SubscrStatus.PENDING_SUBSCR, service.getStatus());
	}
	
	@Test
	public void testCtor0_DefaultStatus() {
		assertEquals(SubscrStatus.NOT_SUBSCR, service.getStatus());
	}
	
	@Test
	public void testSwitchTo_() {
		testSwitchings(NOT_SUBSCR,		tol(NOT_SUBSCR, PENDING_SUBSCR, NOT_AVAILABLE));
		testSwitchings(PENDING_SUBSCR,	tol(NOT_SUBSCR, PENDING_SUBSCR, SUBSCR, NOT_AVAILABLE));
		testSwitchings(SUBSCR,			tol(NOT_SUBSCR, SUBSCR, PENDING_UNSUBSCR, NOT_AVAILABLE));
		testSwitchings(PENDING_UNSUBSCR,tol(NOT_SUBSCR, SUBSCR, PENDING_UNSUBSCR, NOT_AVAILABLE));
		testSwitchings(NOT_AVAILABLE,	tol(NOT_AVAILABLE));
	}

}

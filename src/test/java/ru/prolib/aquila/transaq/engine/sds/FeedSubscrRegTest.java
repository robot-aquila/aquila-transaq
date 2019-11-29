package ru.prolib.aquila.transaq.engine.sds;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.engine.sds.FeedSubscrReg;
import ru.prolib.aquila.transaq.engine.sds.FeedSubscrStatus;

public class FeedSubscrRegTest {
	private FeedSubscrReg service;

	@Before
	public void setUp() throws Exception {
		service = new FeedSubscrReg();
	}
	
	@Test
	public void testDefaultStatus() {
		assertEquals(FeedSubscrStatus.NOT_SUBSCR, service.getStatus());
	}
	
	@Test
	public void testSwitchTo_FromNotSubscr_Allowed() {
		service = new FeedSubscrReg(FeedSubscrStatus.NOT_SUBSCR);
		
		service.switchTo(FeedSubscrStatus.PENDING_SUBSCR);
		
		assertEquals(FeedSubscrStatus.PENDING_SUBSCR, service.getStatus());
	}
	
	@Test
	public void testSwitchTo_FromNotSubscr_Rejected() {
		List<FeedSubscrStatus> to_reject = new ArrayList<>();
		to_reject.add(FeedSubscrStatus.NOT_SUBSCR);
		to_reject.add(FeedSubscrStatus.PENDING_UNSUBSCR);
		to_reject.add(FeedSubscrStatus.SUBSCR);
		for ( FeedSubscrStatus status : to_reject ) {
			try {
				service = new FeedSubscrReg(FeedSubscrStatus.NOT_SUBSCR);
				service.switchTo(status);
				fail("Expected: " + IllegalStateException.class.getSimpleName());
			} catch ( IllegalStateException e ) {
				assertEquals("Cannot switch from NOT_SUBSCR to " + status, e.getMessage());
				assertEquals(FeedSubscrStatus.NOT_SUBSCR, service.getStatus());
			}
		}
	}
	
	@Test
	public void testSwitchTo_FromSubscr_Allowed() {
		service = new FeedSubscrReg(FeedSubscrStatus.SUBSCR);
		
		service.switchTo(FeedSubscrStatus.PENDING_UNSUBSCR);
		
		assertEquals(FeedSubscrStatus.PENDING_UNSUBSCR, service.getStatus());
	}
	
	@Test
	public void testSwitchTo_FromSubscr_Rejected() {
		List<FeedSubscrStatus> to_reject = new ArrayList<>();
		to_reject.add(FeedSubscrStatus.NOT_SUBSCR);
		to_reject.add(FeedSubscrStatus.PENDING_SUBSCR);
		to_reject.add(FeedSubscrStatus.SUBSCR);
		for ( FeedSubscrStatus status : to_reject ) {
			try {
				service = new FeedSubscrReg(FeedSubscrStatus.SUBSCR);
				service.switchTo(status);
				fail("Expected: " + IllegalStateException.class.getSimpleName());
			} catch ( IllegalStateException e ) {
				assertEquals("Cannot switch from SUBSCR to " + status, e.getMessage());
				assertEquals(FeedSubscrStatus.SUBSCR, service.getStatus());
			}
		}
	}
	
	@Test
	public void testSwitchTo_FromPendingSubscr_ToNotSubscr() {
		service = new FeedSubscrReg(FeedSubscrStatus.PENDING_SUBSCR);
		
		service.switchTo(FeedSubscrStatus.NOT_SUBSCR);
		
		assertEquals(FeedSubscrStatus.NOT_SUBSCR, service.getStatus());
	}
	
	@Test
	public void testSwitchTo_FromPendingSubscr_ToSubscr() {
		service = new FeedSubscrReg(FeedSubscrStatus.PENDING_SUBSCR);
		
		service.switchTo(FeedSubscrStatus.SUBSCR);
		
		assertEquals(FeedSubscrStatus.SUBSCR, service.getStatus());
	}
	
	@Test
	public void testSwitchTo_FromPendingSubscr_Rejected() {
		List<FeedSubscrStatus> to_reject = new ArrayList<>();
		to_reject.add(FeedSubscrStatus.PENDING_SUBSCR);
		to_reject.add(FeedSubscrStatus.PENDING_UNSUBSCR);
		for ( FeedSubscrStatus status : to_reject ) {
			try {
				service = new FeedSubscrReg(FeedSubscrStatus.PENDING_SUBSCR);
				service.switchTo(status);
				fail("Expected: " + IllegalStateException.class.getSimpleName());
			} catch ( IllegalStateException e ) {
				assertEquals("Cannot switch from PENDING_SUBSCR to " + status, e.getMessage());
				assertEquals(FeedSubscrStatus.PENDING_SUBSCR, service.getStatus());
			}
		}
	}
	
	@Test
	public void testSwitchTo_FromPendingUnsubscr_ToNotSubscr() {
		service = new FeedSubscrReg(FeedSubscrStatus.PENDING_UNSUBSCR);
		
		service.switchTo(FeedSubscrStatus.NOT_SUBSCR);
		
		assertEquals(FeedSubscrStatus.NOT_SUBSCR, service.getStatus());
	}
	
	@Test
	public void testSwitchTo_FromPendingUnsubscr_ToSubscr() {
		service = new FeedSubscrReg(FeedSubscrStatus.PENDING_UNSUBSCR);
		
		service.switchTo(FeedSubscrStatus.SUBSCR);
		
		assertEquals(FeedSubscrStatus.SUBSCR, service.getStatus());
	}
	
	@Test
	public void testSwitchTo_FromPendingUnsubscr_Rejected() {
		List<FeedSubscrStatus> to_reject = new ArrayList<>();
		to_reject.add(FeedSubscrStatus.PENDING_SUBSCR);
		to_reject.add(FeedSubscrStatus.PENDING_UNSUBSCR);
		for ( FeedSubscrStatus status : to_reject ) {
			try {
				service = new FeedSubscrReg(FeedSubscrStatus.PENDING_UNSUBSCR);
				service.switchTo(status);
				fail("Expected: " + IllegalStateException.class.getSimpleName());
			} catch ( IllegalStateException e ) {
				assertEquals("Cannot switch from PENDING_UNSUBSCR to " + status, e.getMessage());
				assertEquals(FeedSubscrStatus.PENDING_UNSUBSCR, service.getStatus());
			}
		}
	}

}

package ru.prolib.aquila.transaq.engine.sds;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.remote.TQSecIDT;

public class StateOfDataFeedsFactoryTest {
	private StateOfDataFeedsFactory service;

	@Before
	public void setUp() throws Exception {
		service = new StateOfDataFeedsFactory();
	}

	@Test
	public void testProduce() {
		StateOfDataFeeds actual = service.produce(new TQSecIDT("SBER", "TQBR"));
		
		assertNotNull(actual);
		assertEquals(new TQSecIDT("SBER", "TQBR"), actual.getSecIDT());
		assertEquals(SubscrStatus.NOT_SUBSCR, actual.getFeedStatus(FeedID.SYMBOL_PRIMARY));
		assertEquals(SubscrStatus.NOT_SUBSCR, actual.getFeedStatus(FeedID.SYMBOL_QUOTATIONS));
		assertEquals(SubscrStatus.NOT_SUBSCR, actual.getFeedStatus(FeedID.SYMBOL_ALLTRADES));
		assertEquals(SubscrStatus.NOT_SUBSCR, actual.getFeedStatus(FeedID.SYMBOL_QUOTES));
	}

}

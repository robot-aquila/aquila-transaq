package ru.prolib.aquila.transaq.engine;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class CmdSubscrSymbolTest {
	private static Symbol symbol1 = new Symbol("foo"), symbol2 = new Symbol("bar");
	private CmdSubscrSymbol service;

	@Before
	public void setUp() throws Exception {
		service = new CmdSubscrSymbol(symbol1, MDLevel.L1_BBO);
	}
	
	@Test
	public void testGetters() {
		assertEquals(CmdType.SUBSCR_SYMBOL, service.getType());
		assertFalse(service.getResult().isDone());
		assertEquals(symbol1, service.getSymbol());
		assertEquals(MDLevel.L1_BBO, service.getLevel());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(187822101, 984511)
				.append(CmdType.SUBSCR_SYMBOL)
				.append(symbol1)
				.append(MDLevel.L1_BBO)
				.build();
		
		assertEquals(expected, service.hashCode());
	}

	@Test
	public void testEquals() {
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
		assertFalse(service.equals(new CmdSubscrSymbol(symbol2, MDLevel.L1_BBO)));
		assertFalse(service.equals(new CmdSubscrSymbol(symbol1, MDLevel.L0)));
		assertFalse(service.equals(new CmdSubscrSymbol(symbol2, MDLevel.L0)));
		assertTrue(service.equals(service));
		assertTrue(service.equals(new CmdSubscrSymbol(symbol1, MDLevel.L1_BBO)));
	}

}

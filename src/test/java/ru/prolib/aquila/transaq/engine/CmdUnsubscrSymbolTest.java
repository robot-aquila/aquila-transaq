package ru.prolib.aquila.transaq.engine;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class CmdUnsubscrSymbolTest {
	private static Symbol symbol1 = new Symbol("foo"), symbol2 = new Symbol("bar");
	private CmdUnsubscrSymbol service;

	@Before
	public void setUp() throws Exception {
		service = new CmdUnsubscrSymbol(symbol1, MDLevel.L2);
	}
	
	@Test
	public void testGetters() {
		assertEquals(CmdType.UNSUBSCR_SYMBOL, service.getType());
		assertFalse(service.getResult().isDone());
		assertEquals(symbol1, service.getSymbol());
		assertEquals(MDLevel.L2, service.getLevel());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(91234773, 9005)
				.append(CmdType.UNSUBSCR_SYMBOL)
				.append(symbol1)
				.append(MDLevel.L2)
				.build();
		
		assertEquals(expected, service.hashCode());
	}

	@Test
	public void testEquals() {
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
		assertFalse(service.equals(new CmdUnsubscrSymbol(symbol2, MDLevel.L2)));
		assertFalse(service.equals(new CmdUnsubscrSymbol(symbol1, MDLevel.L0)));
		assertFalse(service.equals(new CmdUnsubscrSymbol(symbol2, MDLevel.L0)));
		assertTrue(service.equals(service));
		assertTrue(service.equals(new CmdUnsubscrSymbol(symbol1, MDLevel.L2)));
	}

}

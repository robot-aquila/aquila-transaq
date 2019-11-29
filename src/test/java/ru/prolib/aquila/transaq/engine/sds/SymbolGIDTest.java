package ru.prolib.aquila.transaq.engine.sds;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

public class SymbolGIDTest {
	private SymbolGID service;

	@Before
	public void setUp() throws Exception {
		service = new SymbolGID("RTS-12.19", 4);
	}
	
	@Test
	public void testGetters() {
		assertEquals("RTS-12.19", service.getTicker());
		assertEquals(4, service.getMarketID());
	}
	
	@Test
	public void testToString() {
		String expected = "SymbolGID[ticker=RTS-12.19,marketID=4]";
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(12377015, 507)
				.append("RTS-12.19")
				.append(4)
				.build();
		
		assertEquals(expected, service.hashCode());
	}

	@Test
	public void testEquals() {
		assertTrue(service.equals(service));
		assertTrue(service.equals(new SymbolGID("RTS-12.19", 4)));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
		assertFalse(service.equals(new SymbolTID("RTS-12.19", 4, "FUT")));
		assertFalse(service.equals(new SymbolGID("RTS-12.19", 7)));
		assertFalse(service.equals(new SymbolGID("Si-12.19", 4)));
		assertFalse(service.equals(new SymbolGID("Si-12.19", 7)));
	}

}

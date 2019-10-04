package ru.prolib.aquila.transaq.impl;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TQSecID2Test {
	@Rule
	public ExpectedException eex = ExpectedException.none();
	private TQSecID2 service;

	@Before
	public void setUp() throws Exception {
		service = new TQSecID2("AAPL", "TQBR");
	}
	
	@Test
	public void testCtor2_ThrowsIfSecCodeNull() {
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Invalid security code: [null]");
		
		new TQSecID2(null, "TQBR");
	}
	
	@Test
	public void testCtor2_ThrowsIfSecCodeZeroLength() {
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Invalid security code: []");
		
		new TQSecID2("", "TQBR");
	}
	
	@Test
	public void testCtor2_ThrowsIfBoardCodeNull() {
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Invalid board code: [null]");
		
		new TQSecID2("AAPL", null);
	}
	
	@Test
	public void testCtor2_ThrowsIfBoardCodeZeroLength() {
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Invalid board code: []");
		
		new TQSecID2("AAPL", "");
	}
	
	@Test
	public void testCtor() {
		assertEquals("AAPL", service.getSecCode());
		assertEquals("TQBR", service.getBoardCode());
	}
	
	@Test
	public void testToString() {
		String expected = "TQSecID2[secCode=AAPL,boardCode=TQBR]";
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(178624381, 95)
				.append("AAPL")
				.append("TQBR")
				.build();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		assertTrue(service.equals(new TQSecID2("AAPL", "TQBR")));
		assertFalse(service.equals(new TQSecID2("MSFT", "TQBR")));
		assertFalse(service.equals(new TQSecID2("AAPL", "EQBR")));
		assertFalse(service.equals(new TQSecID2("MSFT", "EQBR")));
	}

}

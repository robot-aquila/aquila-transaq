package ru.prolib.aquila.transaq.remote;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.instanceOf;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.transaq.remote.TQSecIDT;

public class TQSecIDTTest {
	@Rule
	public ExpectedException eex = ExpectedException.none();
	private TQSecIDT service;

	@Before
	public void setUp() throws Exception {
		service = new TQSecIDT("AAPL", "TQBR");
	}
	
	@Test
	public void testISA() {
		assertThat(service, instanceOf(ISecIDT.class));
	}
	
	@Test
	public void testCtor3_ThrowsIfSecCodeNull() {
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Invalid security code: null");
		
		new TQSecIDT(null, "TQBR");
	}
	
	@Test
	public void testCtor3_ThrowsIfSecCodeZeroLength() {
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Invalid security code: ");
		
		new TQSecIDT("", "TQBR");
	}
	
	@Test
	public void testCtor3_ThrowsIfBoardCodeNull() {
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Invalid board code: null");
		
		new TQSecIDT("AAPL", null);
	}
	
	@Test
	public void testCtor3_ThrowsIfBoardCodeZeroLength() {
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Invalid board code: ");
		
		new TQSecIDT("AAPL", "");
	}
	
	@Test
	public void testGetters() {
		assertEquals("AAPL", service.getSecCode());
		assertEquals("TQBR", service.getBoardCode());
	}
	
	@Test
	public void testToString() {
		String expected = "TQSecIDT[secCode=AAPL,boardCode=TQBR]";
		
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
		Variant<String> vSec = new Variant<>("AAPL", "RTS-12.19");
		Variant<String> vBrd = new Variant<>(vSec, "TQBR", "FUT");
		Variant<?> iterator = vBrd;
		int found_cnt = 0;
		TQSecIDT x, found = null;
		do {
			x = new TQSecIDT(vSec.get(), vBrd.get());
			if ( service.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals("AAPL", found.getSecCode());
		assertEquals("TQBR", found.getBoardCode());
	}

}

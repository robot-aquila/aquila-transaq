package ru.prolib.aquila.transaq.engine.sds;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class SymbolTIDTest {
	private SymbolTID service;

	@Before
	public void setUp() throws Exception {
		service = new SymbolTID("RTS-12.19", 4, "FUT");
	}
	
	@Test
	public void testGetters() {
		assertEquals("RTS-12.19", service.getTicker());
		assertEquals(4, service.getMarketID());
		assertEquals("FUT", service.getBoard());
	}
	
	@Test
	public void testToString() {
		String expected = "SymbolTID[board=FUT,ticker=RTS-12.19,marketID=4]";
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(903115, 1207)
				.append("RTS-12.19")
				.append(4)
				.append("FUT")
				.build();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(this));
		assertFalse(service.equals(null));
		assertFalse(service.equals(new SymbolGID("RTS-12.19", 4)));
	}

	@Test
	public void testEquals() {
		Variant<String> vTkr = new Variant<>("RTS-12.19", "Si-12.19");
		Variant<Integer> vMkt = new Variant<>(vTkr, 4, 7);
		Variant<String> vBrd = new Variant<>(vMkt, "FUT", "EQTB");
		Variant<?> iterator = vBrd;
		int found_cnt = 0;
		SymbolTID x, found = null;
		do {
			x = new SymbolTID(vTkr.get(), vMkt.get(), vBrd.get());
			if ( service.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals("RTS-12.19", found.getTicker());
		assertEquals(4, found.getMarketID());
		assertEquals("FUT", found.getBoard());
	}

}

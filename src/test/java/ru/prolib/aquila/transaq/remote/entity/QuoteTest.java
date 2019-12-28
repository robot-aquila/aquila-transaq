package ru.prolib.aquila.transaq.remote.entity;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.transaq.remote.ISecIDT;
import ru.prolib.aquila.transaq.remote.TQSecIDT;

public class QuoteTest {
	private Quote service;

	@Before
	public void setUp() throws Exception {
		service = new Quote(new TQSecIDT("GAZP", "TQBR"), of("1024.012", "ZUZUKA"), 5L, 10L, 25L);
	}
	
	@Test
	public void testGetters() {
		assertEquals(new TQSecIDT("GAZP", "TQBR"), service.getID());
		assertEquals(of("1024.012", "ZUZUKA"), service.getPrice());
		assertEquals(Long.valueOf( 5), service.getYield());
		assertEquals(Long.valueOf(10), service.getBuy());
		assertEquals(Long.valueOf(25), service.getSell());
	}
	
	@Test
	public void testToString() {
		String expected = "Quote[secCode=GAZP,boardCode=TQBR,price=1024.012 ZUZUKA,yield=5,buy=10,sell=25]";
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(2000981, 5043)
				.append(new TQSecIDT("GAZP", "TQBR"))
				.append(of("1024.012", "ZUZUKA"))
				.append(Integer.valueOf( 5))
				.append(Integer.valueOf(10))
				.append(Integer.valueOf(25))
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
		Variant<ISecIDT> vID = new Variant<ISecIDT>(new TQSecIDT("GAZP", "TQBR"), new TQSecIDT("SIH0", "FUT"));
		Variant<CDecimal> vPr = new Variant<CDecimal>(vID)
				.add(of("1024.012", "ZUZUKA"))
				.add(of("1024.012", "UPS"))
				.add(of("1024.012"))
				.add(of("10.52"));
		Variant<Long> vYld = new Variant<>(vPr,   5L, 12L);
		Variant<Long> vBuy = new Variant<>(vYld, 10L, 85L);
		Variant<Long> vSel = new Variant<>(vBuy, 25L, 99L);
		Variant<?> iterator = vSel;
		int found_cnt = 0;
		Quote x, found = null;
		do {
			x = new Quote(vID.get(), vPr.get(), vYld.get(), vBuy.get(), vSel.get());
			if ( service.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals(new TQSecIDT("GAZP", "TQBR"), found.getID());
		assertEquals(of("1024.012", "ZUZUKA"), found.getPrice());
		assertEquals(Long.valueOf( 5), found.getYield());
		assertEquals(Long.valueOf(10), found.getBuy());
		assertEquals(Long.valueOf(25), found.getSell());
	}

}

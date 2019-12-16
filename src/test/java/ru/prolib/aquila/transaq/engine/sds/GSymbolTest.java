package ru.prolib.aquila.transaq.engine.sds;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
import ru.prolib.aquila.core.utils.Variant;

public class GSymbolTest {
	@Rule
	public ExpectedException eex = ExpectedException.none();
	private GSymbol service;

	@Before
	public void setUp() throws Exception {
		service = new GSymbol("RTS-12.19", "FORTS", "RUB", SymbolType.FUTURES);
	}
	
	@Test
	public void testGetters() {
		assertEquals("RTS-12.19", service.getCode());
		assertEquals("FORTS", service.getMarketCode());
		assertEquals("FORTS", service.getExchangeID());
		assertEquals("RUB", service.getCurrencyCode());
		assertEquals(SymbolType.FUTURES, service.getType());
	}
	
	@Test
	public void testCtor4_ThrowsIfCodeIsNull() {
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Security code must be not null");
		
		new GSymbol(null, "FORTS", "RUB", SymbolType.FUTURES);
	}
	
	@Test
	public void testCtor4_ThrowsIfMarketCodeIsNull() {
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Market code must be not null");
		
		new GSymbol("RTS-12.19", null, "RUB", SymbolType.FUTURES);
	}
	
	@Test
	public void testCtor4_ThrowsIfCurrencyCodeIsNull() {
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Currency code must be not null");
		
		new GSymbol("RTS-12.19", "FORTS", null, SymbolType.FUTURES);
	}

	@Test
	public void testCtor4_ThrowsIfTypeIsNull() {
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Type must be not null");
		
		new GSymbol("RTS-12.19", "FORTS", "RUB", null);
	}

	@Test
	public void testToString() {
		String expected = "F:RTS-12.19@FORTS:RUB";
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder()
				.append("RTS-12.19")
				.append("FORTS")
				.append("RUB")
				.append("F")
				.build();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(new Symbol(
				service.getCode(),
				service.getExchangeID(),
				service.getCurrencyCode(),
				service.getType()
			)));
		assertFalse(service.equals(new TSymbol(
				service.getCode(),
				service.getExchangeID(),
				service.getCurrencyCode(),
				service.getType()
			)));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals_SpeciaCases2() {
		assertTrue(service.equals(new GSymbol("RTS-12.19", "FORTS", "RUB", SymbolType.FUTURES)));
		assertFalse(service.equals(new TSymbol("RTS-12.19", "FORTS", "RUB", SymbolType.FUTURES)));
	}

	@Test
	public void testEquals() {
		Variant<String> vTkr = new Variant<>("RTS-12.19", "AAPL");
		Variant<String> vMkt = new Variant<>(vTkr, "FORTS", "NASDAQ");
		Variant<String> vCur = new Variant<>(vMkt, "RUB", "USD");
		Variant<SymbolType> vTyp = new Variant<>(vCur, SymbolType.FUTURES, SymbolType.STOCK);
		Variant<?> iterator = vTyp;
		int found_cnt = 0;
		GSymbol x, found = null;
		do {
			x = new GSymbol(vTkr.get(), vMkt.get(), vCur.get(), vTyp.get());
			if ( service.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals("RTS-12.19", found.getCode());
		assertEquals("FORTS", found.getMarketCode());
		assertEquals("RUB", found.getCurrencyCode());
		assertEquals(SymbolType.FUTURES, found.getType());
	}

}

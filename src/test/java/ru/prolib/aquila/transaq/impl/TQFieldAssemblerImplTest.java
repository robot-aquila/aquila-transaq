package ru.prolib.aquila.transaq.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.transaq.entity.Market;
import ru.prolib.aquila.transaq.entity.SecType;

public class TQFieldAssemblerImplTest {
	private TQDirectory dir;
	private TQFieldAssemblerImpl service;

	@Before
	public void setUp() throws Exception {
		dir = new TQDirectoryImpl();
		service = new TQFieldAssemblerImpl(dir);

		List<Market> markets = new ArrayList<>();
		markets.add(new Market(0, "foo"));
		markets.add(new Market(1, "bar"));
		markets.add(new Market(2, "zoo"));
		dir.updateMarkets(markets);
	}

	@Test
	public void testToSymbol() {
		assertEquals(new Symbol("U:boo@bar:RUB"), service.toSymbol(new TQSecID_F("boo", 1, "BAA", SecType.ADR)));
		assertEquals(new Symbol("B:lol@zoo:RUB"), service.toSymbol(new TQSecID_F("lol", 2, "KAA", SecType.BOND)));
		assertEquals(new Symbol("C:JPY@foo:RUB"), service.toSymbol(new TQSecID_F("JPY", 0, "JAPAN", SecType.CURRENCY)));
		assertEquals(new Symbol("U:gaz@zoo:RUB"), service.toSymbol(new TQSecID_F("gaz", 2, "GGG", SecType.ERROR))); 
		assertEquals(new Symbol("C:CAD@foo:RUB"), service.toSymbol(new TQSecID_F("CAD", 0, "CANADA", SecType.ETS_CURRENCY)));
		assertEquals(new Symbol("U:GGR@zoo:RUB"), service.toSymbol(new TQSecID_F("GGR", 2, "GAGR", SecType.ETS_SWAP)));
		assertEquals(new Symbol("F:GAZ@foo:RUB"), service.toSymbol(new TQSecID_F("GAZ", 0, "<G>", SecType.FOB)));
		assertEquals(new Symbol("F:RTS-9.19@bar:RUB"), service.toSymbol(new TQSecID_F("RIZ", 1, "RTS-9.19", SecType.FUT)));
		assertEquals(new Symbol("B:ZAP@zoo:RUB"), service.toSymbol(new TQSecID_F("ZAP", 2, "Zorg", SecType.GKO)));
		assertEquals(new Symbol("U:zzz@bar:RUB"), service.toSymbol(new TQSecID_F("zzz", 1, "aaa", SecType.IDX)));
		assertEquals(new Symbol("U:bak@foo:RUB"), service.toSymbol(new TQSecID_F("bak", 0, "Barter", SecType.MCT)));
		assertEquals(new Symbol("U:GOLD@zoo:RUB"), service.toSymbol(new TQSecID_F("GOLD", 2, "Gold", SecType.METAL)));
		assertEquals(new Symbol("U:AAPL@bar:RUB"), service.toSymbol(new TQSecID_F("AAPL", 1, "Apple", SecType.NYSE)));
		assertEquals(new Symbol("U:BR@zoo:RUB"), service.toSymbol(new TQSecID_F("BR", 2, "Brent", SecType.OIL)));
		assertEquals(new Symbol("O:GZR-1@bar:RUB"), service.toSymbol(new TQSecID_F("GZR-1", 1, "Grizzly", SecType.OPT)));
		assertEquals(new Symbol("U:bubble@foo:RUB"), service.toSymbol(new TQSecID_F("bubble", 0, "Bx", SecType.QUOTES)));
		assertEquals(new Symbol("S:SBER@zoo:RUB"), service.toSymbol(new TQSecID_F("SBER", 2, "Sberbank", SecType.SHARE)));
	}

}

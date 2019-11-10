package ru.prolib.aquila.transaq.impl;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.transaq.engine.Engine;

public class TQSymbolSubscrHandlerTest {
	private static Symbol symbol1, symbol2;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		symbol1 = new Symbol("AAPL");
		symbol2 = new Symbol("MSFT");
	}
	
	private IMocksControl control;
	private Engine engMock1, engMock2;
	private TQSymbolSubscrHandler service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		engMock1 = control.createMock(Engine.class);
		engMock2 = control.createMock(Engine.class);
		service = new TQSymbolSubscrHandler(engMock1, symbol1, MDLevel.L1);
	}
	
	@Test
	public void testGetters() {
		assertEquals(engMock1, service.getEngine());
		assertEquals(symbol1, service.getSymbol());
		assertEquals(MDLevel.L1, service.getLevel());
		assertFalse(service.isClosed());
	}
	
	@Test
	public void testClose() {
		engMock1.unsubscribeSymbol(symbol1, MDLevel.L1);
		control.replay();
		
		service.close();
		
		control.verify();
		
		service.close();
		service.close();
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}

	@Test
	public void testEquals() {
		control.resetToNice();
		control.replay();
		Variant<Engine> vEng = new Variant<>(engMock1, engMock2);
		Variant<Symbol> vSym = new Variant<>(vEng, symbol1, symbol2);
		Variant<MDLevel> vLev = new Variant<>(vSym, MDLevel.L1, MDLevel.L2);
		Variant<Boolean> vCls = new Variant<>(vLev, false, true);
		Variant<?> iterator = vCls;
		int found_cnt = 0;
		TQSymbolSubscrHandler x, found = null;
		do {
			x = new TQSymbolSubscrHandler(vEng.get(), vSym.get(), vLev.get());
			if ( vCls.get() ) {
				x.close();
			}
			if ( service.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals(engMock1, found.getEngine());
		assertEquals(symbol1, found.getSymbol());
		assertEquals(MDLevel.L1, found.getLevel());
		assertFalse(found.isClosed());
	}

}

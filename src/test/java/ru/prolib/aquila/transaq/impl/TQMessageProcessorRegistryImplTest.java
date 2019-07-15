package ru.prolib.aquila.transaq.impl;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class TQMessageProcessorRegistryImplTest {
	private IMocksControl control;
	private TQMessageProcessor procMock1, procMock2, procMock3, procMock4;
	private TQMessageProcessorRegistryImpl service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		procMock1 = control.createMock(TQMessageProcessor.class);
		procMock2 = control.createMock(TQMessageProcessor.class);
		procMock3 = control.createMock(TQMessageProcessor.class);
		procMock4 = control.createMock(TQMessageProcessor.class);
		Map<String, TQMessageProcessor> map = new HashMap<>();
		service = new TQMessageProcessorRegistryImpl(procMock1, map);
		map.put("one", procMock2);
		map.put("foo", procMock3);
		map.put("buz", procMock4);
	}
	
	@Test
	public void testGet_DefaultIfNotFound() {
		
		assertEquals(procMock1, service.get("goo"));
		assertEquals(procMock1, service.get("map"));
		assertEquals(procMock1, service.get("poo"));
	}

	@Test
	public void testGet() {
		
		assertEquals(procMock2, service.get("one"));
		assertEquals(procMock3, service.get("foo"));
		assertEquals(procMock4, service.get("buz"));
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		Map<String, TQMessageProcessor> map1 = new HashMap<>();
		map1.put("one", procMock2);
		map1.put("foo", procMock3);
		map1.put("buz", procMock4);
		Map<String, TQMessageProcessor> map2 = new HashMap<>();
		map2.put("gap", procMock3);
		map2.put("zap", procMock2);
		map2.put("boo", procMock1);
		Variant<TQMessageProcessor> vDEF = new Variant<>(procMock1, procMock4);
		Variant<Map<String, TQMessageProcessor>> vMAP = new Variant<>(vDEF, map1, map2);
		Variant<?> iterator = vMAP;
		int found_cnt = 0;
		TQMessageProcessorRegistryImpl x, found = null;
		do {
			x = new TQMessageProcessorRegistryImpl(vDEF.get(), vMAP.get());
			if ( service.equals(x) ) {
				found = x;
				found_cnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals(procMock1, found.getDefaultProcessor());
		assertEquals(map1, found.getProcessorMap());
	}

}

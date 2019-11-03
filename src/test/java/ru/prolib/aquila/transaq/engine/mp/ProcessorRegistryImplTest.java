package ru.prolib.aquila.transaq.engine.mp;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.transaq.engine.mp.MessageProcessor;
import ru.prolib.aquila.transaq.engine.mp.ProcessorRegistryImpl;

public class ProcessorRegistryImplTest {
	private IMocksControl control;
	private MessageProcessor procMock1, procMock2, procMock3, procMock4;
	private ProcessorRegistryImpl service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		procMock1 = control.createMock(MessageProcessor.class);
		procMock2 = control.createMock(MessageProcessor.class);
		procMock3 = control.createMock(MessageProcessor.class);
		procMock4 = control.createMock(MessageProcessor.class);
		Map<String, MessageProcessor> map = new HashMap<>();
		service = new ProcessorRegistryImpl(procMock1, map);
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
		Map<String, MessageProcessor> map1 = new HashMap<>();
		map1.put("one", procMock2);
		map1.put("foo", procMock3);
		map1.put("buz", procMock4);
		Map<String, MessageProcessor> map2 = new HashMap<>();
		map2.put("gap", procMock3);
		map2.put("zap", procMock2);
		map2.put("boo", procMock1);
		Variant<MessageProcessor> vDEF = new Variant<>(procMock1, procMock4);
		Variant<Map<String, MessageProcessor>> vMAP = new Variant<>(vDEF, map1, map2);
		Variant<?> iterator = vMAP;
		int found_cnt = 0;
		ProcessorRegistryImpl x, found = null;
		do {
			x = new ProcessorRegistryImpl(vDEF.get(), vMAP.get());
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

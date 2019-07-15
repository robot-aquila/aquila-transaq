package ru.prolib.aquila.transaq.impl;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class TQMessageProcessorRegistryBuilderTest {
	private IMocksControl control;
	private TQMessageProcessor procMock1, procMock2, procMock3, procMock4;
	private TQMessageProcessorRegistryBuilder service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		procMock1 = control.createMock(TQMessageProcessor.class);
		procMock2 = control.createMock(TQMessageProcessor.class);
		procMock3 = control.createMock(TQMessageProcessor.class);
		procMock4 = control.createMock(TQMessageProcessor.class);
		service = new TQMessageProcessorRegistryBuilder();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testBuild_ThrowsIfDefaultProcessorNotDefined() {
		assertSame(service, service.withProcessor("foo", procMock1));
		assertSame(service, service.withProcessor("bar", procMock2));
		
		service.build();
	}

	@Test
	public void testBuild() {
		assertSame(service, service.withProcessor("bak", procMock1));
		assertSame(service, service.withProcessor("boo", procMock2));
		assertSame(service, service.withProcessor("pal", procMock3));
		assertSame(service, service.withDefaultProcessor(procMock4));
		
		TQMessageProcessorRegistry actual = service.build();
		
		Map<String, TQMessageProcessor> map = new HashMap<>();
		map.put("bak", procMock1);
		map.put("boo", procMock2);
		map.put("pal", procMock3);
		TQMessageProcessorRegistry expected = new TQMessageProcessorRegistryImpl(procMock4, map);
		assertEquals(expected, actual);
	}

}

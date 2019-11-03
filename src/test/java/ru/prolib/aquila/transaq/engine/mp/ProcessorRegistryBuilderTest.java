package ru.prolib.aquila.transaq.engine.mp;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.engine.mp.MessageProcessor;
import ru.prolib.aquila.transaq.engine.mp.ProcessorRegistry;
import ru.prolib.aquila.transaq.engine.mp.ProcessorRegistryBuilder;
import ru.prolib.aquila.transaq.engine.mp.ProcessorRegistryImpl;

public class ProcessorRegistryBuilderTest {
	private IMocksControl control;
	private MessageProcessor procMock1, procMock2, procMock3, procMock4;
	private ProcessorRegistryBuilder service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		procMock1 = control.createMock(MessageProcessor.class);
		procMock2 = control.createMock(MessageProcessor.class);
		procMock3 = control.createMock(MessageProcessor.class);
		procMock4 = control.createMock(MessageProcessor.class);
		service = new ProcessorRegistryBuilder();
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
		
		ProcessorRegistry actual = service.build();
		
		Map<String, MessageProcessor> map = new HashMap<>();
		map.put("bak", procMock1);
		map.put("boo", procMock2);
		map.put("pal", procMock3);
		ProcessorRegistry expected = new ProcessorRegistryImpl(procMock4, map);
		assertEquals(expected, actual);
	}

}

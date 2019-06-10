package ru.prolib.aquila.transaq.impl;

import static org.junit.Assert.*;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.easymock.EasyMock.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.transaq.xml.Parser;

public class MessageRouterTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	private IMocksControl control;
	private IMessageProcessor procMock1;
	private IProcessorRegistry registryMock;
	private MessageRouter service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		procMock1 = control.createMock(IMessageProcessor.class);
		registryMock = control.createMock(IProcessorRegistry.class);
		service = new MessageRouter(registryMock);
	}

	@Test
	public void testDispatchMessage_OK() {
		expect(registryMock.get("foobar")).andReturn(new IMessageProcessor() {
			@Override
			public void processMessage(XMLStreamReader reader) throws XMLStreamException {
				assertEquals("foobar", reader.getLocalName());
				assertEquals("zulu-charlie", new Parser().readCharacters(reader));
			}
		});
		control.replay();
		
		service.dispatchMessage("<foobar>zulu-charlie</foobar>");
		
		control.verify();
	}

	@Test
	public void testDispatchMessage_MalformedMessage() {
		control.replay();
		
		service.dispatchMessage("zulu-charlie");
		
		control.verify();
	}

	
	@Test
	public void testDispatchMessage_ProcessorThrows() throws Exception {
		expect(registryMock.get("foobar")).andReturn(procMock1);
		procMock1.processMessage(anyObject());
		expectLastCall().andThrow(new Exception("Test error"));
		control.replay();
		
		service.dispatchMessage("<foobar>zulu-charlie</foobar>");
		
		control.verify();
	}

}

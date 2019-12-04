package ru.prolib.aquila.transaq.engine.mp;

import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.easymock.EasyMock.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.transaq.engine.mp.MessageProcessor;
import ru.prolib.aquila.transaq.engine.mp.ProcessorRegistry;
import ru.prolib.aquila.transaq.remote.MessageParser;
import ru.prolib.aquila.transaq.engine.mp.MessageRouterImpl;

public class MessageRouterImplTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}

	private IMocksControl control;
	private MessageProcessor procMock1;
	private ProcessorRegistry registryMock;
	private MessageRouterImpl service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		procMock1 = control.createMock(MessageProcessor.class);
		registryMock = control.createMock(ProcessorRegistry.class);
		service = new MessageRouterImpl(registryMock);
	}

	@Test
	public void testDispatchMessage_OK() throws Exception {
		CountDownLatch finished = new CountDownLatch(2);
		expect(registryMock.get("foobar")).andReturn(new MessageProcessor() {
			
			@Override
			public void processRawMessage(String message) {
				assertEquals("<foobar>zulu-charlie</foobar>", message);
				finished.countDown();
			}
			
			@Override
			public void processMessage(XMLStreamReader reader) throws XMLStreamException {
				assertEquals("foobar", reader.getLocalName());
				assertEquals("zulu-charlie", new MessageParser().readCharacters(reader));
				finished.countDown();
			}
			
		});
		control.replay();
		
		service.dispatchMessage("<foobar>zulu-charlie</foobar>");
		
		control.verify();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
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
		procMock1.processRawMessage(eq("<foobar>zulu-charlie</foobar>"));
		procMock1.processMessage(anyObject());
		expectLastCall().andThrow(new Exception("Test error"));
		control.replay();
		
		service.dispatchMessage("<foobar>zulu-charlie</foobar>");
		
		control.verify();
	}

}

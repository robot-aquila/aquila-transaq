package ru.prolib.aquila.transaq.engine.mp;

import static org.easymock.EasyMock.*;

import javax.xml.stream.XMLStreamReader;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.engine.mp.SecInfoProcessor;
import ru.prolib.aquila.transaq.impl.TQStateUpdate;
import ru.prolib.aquila.transaq.remote.ISecIDG;
import ru.prolib.aquila.transaq.remote.MessageParser;

public class SecInfoProcessorTest {
	private ServiceLocator services;
	private IMocksControl control;
	private MessageParser parserMock;
	private TQReactor reactorMock;
	private SecInfoProcessor service;
	
	@Before
	public void setUp() throws Exception {
		services = new ServiceLocator();
		control = createStrictControl();
		services.setParser(parserMock = control.createMock(MessageParser.class));
		services.setReactor(reactorMock = control.createMock(TQReactor.class));
		service = new SecInfoProcessor(services);
	}

	@Test
	public void testProcessMessage() throws Exception {
		XMLStreamReader readerMock = control.createMock(XMLStreamReader.class);
		@SuppressWarnings("unchecked")
		TQStateUpdate<ISecIDG> suMock = control.createMock(TQStateUpdate.class);
		expect(parserMock.readSecInfo(readerMock)).andReturn(suMock);
		reactorMock.updateSecurity1(suMock);
		control.replay();
		
		service.processMessage(readerMock);
		
		control.verify();
	}

}

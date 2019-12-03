package ru.prolib.aquila.transaq.engine.mp;

import static org.easymock.EasyMock.*;

import javax.xml.stream.XMLStreamReader;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.engine.mp.SecInfoProcessor;
import ru.prolib.aquila.transaq.impl.TQParser;
import ru.prolib.aquila.transaq.impl.TQStateUpdate;
import ru.prolib.aquila.transaq.remote.TQSecIDG;

public class SecInfoProcessorTest {
	private ServiceLocator services;
	private IMocksControl control;
	private TQParser parserMock;
	private TQReactor reactorMock;
	private SecInfoProcessor service;
	
	@Before
	public void setUp() throws Exception {
		services = new ServiceLocator();
		control = createStrictControl();
		services.setParser(parserMock = control.createMock(TQParser.class));
		services.setReactor(reactorMock = control.createMock(TQReactor.class));
		service = new SecInfoProcessor(services);
	}

	@Test
	public void testProcessMessage() throws Exception {
		XMLStreamReader readerMock = control.createMock(XMLStreamReader.class);
		@SuppressWarnings("unchecked")
		TQStateUpdate<TQSecIDG> suMock = control.createMock(TQStateUpdate.class);
		expect(parserMock.readSecInfo(readerMock)).andReturn(suMock);
		reactorMock.updateSecurity1(suMock);
		control.replay();
		
		service.processMessage(readerMock);
		
		control.verify();
	}

}

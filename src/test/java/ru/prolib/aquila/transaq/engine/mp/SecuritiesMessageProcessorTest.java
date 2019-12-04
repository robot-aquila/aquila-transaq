package ru.prolib.aquila.transaq.engine.mp;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamReader;

import static org.easymock.EasyMock.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.engine.mp.SecuritiesMessageProcessor;
import ru.prolib.aquila.transaq.impl.TQStateUpdate;
import ru.prolib.aquila.transaq.remote.ISecIDF;
import ru.prolib.aquila.transaq.remote.MessageParser;

public class SecuritiesMessageProcessorTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	private ServiceLocator services;
	private IMocksControl control;
	private MessageParser parserMock;
	private TQReactor reactorMock;
	private SecuritiesMessageProcessor service;

	@Before
	public void setUp() throws Exception {
		services = new ServiceLocator();
		control = createStrictControl();
		services.setParser(parserMock = control.createMock(MessageParser.class));
		services.setReactor(reactorMock = control.createMock(TQReactor.class));
		service = new SecuritiesMessageProcessor(services);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProcessMessage() throws Exception {
		XMLStreamReader readerMock = control.createMock(XMLStreamReader.class);
		List<TQStateUpdate<ISecIDF>> updates = new ArrayList<>();
		TQStateUpdate<ISecIDF> duMock1, duMock2, duMock3;
		updates.add(duMock1 = control.createMock(TQStateUpdate.class));
		updates.add(duMock2 = control.createMock(TQStateUpdate.class));
		updates.add(duMock3 = control.createMock(TQStateUpdate.class));
		expect(parserMock.readSecurities(readerMock)).andReturn(updates);
		reactorMock.updateSecurityF(duMock1);
		reactorMock.updateSecurityF(duMock2);
		reactorMock.updateSecurityF(duMock3);
		control.replay();
		
		service.processMessage(readerMock);
		
		control.verify();
	}

}

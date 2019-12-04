package ru.prolib.aquila.transaq.engine.mp;

import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamReader;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.engine.mp.PitsProcessor;
import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.impl.TQStateUpdate;
import ru.prolib.aquila.transaq.remote.ISecIDT;
import ru.prolib.aquila.transaq.remote.MessageParser;

public class PitsProcessorTest {
	private ServiceLocator services;
	private IMocksControl control;
	private MessageParser parserMock;
	private TQReactor reactorMock;
	private XMLStreamReader readerMock;
	private PitsProcessor service;

	@Before
	public void setUp() throws Exception {
		services = new ServiceLocator();
		control = createStrictControl();
		services.setParser(parserMock = control.createMock(MessageParser.class));
		services.setReactor(reactorMock = control.createMock(TQReactor.class));
		readerMock = control.createMock(XMLStreamReader.class);
		service = new PitsProcessor(services);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProcessMessage() throws Exception {
		List<TQStateUpdate<ISecIDT>> updates = new ArrayList<>();
		TQStateUpdate<ISecIDT> duMock1, duMock2, duMock3;
		updates.add(duMock1 = control.createMock(TQStateUpdate.class));
		updates.add(duMock2 = control.createMock(TQStateUpdate.class));
		updates.add(duMock3 = control.createMock(TQStateUpdate.class));
		expect(parserMock.readPits(readerMock)).andReturn(updates);
		reactorMock.updateSecurityBoard(duMock1);
		reactorMock.updateSecurityBoard(duMock2);
		reactorMock.updateSecurityBoard(duMock3);
		control.replay();
		
		service.processMessage(readerMock);
		
		control.verify();
	}

}

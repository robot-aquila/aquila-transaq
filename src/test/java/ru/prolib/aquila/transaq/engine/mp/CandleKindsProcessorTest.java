package ru.prolib.aquila.transaq.engine.mp;

import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamReader;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.engine.mp.CandleKindsProcessor;
import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.impl.TQStateUpdate;
import ru.prolib.aquila.transaq.remote.MessageParser;

public class CandleKindsProcessorTest {
	private ServiceLocator services;
	private IMocksControl control;
	private MessageParser parserMock;
	private TQReactor reactorMock;
	private XMLStreamReader readerMock;
	private CandleKindsProcessor service;

	@Before
	public void setUp() throws Exception {
		services = new ServiceLocator();
		control = createStrictControl();
		services.setParser(parserMock = control.createMock(MessageParser.class));
		services.setReactor(reactorMock = control.createMock(TQReactor.class));
		readerMock = control.createMock(XMLStreamReader.class);
		service = new CandleKindsProcessor(services);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProcessMessage() throws Exception {
		TQStateUpdate<Integer> upMock1, upMock2, upMock3;
		List<TQStateUpdate<Integer>> updates = new ArrayList<>();
		updates.add(upMock1 = control.createMock(TQStateUpdate.class));
		updates.add(upMock2 = control.createMock(TQStateUpdate.class));
		updates.add(upMock3 = control.createMock(TQStateUpdate.class));
		expect(parserMock.readCandleKinds(readerMock)).andReturn(updates);
		reactorMock.updateCandleKind(upMock1);
		reactorMock.updateCandleKind(upMock2);
		reactorMock.updateCandleKind(upMock3);
		control.replay();
		
		service.processMessage(readerMock);
		
		control.verify();
	}

}

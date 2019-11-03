package ru.prolib.aquila.transaq.engine.mp;

import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamReader;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.engine.mp.BoardsProcessor;
import ru.prolib.aquila.transaq.impl.TQParser;
import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.impl.TQStateUpdate;

public class BoardsProcessorTest {
	private IMocksControl control;
	private TQParser parserMock;
	private TQReactor reactorMock;
	private ServiceLocator services;
	private XMLStreamReader readerMock;
	private BoardsProcessor service;

	@Before
	public void setUp() throws Exception {
		services = new ServiceLocator();
		control = createStrictControl();
		services.setParser(parserMock = control.createMock(TQParser.class));
		services.setReactor(reactorMock = control.createMock(TQReactor.class));
		readerMock = control.createMock(XMLStreamReader.class);
		service = new BoardsProcessor(services);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProcessMessage() throws Exception {
		TQStateUpdate<String> upMock1, upMock2, upMock3;
		List<TQStateUpdate<String>> updates = new ArrayList<>();
		updates.add(upMock1 = control.createMock(TQStateUpdate.class));
		updates.add(upMock2 = control.createMock(TQStateUpdate.class));
		updates.add(upMock3 = control.createMock(TQStateUpdate.class));
		expect(parserMock.readBoards(readerMock)).andReturn(updates);
		reactorMock.updateBoard(upMock1);
		reactorMock.updateBoard(upMock2);
		reactorMock.updateBoard(upMock3);
		control.replay();
		
		service.processMessage(readerMock);
		
		control.verify();
	}

}

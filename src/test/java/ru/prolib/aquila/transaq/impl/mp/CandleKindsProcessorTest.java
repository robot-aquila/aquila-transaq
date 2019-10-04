package ru.prolib.aquila.transaq.impl.mp;

import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamReader;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.impl.TQParser;
import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.impl.TQStateUpdate;

public class CandleKindsProcessorTest {
	private IMocksControl control;
	private TQParser parserMock;
	private TQReactor reactorMock;
	private XMLStreamReader readerMock;
	private CandleKindsProcessor service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		parserMock = control.createMock(TQParser.class);
		reactorMock = control.createMock(TQReactor.class);
		readerMock = control.createMock(XMLStreamReader.class);
		service = new CandleKindsProcessor(reactorMock, parserMock);
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

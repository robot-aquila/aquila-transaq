package ru.prolib.aquila.transaq.impl.mp;

import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamReader;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.entity.CandleKind;
import ru.prolib.aquila.transaq.impl.TQParser;
import ru.prolib.aquila.transaq.impl.TQReactor;

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

	@Test
	public void testProcessMessage() throws Exception {
		List<CandleKind> kinds = new ArrayList<>();
		kinds.add(control.createMock(CandleKind.class));
		kinds.add(control.createMock(CandleKind.class));
		kinds.add(control.createMock(CandleKind.class));
		expect(parserMock.readCandleKinds(readerMock)).andReturn(kinds);
		reactorMock.updateCandleKinds(kinds);
		control.replay();
		
		service.processMessage(readerMock);
		
		control.verify();
	}

}

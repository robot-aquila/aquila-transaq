package ru.prolib.aquila.transaq.impl.mp;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamReader;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.entity.Market;
import ru.prolib.aquila.transaq.impl.TQParser;
import ru.prolib.aquila.transaq.impl.TQReactor;

public class MarketsProcessorTest {
	private IMocksControl control;
	private TQParser parserMock;
	private TQReactor reactorMock;
	private XMLStreamReader readerMock;
	private MarketsProcessor service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		parserMock = control.createMock(TQParser.class);
		reactorMock = control.createMock(TQReactor.class);
		readerMock = control.createMock(XMLStreamReader.class);
		service = new MarketsProcessor(reactorMock, parserMock);
	}

	@Test
	public void testProcessMessage() throws Exception {
		List<Market> markets = new ArrayList<>();
		markets.add(control.createMock(Market.class));
		markets.add(control.createMock(Market.class));
		markets.add(control.createMock(Market.class));
		expect(parserMock.readMarkets(readerMock)).andReturn(markets);
		reactorMock.updateMarkets(markets);
		control.replay();
		
		service.processMessage(readerMock);
		
		control.verify();
	}

}

package ru.prolib.aquila.transaq.impl.mp;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;

import javax.xml.stream.XMLStreamReader;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.impl.TQParser;
import ru.prolib.aquila.transaq.impl.TQSecurityUpdate1;

public class SecInfoUpdProcessorTest {
	private IMocksControl control;
	private TQParser parserMock;
	private TQReactor reactorMock;
	private SecInfoUpdProcessor service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		parserMock = control.createMock(TQParser.class);
		reactorMock = control.createMock(TQReactor.class);
		service = new SecInfoUpdProcessor(reactorMock, parserMock);
	}

	@Test
	public void testProcessMessage() throws Exception {
		XMLStreamReader readerMock = control.createMock(XMLStreamReader.class);
		TQSecurityUpdate1 suMock = control.createMock(TQSecurityUpdate1.class);
		expect(parserMock.readSecInfoUpd(readerMock)).andReturn(suMock);
		reactorMock.updateSecurity(suMock);
		control.replay();
		
		service.processMessage(readerMock);
		
		control.verify();
	}

}

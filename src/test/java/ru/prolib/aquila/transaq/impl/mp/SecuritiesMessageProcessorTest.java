package ru.prolib.aquila.transaq.impl.mp;

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
import ru.prolib.aquila.transaq.impl.TQParser;
import ru.prolib.aquila.transaq.impl.TQSecurityUpdate3;

public class SecuritiesMessageProcessorTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	private IMocksControl control;
	private TQParser parserMock;
	private TQReactor reactorMock;
	private SecuritiesMessageProcessor service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		parserMock = control.createMock(TQParser.class);
		reactorMock = control.createMock(TQReactor.class);
		service = new SecuritiesMessageProcessor(reactorMock, parserMock);
	}

	@Test
	public void testProcessMessage() throws Exception {
		XMLStreamReader readerMock = control.createMock(XMLStreamReader.class);
		List<TQSecurityUpdate3> updates = new ArrayList<>();
		TQSecurityUpdate3 duMock1, duMock2, duMock3;
		updates.add(duMock1 = control.createMock(TQSecurityUpdate3.class));
		updates.add(duMock2 = control.createMock(TQSecurityUpdate3.class));
		updates.add(duMock3 = control.createMock(TQSecurityUpdate3.class));
		expect(parserMock.readSecurities(readerMock)).andReturn(updates);
		reactorMock.updateSecurity(duMock1);
		reactorMock.updateSecurity(duMock2);
		reactorMock.updateSecurity(duMock3);
		control.replay();
		
		service.processMessage(readerMock);
		
		control.verify();
	}

}

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

import ru.prolib.aquila.transaq.entity.SecurityUpdate1;
import ru.prolib.aquila.transaq.impl.IUpdateReceiver;
import ru.prolib.aquila.transaq.impl.Parser;

public class SecuritiesMessageProcessorTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	private IMocksControl control;
	private Parser parserMock;
	private IUpdateReceiver recvMock;
	private SecuritiesMessageProcessor service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		parserMock = control.createMock(Parser.class);
		recvMock = control.createMock(IUpdateReceiver.class);
		service = new SecuritiesMessageProcessor(recvMock, parserMock);
	}

	@Test
	public void testProcessMessage() throws Exception {
		XMLStreamReader readerMock = control.createMock(XMLStreamReader.class);
		List<SecurityUpdate1> updates = new ArrayList<>();
		SecurityUpdate1 duMock1, duMock2, duMock3;
		updates.add(duMock1 = control.createMock(SecurityUpdate1.class));
		updates.add(duMock2 = control.createMock(SecurityUpdate1.class));
		updates.add(duMock3 = control.createMock(SecurityUpdate1.class));
		expect(parserMock.readSecurities(readerMock)).andReturn(updates);
		recvMock.updateSecurity(duMock1);
		recvMock.updateSecurity(duMock2);
		recvMock.updateSecurity(duMock3);
		control.replay();
		
		service.processMessage(readerMock);
		
		control.verify();
	}

}
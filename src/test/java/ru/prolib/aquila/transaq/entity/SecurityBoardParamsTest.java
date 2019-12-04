package ru.prolib.aquila.transaq.entity;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.transaq.engine.sds.SymbolTID;
import ru.prolib.aquila.transaq.impl.TQStateUpdate;
import ru.prolib.aquila.transaq.remote.TQSecIDT;
import ru.prolib.aquila.transaq.remote.ISecIDT;
import ru.prolib.aquila.transaq.remote.MessageParser;
import ru.prolib.aquila.transaq.remote.TQSecIDG;

public class SecurityBoardParamsTest {
	private static XMLInputFactory factory;
	private static MessageParser parser;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		factory = XMLInputFactory.newInstance();
		parser = new MessageParser();
	}

	private IMocksControl control;
	private OSCRepository<SymbolTID, SecurityBoardParams> repoMock;
	private EventQueue queueMock;
	private SecurityBoardParams service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		repoMock = control.createMock(OSCRepository.class);
		queueMock = control.createMock(EventQueue.class);
		service = new SecurityBoardParamsFactory(queueMock).produce(repoMock, new SymbolTID("foo", 5, "bar"));
	}
	
	private XMLStreamReader startReading(String filename, String expected_elem) throws Exception {
		InputStream is = new FileInputStream(new File(filename));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_DOCUMENT:
			case XMLStreamReader.START_ELEMENT:
				if ( expected_elem.equals(sr.getLocalName()) ) {
					return sr;
				}
				break;
			}
		}
		throw new IllegalStateException("Not found: " + expected_elem);
	}

	@Test
	public void testGetters() throws Exception {
		XMLStreamReader sr = startReading("fixture/pits.xml", "pits");
		for ( TQStateUpdate<ISecIDT> su : parser.readPits(sr) ) {
			service.consume(su.getUpdate());
		}
		sr.close();
		
		assertEquals("PRTK", service.getSecCode());
		assertEquals("TQBR", service.getBoardCode());
		assertEquals(1, service.getMarketID());
		assertEquals(2, service.getDecimals());
		assertEquals(of("0.1"), service.getMinStep());
		assertEquals(of("10"), service.getLotSize());
		assertEquals(of("12.34"), service.getPointCost());
		assertEquals(new TQSecIDG("PRTK", 1), service.toSecID1());
		assertEquals(new TQSecIDT("PRTK", "TQBR"), service.toSecID2());
	}

}

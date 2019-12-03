package ru.prolib.aquila.transaq.entity;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.transaq.engine.sds.SymbolGID;
import ru.prolib.aquila.transaq.impl.TQParser;
import ru.prolib.aquila.transaq.impl.TQStateUpdate;
import ru.prolib.aquila.transaq.remote.TQSecIDF;

public class SecurityParamsTest {
	private static XMLInputFactory factory;
	private static TQParser parser;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		factory = XMLInputFactory.newInstance();
		parser = new TQParser();
	}
	
	private IMocksControl control;
	private OSCRepository<SymbolGID, SecurityParams> repoMock;
	private EventQueue queueMock;

	private SecurityParams service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		repoMock = control.createMock(OSCRepository.class);
		queueMock = control.createMock(EventQueue.class);
		service = new SecurityParamsFactory(queueMock).produce(repoMock, new SymbolGID("KKK", 7));
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
		XMLStreamReader sr = startReading("fixture/securities.xml", "securities");
		for ( TQStateUpdate<TQSecIDF> su : parser.readSecurities(sr) ) {
			service.consume(su.getUpdate());
		}
		sr.close();
		sr = startReading("fixture/sec_info.xml", "sec_info");
		service.consume(parser.readSecInfo(sr).getUpdate());
		sr.close();
		sr = startReading("fixture/sec_info_upd.xml", "sec_info_upd");
		service.consume(parser.readSecInfoUpd(sr).getUpdate());
		sr.close();

		assertEquals("BRH0", service.getSecCode());
		assertEquals(4, service.getMarketID());
		assertEquals(true, service.isActive());
		assertEquals("F", service.getSecClass());
		assertEquals("FUT", service.getDefaultBoard());
		assertEquals("RTS-6.19", service.getShortName());
		assertEquals(0, service.getDecimals());
		assertEquals(of(10L), service.getMinStep());
		assertEquals(of(1L), service.getLotSize());
		assertEquals(of("7625.71"), service.getPointCost());
		assertEquals(0x02 | 0x10, service.getOpMask());
		assertEquals(SecType.FUT, service.getSecType());
		assertEquals("Russian Standard Time", service.getSecTZ());
		assertEquals(1, service.getQuotesType());
		assertEquals("FOOBAR", service.getSecName());
		assertEquals("pcs.", service.getPName());
		assertEquals(LocalDateTime.of(2019, 6, 1, 6, 26, 15), service.getMatDate());
		assertEquals(of("203.082"), service.getClearingPrice());
		assertEquals(of("61.97"), service.getMinPrice());
		assertEquals(of("67.19"), service.getMaxPrice());
		assertEquals(of("6132.61"), service.getBuyDeposit());
		assertEquals(of("6467.30"), service.getSellDeposit());
		assertEquals(of("811.44"), service.getBGO_C());
		assertEquals(of("4640.88"), service.getBGO_NC());
		assertEquals(of("0.02"), service.getAccruedInt());
		assertEquals(of("192.77"), service.getCouponValue());
		assertEquals(LocalDateTime.of(2019, 12, 31, 0, 0, 0), service.getCouponDate());
		assertEquals(12, service.getCouponPeriod());
		assertEquals(of("1000.00"), service.getFaceValue());
		assertEquals("P", service.getPutCall());
		assertEquals("M", service.getOptType());
		assertEquals(1, service.getLotVolume());
		assertEquals(of("4605.53"), service.getBGO_BUY());
	}

}

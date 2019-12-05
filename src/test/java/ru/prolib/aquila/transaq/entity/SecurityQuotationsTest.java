package ru.prolib.aquila.transaq.entity;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
import ru.prolib.aquila.transaq.remote.ISecIDT;
import ru.prolib.aquila.transaq.remote.MessageParser;

public class SecurityQuotationsTest {
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
	private OSCRepository<SymbolTID, SecurityQuotations> repoMock;
	private EventQueue queueMock;

	private SecurityQuotations service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		repoMock = control.createMock(OSCRepository.class);
		queueMock = control.createMock(EventQueue.class);
		service = new SecurityQuotationsFactory(queueMock).produce(repoMock, new SymbolTID("KKK", 7, "ZZZ"));
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
		XMLStreamReader sr = startReading("fixture/quotations1.xml", "quotations");
		for ( TQStateUpdate<ISecIDT> su : parser.readQuotations(sr) ) {
			service.consume(su.getUpdate());
		}
		sr.close();

		assertEquals(12345, service.getSecID());
		assertEquals("XXX", service.getBoardCode());
		assertEquals("ZZZ", service.getSecCode());
		assertEquals(of("12.345"), service.getPointCost());
		assertEquals(of("625.112"), service.getAccruedIntValue());
		assertEquals(of("776.123"), service.getOpen());
		assertEquals(of("887.141"), service.getWAPrice());
		assertEquals(Integer.valueOf(42), service.getBidDepth());
		assertEquals(Integer.valueOf(672), service.getBidDepthT());
		assertEquals(Integer.valueOf(82), service.getNumBids());
		assertEquals(Integer.valueOf(63), service.getOfferDepth());
		assertEquals(Integer.valueOf(23), service.getOfferDepthT());
		assertEquals(of("648.940"), service.getBid());
		assertEquals(of("423.512"), service.getOffer());
		assertEquals(Integer.valueOf(12), service.getNumOffers());
		assertEquals(Integer.valueOf(554), service.getNumTrades());
		assertEquals(Integer.valueOf(111), service.getVolToday());
		assertEquals(Integer.valueOf(882), service.getOpenPositions());
		assertEquals(Integer.valueOf(485), service.getDeltaPositions());
		assertEquals(of("508.124"), service.getLast());
		assertEquals(Integer.valueOf(8082), service.getQuantity());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(18,  3, 25)), service.getTime());
		assertEquals(of("4.15"), service.getChange());
		assertEquals(of("526.133"), service.getPriceMinusPrevWAPrice());
		assertEquals(of("982619.23"), service.getValToday());
		assertEquals(of("726.03"), service.getYield());
		assertEquals(of("76182.82"), service.getYieldAtWAPrice());
		assertEquals(of("9928.81"), service.getMarketPriceToday());
		assertEquals(of("821.11"), service.getHighBid());
		assertEquals(of("112.56"), service.getLowOffer());
		assertEquals(of("822.27"), service.getHigh());
		assertEquals(of("751.12"), service.getLow());
		assertEquals(of("8722.196"), service.getClosePrice());
		assertEquals(of("817.09"), service.getCloseYield());
		assertEquals("some status 1", service.getStatus());
		assertEquals("some status 2", service.getTradingStatus());
		assertEquals(of("9281.543"), service.getBuyDeposit());
		assertEquals(of("8372.711"), service.getSellDeposit());
		assertEquals(of("91.0"), service.getVolatility());
		assertEquals(of("991.115"), service.getTheoreticalPrice());
		assertEquals(of("812.761"), service.getBGO_BUY());
		assertEquals(of("883.443"), service.getLCurrentPrice());
	}

}

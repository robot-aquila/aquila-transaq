package ru.prolib.aquila.transaq.remote;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;
import static ru.prolib.aquila.transaq.remote.MessageFields.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.transaq.entity.SecType;
import ru.prolib.aquila.transaq.impl.TQStateUpdate;
import ru.prolib.aquila.transaq.remote.TQSecIDT;
import ru.prolib.aquila.transaq.remote.entity.Quote;
import ru.prolib.aquila.transaq.remote.entity.ServerStatus;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FFortsCollaterals;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FFortsMoney;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FFortsPosition;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FMoneyPosition;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FSecPosition;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FSpotLimits;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FUnitedLimits;
import ru.prolib.aquila.transaq.remote.MessageParser;
import ru.prolib.aquila.transaq.remote.TQSecIDF;
import ru.prolib.aquila.transaq.remote.TQSecIDG;

public class MessageParserTest {
	private static final Map<Integer, String> EVENT_TYPE_MAP = new Hashtable<>();
	
	static {
		EVENT_TYPE_MAP.put(XMLStreamReader.ATTRIBUTE, "ATRIBUTE");
		EVENT_TYPE_MAP.put(XMLStreamReader.CDATA, "CDATA");
		EVENT_TYPE_MAP.put(XMLStreamReader.CHARACTERS, "CHARACTERS");
		EVENT_TYPE_MAP.put(XMLStreamReader.COMMENT, "COMMENT");
		EVENT_TYPE_MAP.put(XMLStreamReader.DTD, "DTD");
		EVENT_TYPE_MAP.put(XMLStreamReader.END_DOCUMENT, "END_DOCUMENT");
		EVENT_TYPE_MAP.put(XMLStreamReader.END_ELEMENT, "END_ELEMENT");
		EVENT_TYPE_MAP.put(XMLStreamReader.ENTITY_DECLARATION, "ENTITY_DECLARATION");
		EVENT_TYPE_MAP.put(XMLStreamReader.ENTITY_REFERENCE, "ENTITY_REFERENCE");
		EVENT_TYPE_MAP.put(XMLStreamReader.NAMESPACE, "NAMESPACE");
		EVENT_TYPE_MAP.put(XMLStreamReader.NOTATION_DECLARATION, "NOTATION_DECLARATION");
		EVENT_TYPE_MAP.put(XMLStreamReader.PROCESSING_INSTRUCTION, "PROCESSING_INSTRUCTION");
		EVENT_TYPE_MAP.put(XMLStreamReader.SPACE, "SPACE");
		EVENT_TYPE_MAP.put(XMLStreamReader.START_DOCUMENT, "START_DOCUMENT");
		EVENT_TYPE_MAP.put(XMLStreamReader.START_ELEMENT, "START_ELEMENT");
	}
	
	static List<Integer> toList(Integer... value) {
		List<Integer> r = new ArrayList<>();
		for ( Integer i : value ) {
			r.add(i);
		}
		return r;
	}
	
	static Set<Integer> toSet(Integer... value) {
		return new HashSet<>(toList(value));
	}
	
	private static XMLInputFactory factory;
	
	private String getEventTypeString(int event_type) {
		String x = EVENT_TYPE_MAP.get(event_type);
		if ( x != null ) {
			return x;
		}
		throw new IllegalArgumentException("Unknown event type: " + event_type);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		factory = XMLInputFactory.newInstance();
	}
	
	private MessageParser service;

	@Before
	public void setUp() throws Exception {
		service = new MessageParser();
	}
	
	private XMLStreamReader moveNextElem(XMLStreamReader sr, String expected_elem_name) throws Exception {
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_DOCUMENT:
			case XMLStreamReader.START_ELEMENT:
				if ( expected_elem_name.equals(sr.getLocalName()) ) {
					return sr;
				}
				throw new IllegalStateException("Unexpected element: " + sr.getLocalName());
			}
		}
		throw new IllegalStateException("Element not found: " + expected_elem_name);
	}
	
	private XMLStreamReader startReader(String filename, String start_element) throws Exception {
		return moveNextElem(factory.createXMLStreamReader(new FileInputStream(new File(filename))), start_element);
	}
	
	private XMLStreamReader checkIsElementEnd(XMLStreamReader sr, String expected_element) throws Exception {
		if ( sr.getEventType() != XMLStreamReader.END_ELEMENT
		  && sr.getEventType() != XMLStreamReader.END_DOCUMENT )
		{
			throw new IllegalStateException("Unexpected event type: " + getEventTypeString(sr.getEventType()));
		}
		if ( ! expected_element.equals(sr.getLocalName()) ) {
			throw new IllegalStateException("Unexpected element: " + sr.getLocalName());
		}
		return sr;
	}
	
	@Test
	public void testReadDate() throws Exception {
		String xml = new StringBuilder()
				.append("<foo>")
					.append("<date1>01.06.2019 07:46:15.882</date1>")
					.append("<date2>31.12.1978 14:20:12</date2>")
					.append("<date3>11:37:15</date3>")
					.append("<date4>09:15:32.026</date4>")
				.append("</foo>")
				.toString();
		InputStream is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		LocalDateTime actual1 = null, actual2 = null, actual3 = null, actual4 = null;
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( sr.getLocalName() ) {
				case "date1":
					actual1 = service.readDate(sr);
					break;
				case "date2":
					actual2 = service.readDate(sr);
					break;
				case "date3":
					actual3 = service.readDate(sr);
					break;
				case "date4":
					actual4 = service.readDate(sr);
					break;
				}
				break;
			}
		}
		assertEquals(LocalDateTime.of(2019,  6,  1,  7, 46, 15, 882000000), actual1);
		assertEquals(LocalDateTime.of(1978, 12, 31, 14, 20, 12), actual2);
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 37, 15)), actual3);
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 15, 32, 26000000)), actual4);
	}
	
	@Test
	public void testReadMarkets() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/markets.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		List<TQStateUpdate<Integer>> actual = null;
		while ( sr.hasNext() ) {
        	switch ( sr.next() ) {
        	case XMLStreamReader.START_DOCUMENT:
        	case XMLStreamReader.START_ELEMENT:
        		if ( "markets".equals(sr.getLocalName()) ) {
        			actual = service.readMarkets(sr);
        		}
        		break;
        	}
		}
		List<TQStateUpdate<Integer>> expected = new ArrayList<>();
		expected.add(new TQStateUpdate<>(0, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 0)
				.withToken(FMarket.NAME, "Collateral")
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>(1, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 1)
				.withToken(FMarket.NAME, "MICEX")
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>(4, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 4)
				.withToken(FMarket.NAME, "FORTS")
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>(7, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 7)
				.withToken(FMarket.NAME, "SPBEX")
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>(8, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 8)
				.withToken(FMarket.NAME, "INF")
				.buildUpdate()
			));		
		expected.add(new TQStateUpdate<>(9, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 9)
				.withToken(FMarket.NAME, "9 [N/A]")
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>(12, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 12)
				.withToken(FMarket.NAME, "12 [N/A]")
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>(14, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 14)
				.withToken(FMarket.NAME, "MMA")
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>(15, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 15)
				.withToken(FMarket.NAME, "ETS")
				.buildUpdate()
			));
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testReadMarkets_ThrowsUnexpectedTag() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/markets2.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_ELEMENT:
				service.readMarkets(sr);
				break;
			}
		}
	}

	@Test
	public void testReadBoards() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/boards.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		List<TQStateUpdate<String>> actual = null;
		while ( sr.hasNext() ) {
        	switch ( sr.next() ) {
        	case XMLStreamReader.START_DOCUMENT:
        	case XMLStreamReader.START_ELEMENT:
        		if ( "boards".equals(sr.getLocalName()) ) {
        			actual = service.readBoards(sr);
        		}
        		break;
        	}
		}
		List<TQStateUpdate<String>> expected = new ArrayList<>();
		expected.add(new TQStateUpdate<>("AUCT", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "AUCT")
				.withToken(FBoard.NAME, "Auction")
				.withToken(FBoard.MARKET_ID, 1)
				.withToken(FBoard.TYPE, 2)
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>("EQDB", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "EQDB")
				.withToken(FBoard.NAME, "Main market: D bonds")
				.withToken(FBoard.MARKET_ID, 1)
				.withToken(FBoard.TYPE, 2)
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>("EQDP", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "EQDP")
				.withToken(FBoard.NAME, "Dark Pool")
				.withToken(FBoard.MARKET_ID, 1)
				.withToken(FBoard.TYPE, 2)
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>("CNGD", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "CNGD")
				.withToken(FBoard.NAME, "ETS Neg. deals")
				.withToken(FBoard.MARKET_ID, 15)
				.withToken(FBoard.TYPE, 2)
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>("INDEXE", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "INDEXE")
				.withToken(FBoard.NAME, "ETS indexes")
				.withToken(FBoard.MARKET_ID, 15)
				.withToken(FBoard.TYPE, 2)
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>("ZLG", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "ZLG")
				.withToken(FBoard.NAME, "Залоговые инструменты")
				.withToken(FBoard.MARKET_ID, 0)
				.withToken(FBoard.TYPE, 2)
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>("EQNL", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "EQNL")
				.withToken(FBoard.NAME, "EQNL [N/A]")
				.withToken(FBoard.MARKET_ID, 255)
				.withToken(FBoard.TYPE, 2)
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>("AETS", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "AETS")
				.withToken(FBoard.NAME, "Дополнительная сессия")
				.withToken(FBoard.MARKET_ID, 15)
				.withToken(FBoard.TYPE, 2)
				.buildUpdate()
			));
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testReadBoards_ThrowsUnexpectedTags() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/boards2.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		while ( sr.hasNext() ) {
        	switch ( sr.next() ) {
        	case XMLStreamReader.START_ELEMENT:
        		service.readBoards(sr);
        		break;
        	}
		}
	}
	
	@Test
	public void testReadCandleKinds() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/candlekinds.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		List<TQStateUpdate<Integer>> actual = null;
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_DOCUMENT:
			case XMLStreamReader.START_ELEMENT:
				if ( "candlekinds".equals(sr.getLocalName()) ) {
					actual = service.readCandleKinds(sr);
				}
				break;
			}
		}
		List<TQStateUpdate<Integer>> expected = new ArrayList<>();
		expected.add(new TQStateUpdate<>(1, new DeltaUpdateBuilder()
				.withToken(FCKind.CKIND_ID, 1)
				.withToken(FCKind.CKIND_PERIOD, 60)
				.withToken(FCKind.CKIND_NAME, "1 minute")
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>(2, new DeltaUpdateBuilder()
				.withToken(FCKind.CKIND_ID, 2)
				.withToken(FCKind.CKIND_PERIOD, 300)
				.withToken(FCKind.CKIND_NAME, "5 minutes")
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>(3, new DeltaUpdateBuilder()
				.withToken(FCKind.CKIND_ID, 3)
				.withToken(FCKind.CKIND_PERIOD, 900)
				.withToken(FCKind.CKIND_NAME, "15 minutes")
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>(4, new DeltaUpdateBuilder()
				.withToken(FCKind.CKIND_ID, 4)
				.withToken(FCKind.CKIND_PERIOD, 3600)
				.withToken(FCKind.CKIND_NAME, "1 hour")
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>(5, new DeltaUpdateBuilder()
				.withToken(FCKind.CKIND_ID, 5)
				.withToken(FCKind.CKIND_PERIOD, 86400)
				.withToken(FCKind.CKIND_NAME, "1 day")
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>(6, new DeltaUpdateBuilder()
				.withToken(FCKind.CKIND_ID, 6)
				.withToken(FCKind.CKIND_PERIOD, 604800)
				.withToken(FCKind.CKIND_NAME, "1 week")
				.buildUpdate()
			));
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testReadCandleKinds_ThrowsUnexpectedTags() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/candlekinds2.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_ELEMENT:
				service.readCandleKinds(sr);
				break;
			}
		}
	}
	
	@Test
	public void testReadSecurities() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/securities.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		List<TQStateUpdate<ISecIDF>> actual = null;
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_DOCUMENT:
			case XMLStreamReader.START_ELEMENT:
				if ( "securities".equals(sr.getLocalName()) ) {
					actual = service.readSecurities(sr);
				}
				break;
			}
		}
		List<TQStateUpdate<TQSecIDF>> expected = new ArrayList<>();
		expected.add(new TQStateUpdate<>(
			new TQSecIDF("IRGZ", 1, "TQBR", "IrkutskEnrg", SecType.SHARE),
			new DeltaUpdateBuilder()
				.withToken(FSecurity.SECID, 0)
				.withToken(FSecurity.ACTIVE, true)
				.withToken(FSecurity.SECCODE, "IRGZ")
				.withToken(FSecurity.SECCLASS, "E")
				.withToken(FSecurity.DEFAULT_BOARDCODE, "TQBR")
				.withToken(FSecurity.MARKETID, 1)
				.withToken(FSecurity.SHORT_NAME, "IrkutskEnrg")
				.withToken(FSecurity.DECIMALS, 2)
				.withToken(FSecurity.MINSTEP, of("0.02"))
				.withToken(FSecurity.LOTSIZE, of("100"))
				.withToken(FSecurity.POINT_COST, of("1"))
				.withToken(FSecurity.OPMASK, 0x01 | 0x02 | 0x04 | 0x08 | 0x10)
				.withToken(FSecurity.SECTYPE, SecType.SHARE)
				.withToken(FSecurity.SECTZ, "Russian Standard Time")
				.withToken(FSecurity.QUOTESTYPE, 1)
				.buildUpdate())
			);
		expected.add(new TQStateUpdate<>(
			new TQSecIDF("RU000A0ZZ505", 1, "EQOB", "Russian Agricultural Bank 09T1", SecType.BOND),
			new DeltaUpdateBuilder()
				.withToken(FSecurity.SECID, 3)
				.withToken(FSecurity.ACTIVE, true)
				.withToken(FSecurity.SECCODE, "RU000A0ZZ505")
				.withToken(FSecurity.SECCLASS, "B")
				.withToken(FSecurity.DEFAULT_BOARDCODE, "EQOB")
				.withToken(FSecurity.MARKETID, 1)
				.withToken(FSecurity.SHORT_NAME, "Russian Agricultural Bank 09T1")
				.withToken(FSecurity.DECIMALS, 2)
				.withToken(FSecurity.MINSTEP, of("0.01"))
				.withToken(FSecurity.LOTSIZE, of("1"))
				.withToken(FSecurity.POINT_COST, of("10"))
				.withToken(FSecurity.OPMASK, 0x01 | 0x04 | 0x08 | 0x10)
				.withToken(FSecurity.SECTYPE, SecType.BOND)
				.withToken(FSecurity.SECTZ, "Russian Standard Time")
				.withToken(FSecurity.QUOTESTYPE, 1)
				.buildUpdate())
			);
		expected.add(new TQStateUpdate<>(
			new TQSecIDF("RIM9", 4, "FUT", "RTS-6.19", SecType.FUT),
			new DeltaUpdateBuilder()
				.withToken(FSecurity.SECID, 41190)
				.withToken(FSecurity.ACTIVE, true)
				.withToken(FSecurity.SECCODE, "RIM9")
				.withToken(FSecurity.SECCLASS, "F")
				.withToken(FSecurity.DEFAULT_BOARDCODE, "FUT")
				.withToken(FSecurity.MARKETID, 4)
				.withToken(FSecurity.SHORT_NAME, "RTS-6.19")
				.withToken(FSecurity.DECIMALS, 0)
				.withToken(FSecurity.MINSTEP, of("10"))
				.withToken(FSecurity.LOTSIZE, of("1"))
				.withToken(FSecurity.POINT_COST, of("129.073"))
				.withToken(FSecurity.OPMASK, 0x02 | 0x10)
				.withToken(FSecurity.SECTYPE, SecType.FUT)
				.withToken(FSecurity.SECTZ, "Russian Standard Time")
				.withToken(FSecurity.QUOTESTYPE, 1)
				.buildUpdate())
			);
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testReadSecurities_ThrowsUnexpectedTags() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/securities3.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_ELEMENT:
				service.readSecurities(sr);
				break;
			}
		}
	}
	
	@Test
	public void testReadSecurities_UnknownSecTypeAndTags() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/securities1.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		List<TQStateUpdate<ISecIDF>> actual = null;
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_DOCUMENT:
			case XMLStreamReader.START_ELEMENT:
				if ( "securities".equals(sr.getLocalName()) ) {
					actual = service.readSecurities(sr);
				}
				break;
			}
		}
		List<TQStateUpdate<ISecIDF>> expected = new ArrayList<>();
		expected.add(new TQStateUpdate<>(
			new TQSecIDF("IRGZ", 1, "TQBR", "IrkutskEnrg", SecType.QUOTES),
			new DeltaUpdateBuilder()
				.withToken(FSecurity.SECID, 0)
				.withToken(FSecurity.ACTIVE, true)
				.withToken(FSecurity.SECCODE, "IRGZ")
				.withToken(FSecurity.SECCLASS, "E")
				.withToken(FSecurity.DEFAULT_BOARDCODE, "TQBR")
				.withToken(FSecurity.MARKETID, 1)
				.withToken(FSecurity.SHORT_NAME, "IrkutskEnrg")
				.withToken(FSecurity.DECIMALS, 2)
				.withToken(FSecurity.MINSTEP, of("0.02"))
				.withToken(FSecurity.LOTSIZE, of("100"))
				.withToken(FSecurity.POINT_COST, of("1"))
				.withToken(FSecurity.OPMASK, 0x01 | 0x02 | 0x04 | 0x08 | 0x10)
				.withToken(FSecurity.SECTYPE, SecType.QUOTES)
				.withToken(FSecurity.SECTZ, "Russian Standard Time")
				.withToken(FSecurity.QUOTESTYPE, 1)
				.buildUpdate())
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testReadSecurities_Inactive() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/securities2.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		List<TQStateUpdate<ISecIDF>> actual = null;
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_DOCUMENT:
			case XMLStreamReader.START_ELEMENT:
				if ( "securities".equals(sr.getLocalName()) ) {
					actual = service.readSecurities(sr);
				}
				break;
			}
		}
		List<TQStateUpdate<ISecIDF>> expected = new ArrayList<>();
		expected.add(new TQStateUpdate<>(
			new TQSecIDF("IRGZ", 1, "TQBR", "IrkutskEnrg", SecType.SHARE),
			new DeltaUpdateBuilder()
				.withToken(FSecurity.SECID, 0)
				.withToken(FSecurity.ACTIVE, true)
				.withToken(FSecurity.SECCODE, "IRGZ")
				.withToken(FSecurity.SECCLASS, "E")
				.withToken(FSecurity.DEFAULT_BOARDCODE, "TQBR")
				.withToken(FSecurity.MARKETID, 1)
				.withToken(FSecurity.SHORT_NAME, "IrkutskEnrg")
				.withToken(FSecurity.DECIMALS, 2)
				.withToken(FSecurity.POINT_COST, of("1"))
				.withToken(FSecurity.SECTYPE, SecType.SHARE)
				.withToken(FSecurity.SECTZ, "Russian Standard Time")
				.withToken(FSecurity.QUOTESTYPE, 1)
				.buildUpdate())
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testReadSecInfo() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/sec_info.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		TQStateUpdate<ISecIDG> actual = null;
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_DOCUMENT:
			case XMLStreamReader.START_ELEMENT:
				if ( "sec_info".equals(sr.getLocalName()) ) {
					actual = service.readSecInfo(sr);
				}
				break;
			}
		}
		TQStateUpdate<ISecIDG> expected = new TQStateUpdate<>(
			new TQSecIDG("FOO-12.35", 4),
			new DeltaUpdateBuilder()
				.withToken(FSecurity.SECID, 28334)
				.withToken(FSecurity.SECNAME, "FOOBAR")
				.withToken(FSecurity.SECCODE, "FOO-12.35")
				.withToken(FSecurity.MARKETID, 4)
				.withToken(FSecurity.PNAME, "pcs.")
				.withToken(FSecurity.MAT_DATE, LocalDateTime.of(2019, 6, 1, 6, 26, 15))
				.withToken(FSecurity.CLEARING_PRICE, of("203.082"))
				.withToken(FSecurity.MINPRICE, of("100.000"))
				.withToken(FSecurity.MAXPRICE, of("300.000"))
				.withToken(FSecurity.BUY_DEPOSIT, of("278991.92"))
				.withToken(FSecurity.SELL_DEPOSIT, of("728001.10"))
				.withToken(FSecurity.BGO_C, of("79.03"))
				.withToken(FSecurity.BGO_NC, of("86.12"))
				.withToken(FSecurity.ACCRUED_INT, of("0.02"))
				.withToken(FSecurity.COUPON_VALUE, of("192.77"))
				.withToken(FSecurity.COUPON_DATE, LocalDateTime.of(2019, 12, 31, 0, 0, 0))
				.withToken(FSecurity.COUPON_PERIOD, 12)
				.withToken(FSecurity.FACE_VALUE, of("1000.00"))
				.withToken(FSecurity.PUT_CALL, "P")
				.withToken(FSecurity.OPT_TYPE, "M")
				.withToken(FSecurity.LOT_VOLUME, 1)
				.buildUpdate()
			);
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testReadSecInfo_ThrowsUnexpectedTags() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/sec_info2.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_ELEMENT:
				service.readSecInfo(sr);
				break;
			}
		}
	}
	
	@Test
	public void testReadSecInfoUpd() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/sec_info_upd.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		TQStateUpdate<ISecIDG> actual = null;
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_DOCUMENT:
			case XMLStreamReader.START_ELEMENT:
				if ( "sec_info_upd".equals(sr.getLocalName()) ) {
					actual = service.readSecInfoUpd(sr);
				}
				break;
			}
		}
		TQStateUpdate<ISecIDG> expected = new TQStateUpdate<>(
			new TQSecIDG("BRH0", 4),
			new DeltaUpdateBuilder()
				.withToken(FSecurity.SECID, 66)
				.withToken(FSecurity.SECCODE, "BRH0")
				.withToken(FSecurity.MARKETID, 4)
				.withToken(FSecurity.BUY_DEPOSIT, of("6132.61"))
				.withToken(FSecurity.SELL_DEPOSIT, of("6467.30"))
				.withToken(FSecurity.MINPRICE, of("61.97"))
				.withToken(FSecurity.MAXPRICE, of("67.19"))
				.withToken(FSecurity.POINT_COST, of("7625.71"))
				.withToken(FSecurity.BGO_C, of("811.44"))
				.withToken(FSecurity.BGO_NC, of("4640.88"))
				.withToken(FSecurity.BGO_BUY, of("4605.53"))
				.buildUpdate()
			);
		assertEquals(expected, actual);
	}
	
	@Test (expected=IllegalStateException.class)
	public void testReadSecInfoUpd_ThrowsUnexpectedTag() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/sec_info_upd2.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_ELEMENT:
				service.readSecInfoUpd(sr);
				break;
			}
		}
	}
	
	@Test
	public void testSkipElement() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/skip_element.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		Set<String> actual_score = new LinkedHashSet<>();
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_ELEMENT:
				String tag_name = sr.getLocalName();
				switch ( tag_name ) {
				case "tag1":
				case "tag2":
				case "tag3":
				case "tag4":
					service.skipElement(sr);
					assertEquals(tag_name, sr.getLocalName());
					assertEquals(XMLStreamReader.END_ELEMENT, sr.getEventType());
					actual_score.add(tag_name);
					break;
				}
			}
		}
		Set<String> expected_score = new LinkedHashSet<>();
		expected_score.add("tag1");
		expected_score.add("tag2");
		expected_score.add("tag3");
		expected_score.add("tag4");
		assertEquals(expected_score, actual_score);
	}
	
	@Ignore
	@Test
	public void _convert1() throws Exception {
		List<String> lines = FileUtils.readLines(new File("data-sample3.txt"));
		List<String> result = new ArrayList<>();
		String start_marker = "XHandler IN> ";
		for ( String line : lines ) {
			if ( line.startsWith(start_marker) ) {
				line = line.substring(start_marker.length());
				result.add(line);
			}
		}
		FileUtils.writeLines(new File("data-sample_.txt"), result);
	}
	
	@Ignore
	@Test
	public void _convert2() throws Exception {
		int count_sections = 0, count_securities = 0, line_num = 0;
		List<String> lines = FileUtils.readLines(new File("data-sample_.txt"));
		for ( String line : lines ) {
			ByteArrayInputStream is = new ByteArrayInputStream(line.getBytes(StandardCharsets.UTF_8));
			try {
				XMLStreamReader sr = factory.createXMLStreamReader(is);
				while ( sr.hasNext() ) {
					switch ( sr.next() ) {
					case XMLStreamReader.START_DOCUMENT:
					case XMLStreamReader.START_ELEMENT:
						if ( "securities".equals(sr.getLocalName()) ) {
							count_sections ++;
							List<TQStateUpdate<ISecIDF>> list = service.readSecurities(sr);
							count_securities += list.size();
							for  ( TQStateUpdate<ISecIDF> s : list ) {
								if ( "RTS-6.19".equals(s.getUpdate().getContents().get(FSecurity.SHORT_NAME)) ) {
									System.out.println(s);
								}
							}
						}
						break;
					}
				}
				line_num ++;
				sr.close();
			} catch ( Throwable t ) {
				System.err.println("An error occurred: " + t.getMessage());
				System.err.println("While processing line [" + line_num + "]: " + line);
				t.printStackTrace(System.err);
				break;
			}
		}
		System.out.println("count sections: " + count_sections);
		System.out.println("count securities: " + count_securities);
	}
	
	/*
				if ( decimals != null && tick_size != null ) {
					tick_size = tick_size.withScale(decimals);
					if ( tick_val != null ) {
						tick_val = tick_val.multiply(tick_size)
								.multiply(of(10L).pow(decimals))
								.withScale(8)
								.divide(100L)
								.withScale(5)
								.withUnit(CDecimalBD.RUB);						
					}
				} else {
					tick_val = null;
					tick_size = null;
				}

	 */
	
	@Test
	public void testReadPits() throws Exception{
		InputStream is = new FileInputStream(new File("fixture/pits.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		List<TQStateUpdate<ISecIDT>> actual = null;
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_DOCUMENT:
			case XMLStreamReader.START_ELEMENT:
				if ( "pits".equals(sr.getLocalName()) ) {
					actual = service.readPits(sr);
				}
				break;
			}
		}
		List<TQStateUpdate<ISecIDT>> expected = new ArrayList<>();
		expected.add(new TQStateUpdate<>(new TQSecIDT("IRGZ", "TQBR"), new DeltaUpdateBuilder()
				.withToken(FSecurityBoard.SECCODE, "IRGZ")
				.withToken(FSecurityBoard.BOARD, "TQBR")
				.withToken(FSecurityBoard.MARKET, 1)
				.withToken(FSecurityBoard.DECIMALS, 2)
				.withToken(FSecurityBoard.MINSTEP, of("0.02"))
				.withToken(FSecurityBoard.LOTSIZE, of("100"))
				.withToken(FSecurityBoard.POINT_COST, of("1"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecIDT("IRGZ", "SMAL"), new DeltaUpdateBuilder()
				.withToken(FSecurityBoard.SECCODE, "IRGZ")
				.withToken(FSecurityBoard.BOARD, "SMAL")
				.withToken(FSecurityBoard.MARKET, 1)
				.withToken(FSecurityBoard.DECIMALS, 2)
				.withToken(FSecurityBoard.MINSTEP, of("0.02"))
				.withToken(FSecurityBoard.LOTSIZE, of("1"))
				.withToken(FSecurityBoard.POINT_COST, of("1"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecIDT("IRGZ", "EQRP"), new DeltaUpdateBuilder()
				.withToken(FSecurityBoard.SECCODE, "IRGZ")
				.withToken(FSecurityBoard.BOARD, "EQRP")
				.withToken(FSecurityBoard.MARKET, 1)
				.withToken(FSecurityBoard.DECIMALS, 4)
				.withToken(FSecurityBoard.MINSTEP, of("0.01"))
				.withToken(FSecurityBoard.LOTSIZE, of("100"))
				.withToken(FSecurityBoard.POINT_COST, of("0.01"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecIDT("IRGZ", "PSEQ"), new DeltaUpdateBuilder()
				.withToken(FSecurityBoard.SECCODE, "IRGZ")
				.withToken(FSecurityBoard.BOARD, "PSEQ")
				.withToken(FSecurityBoard.MARKET, 1)
				.withToken(FSecurityBoard.DECIMALS, 2)
				.withToken(FSecurityBoard.MINSTEP, of("0.02"))
				.withToken(FSecurityBoard.LOTSIZE, of("100"))
				.withToken(FSecurityBoard.POINT_COST, of("1"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecIDT("IRGZ", "PSRP"), new DeltaUpdateBuilder()
				.withToken(FSecurityBoard.SECCODE, "IRGZ")
				.withToken(FSecurityBoard.BOARD, "PSRP")
				.withToken(FSecurityBoard.MARKET, 1)
				.withToken(FSecurityBoard.DECIMALS, 4)
				.withToken(FSecurityBoard.MINSTEP, of("0.01"))
				.withToken(FSecurityBoard.LOTSIZE, of("100"))
				.withToken(FSecurityBoard.POINT_COST, of("0.01"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecIDT("IRGZ", "PTEQ"), new DeltaUpdateBuilder()
				.withToken(FSecurityBoard.SECCODE, "IRGZ")
				.withToken(FSecurityBoard.BOARD, "PTEQ")
				.withToken(FSecurityBoard.MARKET, 1)
				.withToken(FSecurityBoard.DECIMALS, 2)
				.withToken(FSecurityBoard.MINSTEP, of("0.02"))
				.withToken(FSecurityBoard.LOTSIZE, of("100"))
				.withToken(FSecurityBoard.POINT_COST, of("1"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecIDT("IRGZ", "RPEU"), new DeltaUpdateBuilder()
				.withToken(FSecurityBoard.SECCODE, "IRGZ")
				.withToken(FSecurityBoard.BOARD, "RPEU")
				.withToken(FSecurityBoard.MARKET, 1)
				.withToken(FSecurityBoard.DECIMALS, 6)
				.withToken(FSecurityBoard.MINSTEP, of("0.000001"))
				.withToken(FSecurityBoard.LOTSIZE, of("1"))
				.withToken(FSecurityBoard.POINT_COST, of("0.006429"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecIDT("IRGZ", "RPMA"), new DeltaUpdateBuilder()
				.withToken(FSecurityBoard.SECCODE, "IRGZ")
				.withToken(FSecurityBoard.BOARD, "RPMA")
				.withToken(FSecurityBoard.MARKET, 1)
				.withToken(FSecurityBoard.DECIMALS, 6)
				.withToken(FSecurityBoard.MINSTEP, of("0.000001"))
				.withToken(FSecurityBoard.LOTSIZE, of("1"))
				.withToken(FSecurityBoard.POINT_COST, of("0.0001"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecIDT("IRGZ", "RPMO"), new DeltaUpdateBuilder()
				.withToken(FSecurityBoard.SECCODE, "IRGZ")
				.withToken(FSecurityBoard.BOARD, "RPMO")
				.withToken(FSecurityBoard.MARKET, 1)
				.withToken(FSecurityBoard.DECIMALS, 6)
				.withToken(FSecurityBoard.MINSTEP, of("0.000001"))
				.withToken(FSecurityBoard.LOTSIZE, of("1"))
				.withToken(FSecurityBoard.POINT_COST, of("0.0001"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecIDT("RU000A0ZZ505", "EQOB"), new DeltaUpdateBuilder()
				.withToken(FSecurityBoard.SECCODE, "RU000A0ZZ505")
				.withToken(FSecurityBoard.BOARD, "EQOB")
				.withToken(FSecurityBoard.MARKET, 1)
				.withToken(FSecurityBoard.DECIMALS, 2)
				.withToken(FSecurityBoard.MINSTEP, of("0.01"))
				.withToken(FSecurityBoard.LOTSIZE, of("1"))
				.withToken(FSecurityBoard.POINT_COST, of("10"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecIDT("RU000A0ZZ505", "PSOB"), new DeltaUpdateBuilder()
				.withToken(FSecurityBoard.SECCODE, "RU000A0ZZ505")
				.withToken(FSecurityBoard.BOARD, "PSOB")
				.withToken(FSecurityBoard.MARKET, 1)
				.withToken(FSecurityBoard.DECIMALS, 2)
				.withToken(FSecurityBoard.MINSTEP, of("0.01"))
				.withToken(FSecurityBoard.LOTSIZE, of("1"))
				.withToken(FSecurityBoard.POINT_COST, of("10"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecIDT("RU000A0ZZ505", "RPEU"), new DeltaUpdateBuilder()
				.withToken(FSecurityBoard.SECCODE, "RU000A0ZZ505")
				.withToken(FSecurityBoard.BOARD, "RPEU")
				.withToken(FSecurityBoard.MARKET, 1)
				.withToken(FSecurityBoard.DECIMALS, 6)
				.withToken(FSecurityBoard.MINSTEP, of("0.000001"))
				.withToken(FSecurityBoard.LOTSIZE, of("1"))
				.withToken(FSecurityBoard.POINT_COST, of("0.001"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecIDT("RU000A0ZZ505", "RPMO"), new DeltaUpdateBuilder()
				.withToken(FSecurityBoard.SECCODE, "RU000A0ZZ505")
				.withToken(FSecurityBoard.BOARD, "RPMO")
				.withToken(FSecurityBoard.MARKET, 1)
				.withToken(FSecurityBoard.DECIMALS, 4)
				.withToken(FSecurityBoard.MINSTEP, of("0.0001"))
				.withToken(FSecurityBoard.LOTSIZE, of("1"))
				.withToken(FSecurityBoard.POINT_COST, of("0.1"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecIDT("PRTK", "TQBR"), new DeltaUpdateBuilder()
				.withToken(FSecurityBoard.SECCODE, "PRTK")
				.withToken(FSecurityBoard.BOARD, "TQBR")
				.withToken(FSecurityBoard.MARKET, 1)
				.withToken(FSecurityBoard.DECIMALS, 2)
				.withToken(FSecurityBoard.MINSTEP, of("0.1"))
				.withToken(FSecurityBoard.LOTSIZE, of("10"))
				.withToken(FSecurityBoard.POINT_COST, of("12.34"))
				.buildUpdate()));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testReadPits_WeirdPointCostForm() throws Exception {
		XMLStreamReader sr = startReader("fixture/pits2.xml", "pits");
		
		List<TQStateUpdate<ISecIDT>> actual = service.readPits(sr);
		
		List<TQStateUpdate<ISecIDT>> expected = new ArrayList<>();
		expected.add(new TQStateUpdate<>(new TQSecIDT("ZOO", "BAR"), new DeltaUpdateBuilder()
				.withToken(FSecurityBoard.SECCODE, "ZOO")
				.withToken(FSecurityBoard.BOARD, "BAR")
				.withToken(FSecurityBoard.MARKET, 1)
				.withToken(FSecurityBoard.DECIMALS, 2)
				.withToken(FSecurityBoard.MINSTEP, of("0.02"))
				.withToken(FSecurityBoard.LOTSIZE, of("100"))
				.withToken(FSecurityBoard.POINT_COST, of("0.025"))
				.buildUpdate()));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testReadQuotations() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/quotations1.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		List<TQStateUpdate<ISecIDT>> actual = null;
		while ( sr.hasNext() ) {
			switch ( sr.next() ) {
			case XMLStreamReader.START_DOCUMENT:
			case XMLStreamReader.START_ELEMENT:
				if ( "quotations".equals(sr.getLocalName()) ) {
					actual = service.readQuotations(sr);
				}
				break;
			}
		}
		List<TQStateUpdate<ISecIDT>> expected = new ArrayList<>();
		expected.add(new TQStateUpdate<>(new TQSecIDT("SBER", "TQBR"), new DeltaUpdateBuilder()
				.withToken(FQuotation.SECID, 2426)
				.withToken(FQuotation.BOARD, "TQBR")
				.withToken(FQuotation.SECCODE, "SBER")
				.withToken(FQuotation.LAST, of("241.23"))
				.withToken(FQuotation.QUANTITY, 33)
				.withToken(FQuotation.TIME, LocalDateTime.of(LocalDate.now(), LocalTime.of(16, 2, 16)))
				.withToken(FQuotation.CHANGE, of("1.06"))
				.withToken(FQuotation.PRICE_MINUS_PREV_WA_PRICE, of("1.05"))
				.withToken(FQuotation.BID, of("241.22"))
				.withToken(FQuotation.BID_DEPTH, 451)
				.withToken(FQuotation.BID_DEPTH_T, 391535)
				.withToken(FQuotation.NUM_BIDS, 2847)
				.withToken(FQuotation.OFFER, of("241.26"))
				.withToken(FQuotation.OFFER_DEPTH, 250)
				.withToken(FQuotation.OFFER_DEPTH_T, 495471)
				.withToken(FQuotation.NUM_OFFERS, 2583)
				.withToken(FQuotation.VOL_TODAY, 2120430)
				.withToken(FQuotation.NUM_TRADES, 36543)
				.withToken(FQuotation.VAL_TODAY, of("5106.936"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecIDT("RIZ9", "FUT"), new DeltaUpdateBuilder()
				.withToken(FQuotation.SECID, 41082)
				.withToken(FQuotation.BOARD, "FUT")
				.withToken(FQuotation.SECCODE, "RIZ9")
				.withToken(FQuotation.QUANTITY, 1)
				.withToken(FQuotation.BID, of("145250"))
				.withToken(FQuotation.BID_DEPTH, 1)
				.withToken(FQuotation.BID_DEPTH_T, 13842)
				.withToken(FQuotation.NUM_BIDS, 2130)
				.withToken(FQuotation.OFFER, of("145260"))
				.withToken(FQuotation.OFFER_DEPTH, 28)
				.withToken(FQuotation.OFFER_DEPTH_T, 9820)
				.withToken(FQuotation.NUM_OFFERS, 1890)
				.withToken(FQuotation.VOL_TODAY, 205233)
				.withToken(FQuotation.NUM_TRADES, 105563)
				.withToken(FQuotation.VAL_TODAY, of("38234.816"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecIDT("ZZZ", "XXX"), new DeltaUpdateBuilder()
				.withToken(FQuotation.SECID, 12345)
				.withToken(FQuotation.BOARD, "XXX")
				.withToken(FQuotation.SECCODE, "ZZZ")
				.withToken(FQuotation.POINT_COST, of("12.345"))
				.withToken(FQuotation.ACCRUED_INT_VALUE, of("625.112"))
				.withToken(FQuotation.OPEN, of("776.123"))
				.withToken(FQuotation.WA_PRICE, of("887.141"))
				.withToken(FQuotation.BID_DEPTH, 42)
				.withToken(FQuotation.BID_DEPTH_T, 672)
				.withToken(FQuotation.NUM_BIDS, 82)
				.withToken(FQuotation.OFFER_DEPTH, 63)
				.withToken(FQuotation.OFFER_DEPTH_T, 23)
				.withToken(FQuotation.BID, of("648.940"))
				.withToken(FQuotation.OFFER, of("423.512"))
				.withToken(FQuotation.NUM_OFFERS, 12)
				.withToken(FQuotation.NUM_TRADES, 554)
				.withToken(FQuotation.VOL_TODAY, 111)
				.withToken(FQuotation.OPEN_POSITIONS, 882)
				.withToken(FQuotation.DELTA_POSITIONS, 485)
				.withToken(FQuotation.LAST, of("508.124"))
				.withToken(FQuotation.QUANTITY, 8082)
				.withToken(FQuotation.TIME, LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 3, 25)))
				.withToken(FQuotation.CHANGE, of("4.15"))
				.withToken(FQuotation.PRICE_MINUS_PREV_WA_PRICE, of("526.133"))
				.withToken(FQuotation.VAL_TODAY, of("982619.23"))
				.withToken(FQuotation.YIELD, of("726.03"))
				.withToken(FQuotation.YIELD_AT_WA_PRICE, of("76182.82"))
				.withToken(FQuotation.MARKET_PRICE_TODAY, of("9928.81"))
				.withToken(FQuotation.HIGH_BID, of("821.11"))
				.withToken(FQuotation.LOW_OFFER, of("112.56"))
				.withToken(FQuotation.HIGH, of("822.27"))
				.withToken(FQuotation.LOW, of("751.12"))
				.withToken(FQuotation.CLOSE_PRICE, of("8722.196"))
				.withToken(FQuotation.CLOSE_YIELD, of("817.09"))
				.withToken(FQuotation.STATUS, "some status 1")
				.withToken(FQuotation.TRADING_STATUS, "some status 2")
				.withToken(FQuotation.BUY_DEPOSIT, of("9281.543"))
				.withToken(FQuotation.SELL_DEPOSIT, of("8372.711"))
				.withToken(FQuotation.VOLATILITY, of("91.0"))
				.withToken(FQuotation.THEORETICAL_PRICE, of("991.115"))
				.withToken(FQuotation.BGO_BUY, of("812.761"))
				.withToken(FQuotation.L_CURRENT_PRICE, of("883.443"))
				.buildUpdate()));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testReadAlltrades() throws Exception {
		XMLStreamReader sr = startReader("fixture/alltrades.xml", "alltrades");
		List<TQStateUpdate<ISecIDT>> actual = service.readAlltrades(sr);
		sr.close();
		List<TQStateUpdate<ISecIDT>> expected = new ArrayList<>();
		expected.add(new TQStateUpdate<>(new TQSecIDT("RIZ9", "FUT"), new DeltaUpdateBuilder()
				.withToken(FTrade.SECID, 41712)
				.withToken(FTrade.TRADENO, "2418002896")
				.withToken(FTrade.BOARD, "FUT")
				.withToken(FTrade.TIME, LocalDateTime.of(2019, 12, 4, 11, 12, 37, 85000000))
				.withToken(FTrade.PRICE, of(141300L))
				.withToken(FTrade.QUANTITY, of(1L))
				.withToken(FTrade.BUYSELL, "B")
				.withToken(FTrade.OPENINTEREST, of(319668L))
				.withToken(FTrade.SECCODE, "RIZ9")
				.withToken(FTrade.PERIOD, "")
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecIDT("RIZ9", "FUT"), new DeltaUpdateBuilder()
				.withToken(FTrade.SECID, 41712)
				.withToken(FTrade.TRADENO, "2418002897")
				.withToken(FTrade.BOARD, "FUT")
				.withToken(FTrade.TIME, LocalDateTime.of(2019, 12, 4, 11, 12, 38, 175000000))
				.withToken(FTrade.PRICE, of(141200L))
				.withToken(FTrade.QUANTITY, of(3L))
				.withToken(FTrade.BUYSELL, "S")
				.withToken(FTrade.OPENINTEREST, of(319674L))
				.withToken(FTrade.SECCODE, "RIZ9")
				.withToken(FTrade.PERIOD, "N")
				.buildUpdate()));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testReadQuotes() throws Exception {
		XMLStreamReader sr = startReader("fixture/quotes.xml", "quotes");
		List<Quote> actual = service.readQuotes(sr);
		sr.close();
		
		List<Quote> expected = new ArrayList<>();
		expected.add(new Quote(new TQSecIDT("RIZ9", "FUT"), of(141280L), 0L, 24L, null));
		expected.add(new Quote(new TQSecIDT("RIZ9", "FUT"), of("141290", "BUBBA-5"), 0L, 10L, null));
		expected.add(new Quote(new TQSecIDT("RIZ9", "FUT"), of(141280L), 0L, 25L, null));
		expected.add(new Quote(new TQSecIDT("RIZ9", "FUT"), of("141300", "BABBA-12"), 0L, null, 10L));
		expected.add(new Quote(new TQSecIDT("RIZ9", "FUT"), of(141310L), 0L, null, 28L));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testReadServerStatus() throws Exception {
		String element = "server_status";
		XMLStreamReader sr = startReader("fixture/server_status1.xml", "root");
		
		moveNextElem(sr, element);
		assertEquals(new ServerStatus(true, false, null), service.readServerStatus(sr));
		checkIsElementEnd(sr, element);

		moveNextElem(sr, element);
		assertEquals(new ServerStatus(false, true, null), service.readServerStatus(sr));
		checkIsElementEnd(sr, element);
		
		moveNextElem(sr, element);
		assertEquals(new ServerStatus(false, false, "Connection lost"), service.readServerStatus(sr));
		checkIsElementEnd(sr, element);
	}
	
	@Test
	public void testReadClient() throws Exception {
		String element = "client";
		XMLStreamReader sr = startReader("fixture/clients.xml", "root");
		
		moveNextElem(sr, element);
		TQStateUpdate<String> actual = service.readClient(sr);
		TQStateUpdate<String> expected = new TQStateUpdate<>("1RVKK/1RVKK", new DeltaUpdateBuilder()
				.withToken(FClient.ID, "1RVKK/1RVKK")
				.withToken(FClient.REMOVE, false)
				.withToken(FClient.MARKET_ID, 1)
				.withToken(FClient.CURRENCY, "RUB")
				.withToken(FClient.TYPE, "leverage")
				.withToken(FClient.UNION_CODE, "GISGIS19KV")
				.buildUpdate());
		assertEquals(expected, actual);
		checkIsElementEnd(sr, element);
		
		moveNextElem(sr, element);
		actual = service.readClient(sr);
		expected = new TQStateUpdate<>("88812chuchi", new DeltaUpdateBuilder()
				.withToken(FClient.ID, "88812chuchi")
				.withToken(FClient.REMOVE, false)
				.withToken(FClient.MARKET_ID, 4)
				.withToken(FClient.CURRENCY, "RUB")
				.withToken(FClient.TYPE, "leverage")
				.withToken(FClient.UNION_CODE, "GISGIS19KV")
				.withToken(FClient.FORTS_ACCOUNT, "ZYUZEE-MOOZEE")
				.buildUpdate());
		assertEquals(expected, actual);
		checkIsElementEnd(sr, element);
		
		moveNextElem(sr, element);
		actual = service.readClient(sr);
		expected = new TQStateUpdate<>("SUSEK/2XVKZ", new DeltaUpdateBuilder()
				.withToken(FClient.ID, "SUSEK/2XVKZ")
				.withToken(FClient.REMOVE, true)
				.withToken(FClient.MARKET_ID, 15)
				.withToken(FClient.CURRENCY, "RUB")
				.withToken(FClient.TYPE, "leverage")
				.buildUpdate());
		assertEquals(expected, actual);
		checkIsElementEnd(sr, element);

		moveNextElem(sr, element);
		actual = service.readClient(sr);
		expected = new TQStateUpdate<>("BUM75/2XVKL", new DeltaUpdateBuilder()
				.withToken(FClient.ID, "BUM75/2XVKL")
				.withToken(FClient.REMOVE, false)
				.withToken(FClient.MARKET_ID, 14)
				.withToken(FClient.CURRENCY, "USD")
				.withToken(FClient.TYPE, "mct")
				.withToken(FClient.UNION_CODE, "GISGIS19KV")
				.buildUpdate());
		assertEquals(expected, actual);
		checkIsElementEnd(sr, element);
	}
	
	@Test
	public void testReadPositions_Case1() throws Exception {
		XMLStreamReader sr = startReader("fixture/positions1.xml", "positions");
		List<TQStateUpdate<? extends ID>> actual = service.readPositions(sr);
		sr.close();
		
		List<TQStateUpdate<? extends ID>> expected = new ArrayList<>();
		expected.add(new TQStateUpdate<>(new ID.MP("1RXXX/1RXXX", "FOND_MICEX", "T0"), new DeltaUpdateBuilder()
				.withToken(FMoneyPosition.CLIENT_ID, "1RXXX/1RXXX")
				.withToken(FMoneyPosition.UNION_CODE, "KASUM-1234")
				.withToken(FMoneyPosition.MARKETS, toList(1))
				.withToken(FMoneyPosition.ASSET, "FOND_MICEX")
				.withToken(FMoneyPosition.SHORT_NAME, "Деньги КЦБ ММВБ (RUR)")
				.withToken(FMoneyPosition.REGISTER, "T0")
				.withToken(FMoneyPosition.SALDO_IN, of("990196.7"))
				.withToken(FMoneyPosition.BOUGHT, of("0.1"))
				.withToken(FMoneyPosition.SOLD, of("0.2"))
				.withToken(FMoneyPosition.SALDO, of("990196.7"))
				.withToken(FMoneyPosition.ORD_BUY, of("0.3"))
				.withToken(FMoneyPosition.ORB_BUY_COND, of("0.4"))
				.withToken(FMoneyPosition.COMISSION, of("0.5"))
				.buildUpdate()));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testReadPositions_Case2() throws Exception {
		XMLStreamReader sr = startReader("fixture/positions2.xml", "positions");
		List<TQStateUpdate<? extends ID>> actual = service.readPositions(sr);
		sr.close();
		
		List<TQStateUpdate<? extends ID>> expected = new ArrayList<>();
		expected.add(new TQStateUpdate<>(new ID.MP("COOKIE", "FOND_MOEX", "T1"), new DeltaUpdateBuilder()
				.withToken(FMoneyPosition.MARKETS, toList(1, 5))
				.withToken(FMoneyPosition.REGISTER, "T1")
				.withToken(FMoneyPosition.ASSET, "FOND_MOEX")
				.withToken(FMoneyPosition.CLIENT_ID, "COOKIE")
				.withToken(FMoneyPosition.UNION_CODE, "UNITED-COOKIE")
				.withToken(FMoneyPosition.SHORT_NAME, "My money")
				.withToken(FMoneyPosition.SALDO_IN, of("1000.0"))
				.withToken(FMoneyPosition.BOUGHT, of("100.0"))
				.withToken(FMoneyPosition.SOLD, of("25.0"))
				.withToken(FMoneyPosition.SALDO, of("950.0"))
				.withToken(FMoneyPosition.ORD_BUY, of("10.05"))
				.withToken(FMoneyPosition.ORB_BUY_COND, of("5.13"))
				.withToken(FMoneyPosition.COMISSION, of("4.1"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new ID.SP("COOKIE", "GAZP", 1, "T1"), new DeltaUpdateBuilder()
				.withToken(FSecPosition.SEC_ID, 52013)
				.withToken(FSecPosition.MARKET_ID, 1)
				.withToken(FSecPosition.SEC_CODE, "GAZP")
				.withToken(FSecPosition.REGISTER, "T1")
				.withToken(FSecPosition.CLIENT_ID, "COOKIE")
				.withToken(FSecPosition.UNION_CODE, "UNITED-COOKIE")
				.withToken(FSecPosition.SHORT_NAME, "Газпром АО")
				.withToken(FSecPosition.SALDO_IN, of(5L))
				.withToken(FSecPosition.SALDO_MIN, of(1L))
				.withToken(FSecPosition.BOUGHT, of(3L))
				.withToken(FSecPosition.SOLD, of(9L))
				.withToken(FSecPosition.SALDO, of(4L))
				.withToken(FSecPosition.ORD_BUY, of(7L))
				.withToken(FSecPosition.ORD_SELL, of(8L))
				.withToken(FSecPosition.AMOUNT, of("205.12"))
				.withToken(FSecPosition.EQUITY, of("46.24"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new ID.FP("COOKIE", "RIH0", toSet(4, 8)), new DeltaUpdateBuilder()
				.withToken(FFortsPosition.SEC_ID, 44412)
				.withToken(FFortsPosition.MARKETS, toList(4, 8))
				.withToken(FFortsPosition.SEC_CODE, "RIH0")
				.withToken(FFortsPosition.CLIENT_ID, "COOKIE")
				.withToken(FFortsPosition.UNION_CODE, "UNITED-COOKIE")
				.withToken(FFortsPosition.START_NET, of(15L))
				.withToken(FFortsPosition.OPEN_BUYS, of(10L))
				.withToken(FFortsPosition.OPEN_SELLS, of(4L))
				.withToken(FFortsPosition.TOTAL_NET, of(84L))
				.withToken(FFortsPosition.TODAY_BUY, of(27L))
				.withToken(FFortsPosition.TODAY_SELL, of(19L))
				.withToken(FFortsPosition.OPT_MARGIN, of("115.23"))
				.withToken(FFortsPosition.VAR_MARGIN, of("295.1"))
				.withToken(FFortsPosition.EXPIRATION_POS, of(77681L))
				.withToken(FFortsPosition.USED_SELL_SPOT_LIMIT, of("54.26"))
				.withToken(FFortsPosition.SELL_SPOT_LIMIT, of("200.0"))
				.withToken(FFortsPosition.NETTO, of("2.15"))
				.withToken(FFortsPosition.KGO, of("0.05"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new ID.FM("COOKIE"), new DeltaUpdateBuilder()
				.withToken(FFortsMoney.CLIENT_ID, "COOKIE")
				.withToken(FFortsMoney.UNION_CODE, "UNITED-COOKIE")
				.withToken(FFortsMoney.MARKETS, toList(4, 6))
				.withToken(FFortsMoney.SHORT_NAME, "does not matter (part 1)")
				.withToken(FFortsMoney.CURRENT, of("280.8"))
				.withToken(FFortsMoney.BLOCKED, of("54.23"))
				.withToken(FFortsMoney.FREE, of("97.14"))
				.withToken(FFortsMoney.VAR_MARGIN, of("7176.2"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new ID.FC("COOKIE", toSet(9, 3)), new DeltaUpdateBuilder()
				.withToken(FFortsCollaterals.CLIENT_ID, "COOKIE")
				.withToken(FFortsCollaterals.UNION_CODE, "UNITED-COOKIE")
				.withToken(FFortsCollaterals.MARKETS, toList(9, 3))
				.withToken(FFortsCollaterals.SHORT_NAME, "does not matter (part 2)")
				.withToken(FFortsCollaterals.CURRENT, of("761.2"))
				.withToken(FFortsCollaterals.BLOCKED, of("872.1"))
				.withToken(FFortsCollaterals.FREE, of("581.72"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new ID.SL("COOKIE", toSet(14, 15)), new DeltaUpdateBuilder()
				.withToken(FSpotLimits.CLIENT_ID, "COOKIE")
				.withToken(FSpotLimits.UNION_CODE, "UNITED-COOKIE")
				.withToken(FSpotLimits.MARKETS, toList(14, 15))
				.withToken(FSpotLimits.SHORT_NAME, "does not matter (part 3)")
				.withToken(FSpotLimits.BUY_LIMIT, of("751.2"))
				.withToken(FSpotLimits.BUY_LIMIT_USED, of("1.54"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new ID.UL("KASUM-1234"), new DeltaUpdateBuilder()
				.withToken(FUnitedLimits.UNION_CODE, "KASUM-1234")
				.withToken(FUnitedLimits.OPEN_EQUITY, of("990196.70"))
				.withToken(FUnitedLimits.EQUITY, of("12651.61"))
				.withToken(FUnitedLimits.REQUIREMENTS, of("8135.1"))
				.withToken(FUnitedLimits.FREE, of("33561.12"))
				.withToken(FUnitedLimits.VAR_MARGIN, of("150.00"))
				.withToken(FUnitedLimits.FIN_RES, of("245416.89"))
				.withToken(FUnitedLimits.GO, of("0.02"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new ID.FM("pepiz86"), new DeltaUpdateBuilder()
				.withToken(FFortsMoney.CLIENT_ID, "pepiz86")
				.withToken(FFortsMoney.UNION_CODE, "KASUM-1234")
				.withToken(FFortsMoney.CURRENT, of("990196.70"))
				.withToken(FFortsMoney.FREE, of("990196.70"))
				.buildUpdate()));
		
		for ( int i = 0; i < expected.size(); i ++ ) {
			assertEquals("At#" + i, expected.get(i), actual.get(i));
		}
		assertEquals(expected.size(), actual.size());
	}

}

package ru.prolib.aquila.transaq.impl;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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
import ru.prolib.aquila.transaq.impl.TQParser;

public class TQParserTest {
	private static XMLInputFactory factory;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		factory = XMLInputFactory.newInstance();
	}
	
	private TQParser service;

	@Before
	public void setUp() throws Exception {
		service = new TQParser();
	}
	
	@Test
	public void testReadDate() throws Exception {
		String xml = new StringBuilder()
				.append("<foo>")
					.append("<date1>01.06.2019 07:46:15.882</date1>")
					.append("<date2>31.12.1978 14:20:12</date2>")
					.append("<date3>11:37:15</date3>")
				.append("</foo>")
				.toString();
		InputStream is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		LocalDateTime actual1 = null, actual2 = null, actual3 = null;
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
				}
				break;
			}
		}
		assertEquals(LocalDateTime.of(2019,  6,  1,  7, 46, 15, 882000000), actual1);
		assertEquals(LocalDateTime.of(1978, 12, 31, 14, 20, 12), actual2);
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 37, 15)), actual3);
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
				.withToken(TQMarketField.ID, 0)
				.withToken(TQMarketField.NAME, "Collateral")
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>(1, new DeltaUpdateBuilder()
				.withToken(TQMarketField.ID, 1)
				.withToken(TQMarketField.NAME, "MICEX")
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>(4, new DeltaUpdateBuilder()
				.withToken(TQMarketField.ID, 4)
				.withToken(TQMarketField.NAME, "FORTS")
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>(7, new DeltaUpdateBuilder()
				.withToken(TQMarketField.ID, 7)
				.withToken(TQMarketField.NAME, "SPBEX")
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>(8, new DeltaUpdateBuilder()
				.withToken(TQMarketField.ID, 8)
				.withToken(TQMarketField.NAME, "INF")
				.buildUpdate()
			));		
		expected.add(new TQStateUpdate<>(9, new DeltaUpdateBuilder()
				.withToken(TQMarketField.ID, 9)
				.withToken(TQMarketField.NAME, "9 [N/A]")
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>(12, new DeltaUpdateBuilder()
				.withToken(TQMarketField.ID, 12)
				.withToken(TQMarketField.NAME, "12 [N/A]")
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>(14, new DeltaUpdateBuilder()
				.withToken(TQMarketField.ID, 14)
				.withToken(TQMarketField.NAME, "MMA")
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>(15, new DeltaUpdateBuilder()
				.withToken(TQMarketField.ID, 15)
				.withToken(TQMarketField.NAME, "ETS")
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
				.withToken(TQBoardField.CODE, "AUCT")
				.withToken(TQBoardField.NAME, "Auction")
				.withToken(TQBoardField.MARKET_ID, 1)
				.withToken(TQBoardField.TYPE, 2)
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>("EQDB", new DeltaUpdateBuilder()
				.withToken(TQBoardField.CODE, "EQDB")
				.withToken(TQBoardField.NAME, "Main market: D bonds")
				.withToken(TQBoardField.MARKET_ID, 1)
				.withToken(TQBoardField.TYPE, 2)
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>("EQDP", new DeltaUpdateBuilder()
				.withToken(TQBoardField.CODE, "EQDP")
				.withToken(TQBoardField.NAME, "Dark Pool")
				.withToken(TQBoardField.MARKET_ID, 1)
				.withToken(TQBoardField.TYPE, 2)
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>("CNGD", new DeltaUpdateBuilder()
				.withToken(TQBoardField.CODE, "CNGD")
				.withToken(TQBoardField.NAME, "ETS Neg. deals")
				.withToken(TQBoardField.MARKET_ID, 15)
				.withToken(TQBoardField.TYPE, 2)
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>("INDEXE", new DeltaUpdateBuilder()
				.withToken(TQBoardField.CODE, "INDEXE")
				.withToken(TQBoardField.NAME, "ETS indexes")
				.withToken(TQBoardField.MARKET_ID, 15)
				.withToken(TQBoardField.TYPE, 2)
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>("ZLG", new DeltaUpdateBuilder()
				.withToken(TQBoardField.CODE, "ZLG")
				.withToken(TQBoardField.NAME, "Залоговые инструменты")
				.withToken(TQBoardField.MARKET_ID, 0)
				.withToken(TQBoardField.TYPE, 2)
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>("EQNL", new DeltaUpdateBuilder()
				.withToken(TQBoardField.CODE, "EQNL")
				.withToken(TQBoardField.NAME, "EQNL [N/A]")
				.withToken(TQBoardField.MARKET_ID, 255)
				.withToken(TQBoardField.TYPE, 2)
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>("AETS", new DeltaUpdateBuilder()
				.withToken(TQBoardField.CODE, "AETS")
				.withToken(TQBoardField.NAME, "Дополнительная сессия")
				.withToken(TQBoardField.MARKET_ID, 15)
				.withToken(TQBoardField.TYPE, 2)
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
				.withToken(TQCKindField.CKIND_ID, 1)
				.withToken(TQCKindField.CKIND_PERIOD, 60)
				.withToken(TQCKindField.CKIND_NAME, "1 minute")
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>(2, new DeltaUpdateBuilder()
				.withToken(TQCKindField.CKIND_ID, 2)
				.withToken(TQCKindField.CKIND_PERIOD, 300)
				.withToken(TQCKindField.CKIND_NAME, "5 minutes")
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>(3, new DeltaUpdateBuilder()
				.withToken(TQCKindField.CKIND_ID, 3)
				.withToken(TQCKindField.CKIND_PERIOD, 900)
				.withToken(TQCKindField.CKIND_NAME, "15 minutes")
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>(4, new DeltaUpdateBuilder()
				.withToken(TQCKindField.CKIND_ID, 4)
				.withToken(TQCKindField.CKIND_PERIOD, 3600)
				.withToken(TQCKindField.CKIND_NAME, "1 hour")
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>(5, new DeltaUpdateBuilder()
				.withToken(TQCKindField.CKIND_ID, 5)
				.withToken(TQCKindField.CKIND_PERIOD, 86400)
				.withToken(TQCKindField.CKIND_NAME, "1 day")
				.buildUpdate()
			));
		expected.add(new TQStateUpdate<>(6, new DeltaUpdateBuilder()
				.withToken(TQCKindField.CKIND_ID, 6)
				.withToken(TQCKindField.CKIND_PERIOD, 604800)
				.withToken(TQCKindField.CKIND_NAME, "1 week")
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
		List<TQStateUpdate<TQSecID_F>> actual = null;
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
		List<TQStateUpdate<TQSecID_F>> expected = new ArrayList<>();
		expected.add(new TQStateUpdate<>(
			new TQSecID_F("IRGZ", 1, "IrkutskEnrg", SecType.SHARE),
			new DeltaUpdateBuilder()
				.withToken(TQSecField.SECID, 0)
				.withToken(TQSecField.ACTIVE, true)
				.withToken(TQSecField.SECCODE, "IRGZ")
				.withToken(TQSecField.SECCLASS, "E")
				.withToken(TQSecField.DEFAULT_BOARDCODE, "TQBR")
				.withToken(TQSecField.MARKETID, 1)
				.withToken(TQSecField.SHORT_NAME, "IrkutskEnrg")
				.withToken(TQSecField.DECIMALS, 2)
				.withToken(TQSecField.MINSTEP, of("0.02"))
				.withToken(TQSecField.LOTSIZE, of("100"))
				.withToken(TQSecField.POINT_COST, of("1"))
				.withToken(TQSecField.OPMASK, 0x01 | 0x02 | 0x04 | 0x08 | 0x10)
				.withToken(TQSecField.SECTYPE, SecType.SHARE)
				.withToken(TQSecField.SECTZ, "Russian Standard Time")
				.withToken(TQSecField.QUOTESTYPE, 1)
				.buildUpdate())
			);
		expected.add(new TQStateUpdate<>(
			new TQSecID_F("RU000A0ZZ505", 1, "Russian Agricultural Bank 09T1", SecType.BOND),
			new DeltaUpdateBuilder()
				.withToken(TQSecField.SECID, 3)
				.withToken(TQSecField.ACTIVE, true)
				.withToken(TQSecField.SECCODE, "RU000A0ZZ505")
				.withToken(TQSecField.SECCLASS, "B")
				.withToken(TQSecField.DEFAULT_BOARDCODE, "EQOB")
				.withToken(TQSecField.MARKETID, 1)
				.withToken(TQSecField.SHORT_NAME, "Russian Agricultural Bank 09T1")
				.withToken(TQSecField.DECIMALS, 2)
				.withToken(TQSecField.MINSTEP, of("0.01"))
				.withToken(TQSecField.LOTSIZE, of("1"))
				.withToken(TQSecField.POINT_COST, of("10"))
				.withToken(TQSecField.OPMASK, 0x01 | 0x04 | 0x08 | 0x10)
				.withToken(TQSecField.SECTYPE, SecType.BOND)
				.withToken(TQSecField.SECTZ, "Russian Standard Time")
				.withToken(TQSecField.QUOTESTYPE, 1)
				.buildUpdate())
			);
		expected.add(new TQStateUpdate<>(
			new TQSecID_F("RIM9", 4, "RTS-6.19", SecType.FUT),
			new DeltaUpdateBuilder()
				.withToken(TQSecField.SECID, 41190)
				.withToken(TQSecField.ACTIVE, true)
				.withToken(TQSecField.SECCODE, "RIM9")
				.withToken(TQSecField.SECCLASS, "F")
				.withToken(TQSecField.DEFAULT_BOARDCODE, "FUT")
				.withToken(TQSecField.MARKETID, 4)
				.withToken(TQSecField.SHORT_NAME, "RTS-6.19")
				.withToken(TQSecField.DECIMALS, 0)
				.withToken(TQSecField.MINSTEP, of("10"))
				.withToken(TQSecField.LOTSIZE, of("1"))
				.withToken(TQSecField.POINT_COST, of("129.073"))
				.withToken(TQSecField.OPMASK, 0x02 | 0x10)
				.withToken(TQSecField.SECTYPE, SecType.FUT)
				.withToken(TQSecField.SECTZ, "Russian Standard Time")
				.withToken(TQSecField.QUOTESTYPE, 1)
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
		List<TQStateUpdate<TQSecID_F>> actual = null;
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
		List<TQStateUpdate<TQSecID_F>> expected = new ArrayList<>();
		expected.add(new TQStateUpdate<>(
			new TQSecID_F("IRGZ", 1, "IrkutskEnrg", SecType.QUOTES),
			new DeltaUpdateBuilder()
				.withToken(TQSecField.SECID, 0)
				.withToken(TQSecField.ACTIVE, true)
				.withToken(TQSecField.SECCODE, "IRGZ")
				.withToken(TQSecField.SECCLASS, "E")
				.withToken(TQSecField.DEFAULT_BOARDCODE, "TQBR")
				.withToken(TQSecField.MARKETID, 1)
				.withToken(TQSecField.SHORT_NAME, "IrkutskEnrg")
				.withToken(TQSecField.DECIMALS, 2)
				.withToken(TQSecField.MINSTEP, of("0.02"))
				.withToken(TQSecField.LOTSIZE, of("100"))
				.withToken(TQSecField.POINT_COST, of("1"))
				.withToken(TQSecField.OPMASK, 0x01 | 0x02 | 0x04 | 0x08 | 0x10)
				.withToken(TQSecField.SECTYPE, SecType.QUOTES)
				.withToken(TQSecField.SECTZ, "Russian Standard Time")
				.withToken(TQSecField.QUOTESTYPE, 1)
				.buildUpdate())
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testReadSecurities_Inactive() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/securities2.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		List<TQStateUpdate<TQSecID_F>> actual = null;
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
		List<TQStateUpdate<TQSecID_F>> expected = new ArrayList<>();
		expected.add(new TQStateUpdate<>(
			new TQSecID_F("IRGZ", 1, "IrkutskEnrg", SecType.SHARE),
			new DeltaUpdateBuilder()
				.withToken(TQSecField.SECID, 0)
				.withToken(TQSecField.ACTIVE, true)
				.withToken(TQSecField.SECCODE, "IRGZ")
				.withToken(TQSecField.SECCLASS, "E")
				.withToken(TQSecField.DEFAULT_BOARDCODE, "TQBR")
				.withToken(TQSecField.MARKETID, 1)
				.withToken(TQSecField.SHORT_NAME, "IrkutskEnrg")
				.withToken(TQSecField.DECIMALS, 2)
				.withToken(TQSecField.POINT_COST, of("1"))
				.withToken(TQSecField.SECTYPE, SecType.SHARE)
				.withToken(TQSecField.SECTZ, "Russian Standard Time")
				.withToken(TQSecField.QUOTESTYPE, 1)
				.buildUpdate())
			);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testReadSecInfo() throws Exception {
		InputStream is = new FileInputStream(new File("fixture/sec_info.xml"));
		XMLStreamReader sr = factory.createXMLStreamReader(is);
		TQStateUpdate<TQSecID1> actual = null;
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
		TQStateUpdate<TQSecID1> expected = new TQStateUpdate<>(
			new TQSecID1("FOO-12.35", 4),
			new DeltaUpdateBuilder()
				.withToken(TQSecField.SECID, 28334)
				.withToken(TQSecField.SECNAME, "FOOBAR")
				.withToken(TQSecField.SECCODE, "FOO-12.35")
				.withToken(TQSecField.MARKETID, 4)
				.withToken(TQSecField.PNAME, "pcs.")
				.withToken(TQSecField.MAT_DATE, LocalDateTime.of(2019, 6, 1, 6, 26, 15))
				.withToken(TQSecField.CLEARING_PRICE, of("203.082"))
				.withToken(TQSecField.MINPRICE, of("100.000"))
				.withToken(TQSecField.MAXPRICE, of("300.000"))
				.withToken(TQSecField.BUY_DEPOSIT, of("278991.92"))
				.withToken(TQSecField.SELL_DEPOSIT, of("728001.10"))
				.withToken(TQSecField.BGO_C, of("79.03"))
				.withToken(TQSecField.BGO_NC, of("86.12"))
				.withToken(TQSecField.ACCRUED_INT, of("0.02"))
				.withToken(TQSecField.COUPON_VALUE, of("192.77"))
				.withToken(TQSecField.COUPON_DATE, LocalDateTime.of(2019, 12, 31, 0, 0, 0))
				.withToken(TQSecField.COUPON_PERIOD, 12)
				.withToken(TQSecField.FACE_VALUE, of("1000.00"))
				.withToken(TQSecField.PUT_CALL, "P")
				.withToken(TQSecField.OPT_TYPE, "M")
				.withToken(TQSecField.LOT_VOLUME, 1)
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
		TQStateUpdate<TQSecID1> actual = null;
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
		TQStateUpdate<TQSecID1> expected = new TQStateUpdate<>(
			new TQSecID1("BRH0", 4),
			new DeltaUpdateBuilder()
				.withToken(TQSecField.SECID, 66)
				.withToken(TQSecField.SECCODE, "BRH0")
				.withToken(TQSecField.MARKETID, 4)
				.withToken(TQSecField.BUY_DEPOSIT, of("6132.61"))
				.withToken(TQSecField.SELL_DEPOSIT, of("6467.30"))
				.withToken(TQSecField.MINPRICE, of("61.97"))
				.withToken(TQSecField.MAXPRICE, of("67.19"))
				.withToken(TQSecField.POINT_COST, of("7625.71"))
				.withToken(TQSecField.BGO_C, of("811.44"))
				.withToken(TQSecField.BGO_NC, of("4640.88"))
				.withToken(TQSecField.BGO_BUY, of("4605.53"))
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
							List<TQStateUpdate<TQSecID_F>> list = service.readSecurities(sr);
							count_securities += list.size();
							for  ( TQStateUpdate<TQSecID_F> s : list ) {
								if ( "RTS-6.19".equals(s.getUpdate().getContents().get(TQSecField.SHORT_NAME)) ) {
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
		List<TQStateUpdate<TQSecID2>> actual = null;
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
		List<TQStateUpdate<TQSecID2>> expected = new ArrayList<>();
		expected.add(new TQStateUpdate<>(new TQSecID2("IRGZ", "TQBR"), new DeltaUpdateBuilder()
				.withToken(TQSecField.SECCODE, "IRGZ")
				.withToken(TQBoardField.CODE, "TQBR")
				.withToken(TQSecField.MARKETID, 1)
				.withToken(TQSecField.DECIMALS, 2)
				.withToken(TQSecField.MINSTEP, of("0.02"))
				.withToken(TQSecField.LOTSIZE, of("100"))
				.withToken(TQSecField.POINT_COST, of("1"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecID2("IRGZ", "SMAL"), new DeltaUpdateBuilder()
				.withToken(TQSecField.SECCODE, "IRGZ")
				.withToken(TQBoardField.CODE, "SMAL")
				.withToken(TQSecField.MARKETID, 1)
				.withToken(TQSecField.DECIMALS, 2)
				.withToken(TQSecField.MINSTEP, of("0.02"))
				.withToken(TQSecField.LOTSIZE, of("1"))
				.withToken(TQSecField.POINT_COST, of("1"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecID2("IRGZ", "EQRP"), new DeltaUpdateBuilder()
				.withToken(TQSecField.SECCODE, "IRGZ")
				.withToken(TQBoardField.CODE, "EQRP")
				.withToken(TQSecField.MARKETID, 1)
				.withToken(TQSecField.DECIMALS, 4)
				.withToken(TQSecField.MINSTEP, of("0.01"))
				.withToken(TQSecField.LOTSIZE, of("100"))
				.withToken(TQSecField.POINT_COST, of("0.01"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecID2("IRGZ", "PSEQ"), new DeltaUpdateBuilder()
				.withToken(TQSecField.SECCODE, "IRGZ")
				.withToken(TQBoardField.CODE, "PSEQ")
				.withToken(TQSecField.MARKETID, 1)
				.withToken(TQSecField.DECIMALS, 2)
				.withToken(TQSecField.MINSTEP, of("0.02"))
				.withToken(TQSecField.LOTSIZE, of("100"))
				.withToken(TQSecField.POINT_COST, of("1"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecID2("IRGZ", "PSRP"), new DeltaUpdateBuilder()
				.withToken(TQSecField.SECCODE, "IRGZ")
				.withToken(TQBoardField.CODE, "PSRP")
				.withToken(TQSecField.MARKETID, 1)
				.withToken(TQSecField.DECIMALS, 4)
				.withToken(TQSecField.MINSTEP, of("0.01"))
				.withToken(TQSecField.LOTSIZE, of("100"))
				.withToken(TQSecField.POINT_COST, of("0.01"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecID2("IRGZ", "PTEQ"), new DeltaUpdateBuilder()
				.withToken(TQSecField.SECCODE, "IRGZ")
				.withToken(TQBoardField.CODE, "PTEQ")
				.withToken(TQSecField.MARKETID, 1)
				.withToken(TQSecField.DECIMALS, 2)
				.withToken(TQSecField.MINSTEP, of("0.02"))
				.withToken(TQSecField.LOTSIZE, of("100"))
				.withToken(TQSecField.POINT_COST, of("1"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecID2("IRGZ", "RPEU"), new DeltaUpdateBuilder()
				.withToken(TQSecField.SECCODE, "IRGZ")
				.withToken(TQBoardField.CODE, "RPEU")
				.withToken(TQSecField.MARKETID, 1)
				.withToken(TQSecField.DECIMALS, 6)
				.withToken(TQSecField.MINSTEP, of("0.000001"))
				.withToken(TQSecField.LOTSIZE, of("1"))
				.withToken(TQSecField.POINT_COST, of("0.006429"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecID2("IRGZ", "RPMA"), new DeltaUpdateBuilder()
				.withToken(TQSecField.SECCODE, "IRGZ")
				.withToken(TQBoardField.CODE, "RPMA")
				.withToken(TQSecField.MARKETID, 1)
				.withToken(TQSecField.DECIMALS, 6)
				.withToken(TQSecField.MINSTEP, of("0.000001"))
				.withToken(TQSecField.LOTSIZE, of("1"))
				.withToken(TQSecField.POINT_COST, of("0.0001"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecID2("IRGZ", "RPMO"), new DeltaUpdateBuilder()
				.withToken(TQSecField.SECCODE, "IRGZ")
				.withToken(TQBoardField.CODE, "RPMO")
				.withToken(TQSecField.MARKETID, 1)
				.withToken(TQSecField.DECIMALS, 6)
				.withToken(TQSecField.MINSTEP, of("0.000001"))
				.withToken(TQSecField.LOTSIZE, of("1"))
				.withToken(TQSecField.POINT_COST, of("0.0001"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecID2("RU000A0ZZ505", "EQOB"), new DeltaUpdateBuilder()
				.withToken(TQSecField.SECCODE, "RU000A0ZZ505")
				.withToken(TQBoardField.CODE, "EQOB")
				.withToken(TQSecField.MARKETID, 1)
				.withToken(TQSecField.DECIMALS, 2)
				.withToken(TQSecField.MINSTEP, of("0.01"))
				.withToken(TQSecField.LOTSIZE, of("1"))
				.withToken(TQSecField.POINT_COST, of("10"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecID2("RU000A0ZZ505", "PSOB"), new DeltaUpdateBuilder()
				.withToken(TQSecField.SECCODE, "RU000A0ZZ505")
				.withToken(TQBoardField.CODE, "PSOB")
				.withToken(TQSecField.MARKETID, 1)
				.withToken(TQSecField.DECIMALS, 2)
				.withToken(TQSecField.MINSTEP, of("0.01"))
				.withToken(TQSecField.LOTSIZE, of("1"))
				.withToken(TQSecField.POINT_COST, of("10"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecID2("RU000A0ZZ505", "RPEU"), new DeltaUpdateBuilder()
				.withToken(TQSecField.SECCODE, "RU000A0ZZ505")
				.withToken(TQBoardField.CODE, "RPEU")
				.withToken(TQSecField.MARKETID, 1)
				.withToken(TQSecField.DECIMALS, 6)
				.withToken(TQSecField.MINSTEP, of("0.000001"))
				.withToken(TQSecField.LOTSIZE, of("1"))
				.withToken(TQSecField.POINT_COST, of("0.001"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecID2("RU000A0ZZ505", "RPMO"), new DeltaUpdateBuilder()
				.withToken(TQSecField.SECCODE, "RU000A0ZZ505")
				.withToken(TQBoardField.CODE, "RPMO")
				.withToken(TQSecField.MARKETID, 1)
				.withToken(TQSecField.DECIMALS, 4)
				.withToken(TQSecField.MINSTEP, of("0.0001"))
				.withToken(TQSecField.LOTSIZE, of("1"))
				.withToken(TQSecField.POINT_COST, of("0.1"))
				.buildUpdate()));
		expected.add(new TQStateUpdate<>(new TQSecID2("PRTK", "TQBR"), new DeltaUpdateBuilder()
				.withToken(TQSecField.SECCODE, "PRTK")
				.withToken(TQBoardField.CODE, "TQBR")
				.withToken(TQSecField.MARKETID, 1)
				.withToken(TQSecField.DECIMALS, 1)
				.withToken(TQSecField.MINSTEP, of("0.1"))
				.withToken(TQSecField.LOTSIZE, of("10"))
				.withToken(TQSecField.POINT_COST, of("10"))
				.buildUpdate()));
		assertEquals(expected, actual);
	}

}

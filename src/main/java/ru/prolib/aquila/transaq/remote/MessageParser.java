package ru.prolib.aquila.transaq.remote;

import static ru.prolib.aquila.transaq.impl.TQOpmask.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.transaq.entity.SecType;
import ru.prolib.aquila.transaq.impl.TQStateUpdate;
import ru.prolib.aquila.transaq.remote.MessageFields.FBoard;
import ru.prolib.aquila.transaq.remote.MessageFields.FCKind;
import ru.prolib.aquila.transaq.remote.MessageFields.FClient;
import ru.prolib.aquila.transaq.remote.MessageFields.FMarket;
import ru.prolib.aquila.transaq.remote.MessageFields.FQuotation;
import ru.prolib.aquila.transaq.remote.MessageFields.FSecurity;
import ru.prolib.aquila.transaq.remote.MessageFields.FSecurityBoard;
import ru.prolib.aquila.transaq.remote.MessageFields.FTrade;
import ru.prolib.aquila.transaq.remote.entity.Quote;
import ru.prolib.aquila.transaq.remote.entity.ServerStatus;

public class MessageParser {
	private static final Logger logger;
	private static final MessageParser instance;
	
	static {
		logger = LoggerFactory.getLogger(MessageParser.class);
		instance = new MessageParser();
	}
	
	public static MessageParser getInstance() {
		return instance;
	}
	
	/**
	 * Check value is not null or throw an exception.
	 * <p>
	 * @param value - value to check
	 * @param attr_name - property name to use for exception message
	 * @throws XMLStreamException value is null
	 */
	void checkNotNull(Object value, String attr_name) throws XMLStreamException {
		if ( value == null ) {
			throw new XMLStreamException("Value undefined: " + attr_name);
		}
	}
	
	private String getAttribute(XMLStreamReader reader, String attr_id) throws XMLStreamException {
		 return reader.getAttributeValue(null, attr_id);
	}
	
	private int getAttributeInt(XMLStreamReader reader, String attr_id) throws XMLStreamException {
		String str_val = getAttribute(reader, attr_id);
		try {
			return str_val == null ? null : Integer.parseInt(str_val);
		} catch ( NumberFormatException e ) {
			throw new XMLStreamException("Cannot parse int: " + str_val, e);
		}
	}
	
	private boolean getAttributeBool(XMLStreamReader reader, String attr_id, String true_string)
			throws XMLStreamException
	{
		String str_val = getAttribute(reader, attr_id);
		return str_val.equals(true_string);
	}
	
	private boolean getAttributeBool(XMLStreamReader reader, String attr_id) throws XMLStreamException {
		return getAttributeBool(reader, attr_id, "true");
	}
	
	private CDecimal readDecimal(XMLStreamReader reader) throws XMLStreamException {
		return CDecimalBD.of(readCharacters(reader));
	}
	
	public void skipElement(XMLStreamReader reader) throws XMLStreamException {
		String tag_name = reader.getLocalName();
		if ( reader.getEventType() == XMLStreamReader.END_ELEMENT ) {
			return;
		}
		int level_index = 0;
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				if ( reader.getLocalName().equals(tag_name) ) {
					level_index ++;
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				if ( reader.getLocalName().equals(tag_name) ) {
					if ( level_index == 0 ) {
						return;
					} else {
						level_index --;
					}
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	public String readCharacters(XMLStreamReader reader) throws XMLStreamException {
		StringBuilder result = new StringBuilder();
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
				case XMLStreamReader.CHARACTERS:
				case XMLStreamReader.CDATA:
					result.append(reader.getText());
					break;
				case XMLStreamReader.END_ELEMENT:
					return result.toString();
			}
		}
		throw new XMLStreamException("Premature end of file");
    }
	
	private int readInt(XMLStreamReader reader) throws XMLStreamException {
		String str_integer = readCharacters(reader);
		try {
			return Integer.parseInt(str_integer);
		} catch ( NumberFormatException e ) {
			throw new XMLStreamException("Cannot parse int: " + str_integer, e);
		}
	}
	
	private long readLong(XMLStreamReader reader) throws XMLStreamException {
		String str_value = readCharacters(reader);
		try {
			return Long.parseLong(str_value);
		} catch ( NumberFormatException e ) {
			throw new XMLStreamException("Cannot parse long: " + str_value, e);
		}
	}
	
	public LocalDateTime readDate(XMLStreamReader reader) throws XMLStreamException {
		String str_date = readCharacters(reader);
		int len = str_date.length();
		if ( len == 8 ) {
			return LocalDateTime.of(
					LocalDate.now(),
					LocalTime.parse(str_date, DateTimeFormatter.ofPattern("HH:mm:ss"))
				);
		}
		if ( len == 12  ) {
			return LocalDateTime.of(
					LocalDate.now(),
					LocalTime.parse(str_date, DateTimeFormatter.ofPattern("HH:mm:ss.SSS"))
				);
		}
		if ( len > 19 && ".".equals(str_date.substring(len - 4, len - 3)) ) {
			return LocalDateTime.parse(str_date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS"));
		}
		return LocalDateTime.parse(str_date,DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
	}
	
	private TQStateUpdate<Integer> readMarket(XMLStreamReader reader) throws XMLStreamException {
		String str_id = reader.getAttributeValue(null, "id");
		String str_name = readCharacters(reader).trim();
		if ( str_name.length() == 0 ) {
			str_name = str_id + " [N/A]";
		}
		try {
			int market_id = Integer.parseInt(str_id);
			return new TQStateUpdate<Integer>(market_id, new DeltaUpdateBuilder()
					.withToken(FMarket.ID, market_id)
					.withToken(FMarket.NAME, str_name)
					.buildUpdate()
				);
		} catch ( NumberFormatException e ) {
			throw new XMLStreamException("Cannot parse ID: " + str_id, e);
		}
	}
	
	public List<TQStateUpdate<Integer>> readMarkets(XMLStreamReader reader) throws XMLStreamException {
		if ( ! "markets".equals(reader.getLocalName()) ) {
			throw new IllegalStateException("Unexpected current element: " + reader.getLocalName());
		}
		List<TQStateUpdate<Integer>> result = new ArrayList<>();
		while ( reader.hasNext() ) {
        	switch ( reader.next() ) {
        	case XMLStreamReader.START_ELEMENT:
        		switch ( reader.getLocalName() ) {
        		case "market":
        			result.add(readMarket(reader));
        			break;
        		}
        		break;
        	case XMLStreamReader.END_ELEMENT:
        		switch ( reader.getLocalName() ) {
        		case "markets":
        			return result;
        		}
        		break;
        	}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	private TQStateUpdate<String> readBoard(XMLStreamReader reader) throws XMLStreamException {
		String str_code = reader.getAttributeValue(null, "id");
		String str_name = null;
		int market = -1, type = -1;
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "name":
					str_name = readCharacters(reader);
					break;
				case "market":
					market = readInt(reader);
					break;
				case "type":
					type = readInt(reader);
					break;
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "board":
					if ( str_name == null || str_name.length() == 0 ) {
						str_name = str_code + " [N/A]";
					}
					return new TQStateUpdate<>(str_code, new DeltaUpdateBuilder()
							.withToken(FBoard.CODE, str_code)
							.withToken(FBoard.MARKET_ID, market)
							.withToken(FBoard.NAME, str_name)
							.withToken(FBoard.TYPE, type)
							.buildUpdate()
						);
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	public List<TQStateUpdate<String>> readBoards(XMLStreamReader reader) throws XMLStreamException {
		if ( ! "boards".equals(reader.getLocalName()) ) {
			throw new IllegalStateException("Unexpected current element: " + reader.getLocalName());
		}
		List<TQStateUpdate<String>> result = new ArrayList<>();
		while ( reader.hasNext() ) {
        	switch ( reader.next() ) {
        	case XMLStreamReader.START_ELEMENT:
        		switch ( reader.getLocalName() ) {
        		case "board":
        			result.add(readBoard(reader));
        			break;
        		}
        		break;
        	case XMLStreamReader.END_ELEMENT:
        		switch ( reader.getLocalName() ) {
        		case "boards":
        			return result;
        		}
        		break;
        	}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	private TQStateUpdate<Integer> readCandleKind(XMLStreamReader reader) throws XMLStreamException {
		int id = -1, period = -1;
		String name = null;
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "id":
					id = readInt(reader);
					break;
				case "period":
					period = readInt(reader);
					break;
				case "name":
					name = readCharacters(reader);
					break;
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "kind":
					if ( name == null || name.length() == 0 ) {
						name = id + " [N/A]";
					}
					return new TQStateUpdate<Integer>(id, new DeltaUpdateBuilder()
							.withToken(FCKind.CKIND_ID, id)
							.withToken(FCKind.CKIND_PERIOD, period)
							.withToken(FCKind.CKIND_NAME, name)
							.buildUpdate()
						);
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	public List<TQStateUpdate<Integer>> readCandleKinds(XMLStreamReader reader) throws XMLStreamException {
		if ( ! "candlekinds".equals(reader.getLocalName()) ) {
			throw new IllegalStateException("Unexpected current element: " + reader.getLocalName());
		}
		List<TQStateUpdate<Integer>> result = new ArrayList<>();
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "kind":
					result.add(readCandleKind(reader));
					break;
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "candlekinds":
					return result;
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	private TQStateUpdate<ISecIDF> readSecurity(XMLStreamReader reader) throws XMLStreamException {
		Integer market_id = null;
		String sec_code = null, short_name = null, default_board = null;
		SecType sec_type = null;
		DeltaUpdateBuilder builder = new DeltaUpdateBuilder()
				.withToken(FSecurity.SECID, getAttributeInt(reader, "secid"))
				.withToken(FSecurity.ACTIVE, getAttributeBool(reader, "active"));
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "seccode":
					builder.withToken(FSecurity.SECCODE, sec_code = readCharacters(reader));
					break;
				case "instrclass":
					builder.withToken(FSecurity.SECCLASS, readCharacters(reader));
					break;
				case "board":
					builder.withToken(FSecurity.DEFAULT_BOARDCODE, default_board = readCharacters(reader));
					break;
				case "market":
					builder.withToken(FSecurity.MARKETID, market_id = readInt(reader));
					break;
				case "shortname":
					builder.withToken(FSecurity.SHORT_NAME, short_name = readCharacters(reader));
					break;
				case "decimals":
					builder.withToken(FSecurity.DECIMALS, readInt(reader));
					break;
				case "minstep":
					builder.withToken(FSecurity.MINSTEP, readDecimal(reader));
					break;
				case "lotsize":
					builder.withToken(FSecurity.LOTSIZE, readDecimal(reader));
					break;
				case "point_cost":
					builder.withToken(FSecurity.POINT_COST, readDecimal(reader));
					break;
				case "opmask":
					int opmask = 0;
					opmask |= getAttributeBool(reader, "usecredit", "yes") ? OPMASK_USECREDIT : 0;
					opmask |= getAttributeBool(reader, "bymarket", "yes") ? OPMASK_BYMARKET : 0;
					opmask |= getAttributeBool(reader, "nosplit", "yes") ? OPMASK_NOSPLIT : 0;
					opmask |= getAttributeBool(reader, "fok", "yes") ? OPMASK_FOK : 0;
					opmask |= getAttributeBool(reader, "ioc", "yes") ? OPMASK_IOC : 0;
					reader.nextTag();
					builder.withToken(FSecurity.OPMASK, opmask);
					break;
				case "sectype":
					String str_sec_type = readCharacters(reader);
					try {
						sec_type = SecType.valueOf(str_sec_type);
					} catch ( IllegalArgumentException e ) {
						String msg = e.getMessage();
						if ( msg != null && msg.startsWith("No enum constant") ) {
							logger.warn("Security type constant is undefined: " + str_sec_type);
							sec_type = SecType.QUOTES;
						} else {
							throw e;
						}
					}
					builder.withToken(FSecurity.SECTYPE, sec_type);
					break;
				case "sec_tz":
					builder.withToken(FSecurity.SECTZ, readCharacters(reader));
					break;
				case "quotestype":
					builder.withToken(FSecurity.QUOTESTYPE, readInt(reader));
					break;
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "security":
					checkNotNull(sec_code, "seccode");
					checkNotNull(market_id, "market");
					checkNotNull(short_name, "shortname");
					checkNotNull(sec_type, "sectype");
					checkNotNull(default_board, "board");
					return new TQStateUpdate<>(
							new TQSecIDF(sec_code, market_id, default_board, short_name, sec_type),
							builder.buildUpdate()
						);
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	public List<TQStateUpdate<ISecIDF>> readSecurities(XMLStreamReader reader) throws XMLStreamException {
		if ( ! "securities".equals(reader.getLocalName()) ) {
			throw new IllegalStateException("Unexpected current element: " + reader.getLocalName());
		}
		List<TQStateUpdate<ISecIDF>> result = new ArrayList<>();
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "security":
					result.add(readSecurity(reader));
					break;
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "securities":
					return result;
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	public TQStateUpdate<ISecIDG> readSecInfo(XMLStreamReader reader) throws XMLStreamException {
		if ( ! "sec_info".equals(reader.getLocalName()) ) {
			throw new IllegalStateException("Unexpected current element: " + reader.getLocalName());
		}
		Integer market_id = null;
		String sec_code = null;
		DeltaUpdateBuilder builder = new DeltaUpdateBuilder()
				.withToken(FSecurity.SECID, getAttributeInt(reader, "secid"));
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "secname":
					builder.withToken(FSecurity.SECNAME, readCharacters(reader));
					break;
				case "seccode":
					builder.withToken(FSecurity.SECCODE, sec_code = readCharacters(reader));
					break;
				case "market":
					builder.withToken(FSecurity.MARKETID,  market_id = readInt(reader));
					break;
				case "pname":
					builder.withToken(FSecurity.PNAME, readCharacters(reader));
					break;
				case "mat_date":
					builder.withToken(FSecurity.MAT_DATE, readDate(reader));
					break;
				case "clearing_price":
					builder.withToken(FSecurity.CLEARING_PRICE, readDecimal(reader));
					break;
				case "minprice":
					builder.withToken(FSecurity.MINPRICE, readDecimal(reader));
					break;
				case "maxprice":
					builder.withToken(FSecurity.MAXPRICE, readDecimal(reader));
					break;
				case "buy_deposit":
					builder.withToken(FSecurity.BUY_DEPOSIT, readDecimal(reader));
					break;
				case "sell_deposit":
					builder.withToken(FSecurity.SELL_DEPOSIT, readDecimal(reader));
					break;
				case "bgo_c":
					builder.withToken(FSecurity.BGO_C, readDecimal(reader));
					break;
				case "bgo_nc":
					builder.withToken(FSecurity.BGO_NC, readDecimal(reader));
					break;
				case "accruedint":
					builder.withToken(FSecurity.ACCRUED_INT, readDecimal(reader));
					break;
				case "coupon_value":
					builder.withToken(FSecurity.COUPON_VALUE, readDecimal(reader));
					break;
				case "coupon_date":
					builder.withToken(FSecurity.COUPON_DATE, readDate(reader));
					break;
				case "coupon_period":
					builder.withToken(FSecurity.COUPON_PERIOD, readInt(reader));
					break;
				case "facevalue":
					builder.withToken(FSecurity.FACE_VALUE, readDecimal(reader));
					break;
				case "put_call":
					builder.withToken(FSecurity.PUT_CALL, readCharacters(reader));
					break;
				case "opt_type":
					builder.withToken(FSecurity.OPT_TYPE, readCharacters(reader));
					break;
				case "lot_volume":
					builder.withToken(FSecurity.LOT_VOLUME, readInt(reader));
					break;
				default:
					break;
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "sec_info":
					checkNotNull(sec_code, "seccode");
					checkNotNull(market_id, "market");
					return new TQStateUpdate<>(new TQSecIDG(sec_code,market_id), builder.buildUpdate());
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	public TQStateUpdate<ISecIDG> readSecInfoUpd(XMLStreamReader reader) throws XMLStreamException {
		if ( ! "sec_info_upd".equals(reader.getLocalName()) ) {
			throw new IllegalStateException("Unexpected current element: " + reader.getLocalName());
		}
		Integer market_id = null;
		String sec_code = null;
		DeltaUpdateBuilder builder = new DeltaUpdateBuilder();
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "secid":
					builder.withToken(FSecurity.SECID, readInt(reader));
					break;
				case "market":
					builder.withToken(FSecurity.MARKETID, market_id = readInt(reader));
					break;
				case "seccode":
					builder.withToken(FSecurity.SECCODE, sec_code = readCharacters(reader));
					break;
				case "minprice":
					builder.withToken(FSecurity.MINPRICE, readDecimal(reader));
					break;
				case "maxprice":
					builder.withToken(FSecurity.MAXPRICE, readDecimal(reader));
					break;
				case "buy_deposit":
					builder.withToken(FSecurity.BUY_DEPOSIT, readDecimal(reader));
					break;
				case "sell_deposit":
					builder.withToken(FSecurity.SELL_DEPOSIT, readDecimal(reader));
					break;
				case "bgo_c":
					builder.withToken(FSecurity.BGO_C, readDecimal(reader));
					break;
				case "bgo_nc":
					builder.withToken(FSecurity.BGO_NC, readDecimal(reader));
					break;
				case "bgo_buy":
					builder.withToken(FSecurity.BGO_BUY, readDecimal(reader));
					break;
				case "point_cost":
					builder.withToken(FSecurity.POINT_COST, readDecimal(reader));
					break;
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "sec_info_upd":
					checkNotNull(sec_code, "seccode");
					checkNotNull(market_id, "market");
					return new TQStateUpdate<>(new TQSecIDG(sec_code, market_id), builder.buildUpdate());
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	private TQStateUpdate<ISecIDT> readPit(XMLStreamReader reader) throws XMLStreamException {
		Integer market_id = null;
		String sec_code = null, board_code = null;
		DeltaUpdateBuilder builder = new DeltaUpdateBuilder()
				.withToken(FSecurityBoard.SECCODE, sec_code = getAttribute(reader, "seccode"))
				.withToken(FSecurityBoard.BOARD, board_code = getAttribute(reader, "board"));
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "market":
					builder.withToken(FSecurityBoard.MARKET, market_id = readInt(reader));
					break;
				case "decimals":
					builder.withToken(FSecurityBoard.DECIMALS, readInt(reader));
					break;
				case "minstep":
					builder.withToken(FSecurityBoard.MINSTEP, readDecimal(reader));
					break;
				case "lotsize":
					builder.withToken(FSecurityBoard.LOTSIZE, readDecimal(reader));
					break;
				case "point_cost":
					builder.withToken(FSecurityBoard.POINT_COST, readDecimal(reader));
					break;
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "pit":
					checkNotNull(sec_code, "seccode");
					checkNotNull(board_code, "board");
					checkNotNull(market_id, "market");
					return new TQStateUpdate<>(new TQSecIDT(sec_code, board_code), builder.buildUpdate());
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}

	public List<TQStateUpdate<ISecIDT>> readPits(XMLStreamReader reader) throws XMLStreamException {
		if ( ! "pits".equals(reader.getLocalName()) ) {
			throw new IllegalStateException("Unexpected current element: " + reader.getLocalName());
		}
		List<TQStateUpdate<ISecIDT>> result = new ArrayList<>();
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "pit":
					result.add(readPit(reader));
					break;
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "pits":
					return result;
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	private TQStateUpdate<ISecIDT> readQuotation(XMLStreamReader reader) throws XMLStreamException {
		String sec_code = null, board_code = null;
		DeltaUpdateBuilder builder = new DeltaUpdateBuilder()
				.withToken(FQuotation.SECID, getAttributeInt(reader, "secid"));
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "board":
					builder.withToken(FQuotation.BOARD, board_code = readCharacters(reader));
					break;
				case "seccode":
					builder.withToken(FQuotation.SECCODE, sec_code = readCharacters(reader));
					break;
				case "point_cost":
					builder.withToken(FQuotation.POINT_COST, readDecimal(reader));
					break;
				case "accruedintvalue":
					builder.withToken(FQuotation.ACCRUED_INT_VALUE, readDecimal(reader));
					break;
				case "open":
					builder.withToken(FQuotation.OPEN, readDecimal(reader));
					break;
				case "waprice":
					builder.withToken(FQuotation.WA_PRICE, readDecimal(reader));
					break;
				case "biddepth":
					builder.withToken(FQuotation.BID_DEPTH, readInt(reader));
					break;
				case "biddeptht":
					builder.withToken(FQuotation.BID_DEPTH_T, readInt(reader));
					break;
				case "numbids":
					builder.withToken(FQuotation.NUM_BIDS, readInt(reader));
					break;
				case "offerdepth":
					builder.withToken(FQuotation.OFFER_DEPTH, readInt(reader));
					break;
				case "offerdeptht":
					builder.withToken(FQuotation.OFFER_DEPTH_T, readInt(reader));
					break;
				case "bid":
					builder.withToken(FQuotation.BID, readDecimal(reader));
					break;
				case "offer":
					builder.withToken(FQuotation.OFFER, readDecimal(reader));
					break;
				case "numoffers":
					builder.withToken(FQuotation.NUM_OFFERS, readInt(reader));
					break;
				case "numtrades":
					builder.withToken(FQuotation.NUM_TRADES, readInt(reader));
					break;
				case "voltoday":
					builder.withToken(FQuotation.VOL_TODAY, readInt(reader));
					break;
				case "openpositions":
					builder.withToken(FQuotation.OPEN_POSITIONS, readInt(reader));
					break;
				case "deltapositions":
					builder.withToken(FQuotation.DELTA_POSITIONS, readInt(reader));
					break;
				case "last":
					builder.withToken(FQuotation.LAST, readDecimal(reader));
					break;
				case "quantity":
					builder.withToken(FQuotation.QUANTITY, readInt(reader));
					break;
				case "time":
					builder.withToken(FQuotation.TIME, readDate(reader));
					break;
				case "change":
					builder.withToken(FQuotation.CHANGE, readDecimal(reader));
					break;
				case "priceminusprevwaprice":
					builder.withToken(FQuotation.PRICE_MINUS_PREV_WA_PRICE, readDecimal(reader));
					break;
				case "valtoday":
					builder.withToken(FQuotation.VAL_TODAY, readDecimal(reader));
					break;
				case "yield":
					builder.withToken(FQuotation.YIELD, readDecimal(reader));
					break;
				case "yieldatwaprice":
					builder.withToken(FQuotation.YIELD_AT_WA_PRICE, readDecimal(reader));
					break;
				case "marketpricetoday":
					builder.withToken(FQuotation.MARKET_PRICE_TODAY, readDecimal(reader));
					break;
				case "highbid":
					builder.withToken(FQuotation.HIGH_BID, readDecimal(reader));
					break;
				case "lowoffer":
					builder.withToken(FQuotation.LOW_OFFER, readDecimal(reader));
					break;
				case "high":
					builder.withToken(FQuotation.HIGH, readDecimal(reader));
					break;
				case "low":
					builder.withToken(FQuotation.LOW, readDecimal(reader));
					break;
				case "closeprice":
					builder.withToken(FQuotation.CLOSE_PRICE, readDecimal(reader));
					break;
				case "closeyield":
					builder.withToken(FQuotation.CLOSE_YIELD, readDecimal(reader));
					break;
				case "status":
					builder.withToken(FQuotation.STATUS, readCharacters(reader));
					break;
				case "tradingstatus":
					builder.withToken(FQuotation.TRADING_STATUS, readCharacters(reader));
					break;
				case "buydeposit":
					builder.withToken(FQuotation.BUY_DEPOSIT, readDecimal(reader));
					break;
				case "selldeposit":
					builder.withToken(FQuotation.SELL_DEPOSIT, readDecimal(reader));
					break;
				case "volatility":
					builder.withToken(FQuotation.VOLATILITY, readDecimal(reader));
					break;
				case "theoreticalprice":
					builder.withToken(FQuotation.THEORETICAL_PRICE, readDecimal(reader));
					break;
				case "bgo_buy":
					builder.withToken(FQuotation.BGO_BUY, readDecimal(reader));
					break;
				case "lcurrentprice":
					builder.withToken(FQuotation.L_CURRENT_PRICE, readDecimal(reader));
					break;
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "quotation":
					checkNotNull(sec_code, "seccode");
					checkNotNull(board_code, "board");
					return new TQStateUpdate<>(new TQSecIDT(sec_code, board_code), builder.buildUpdate());
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	public List<TQStateUpdate<ISecIDT>> readQuotations(XMLStreamReader reader) throws XMLStreamException {
		if ( ! "quotations".equals(reader.getLocalName()) ) {
			throw new IllegalStateException("Unexpected current element: " + reader.getLocalName()); 
		}
		List<TQStateUpdate<ISecIDT>> result = new ArrayList<>();
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "quotation":
					result.add(readQuotation(reader));
					break;
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "quotations":
					return result;
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	private TQStateUpdate<ISecIDT> readTrade(XMLStreamReader reader) throws XMLStreamException {
		String sec_code = null, board_code = null;
		DeltaUpdateBuilder builder = new DeltaUpdateBuilder()
				.withToken(FTrade.SECID, getAttributeInt(reader, "secid"));
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "tradeno":
					builder.withToken(FTrade.TRADENO, readCharacters(reader));
					break;
				case "board":
					builder.withToken(FTrade.BOARD, board_code = readCharacters(reader));
					break;
				case "time":
					builder.withToken(FTrade.TIME, readDate(reader));
					break;
				case "price":
					builder.withToken(FTrade.PRICE, readDecimal(reader));
					break;
				case "quantity":
					builder.withToken(FTrade.QUANTITY, readDecimal(reader));
					break;
				case "buysell":
					builder.withToken(FTrade.BUYSELL, readCharacters(reader));
					break;
				case "openinterest":
					builder.withToken(FTrade.OPENINTEREST, readDecimal(reader));
					break;
				case "seccode":
					builder.withToken(FTrade.SECCODE, sec_code = readCharacters(reader));
					break;
				case "period":
					builder.withToken(FTrade.PERIOD, readCharacters(reader));
					break;
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "trade":
					checkNotNull(sec_code, "seccode");
					checkNotNull(board_code, "board");
					return new TQStateUpdate<>(new TQSecIDT(sec_code, board_code), builder.buildUpdate());
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	public List<TQStateUpdate<ISecIDT>> readAlltrades(XMLStreamReader reader) throws XMLStreamException {
		if ( ! "alltrades".equals(reader.getLocalName()) ) {
			throw new IllegalStateException("Unexpected current element: " + reader.getLocalName());
		}
		List<TQStateUpdate<ISecIDT>> result = new ArrayList<>();
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "trade":
					result.add(readTrade(reader));
					break;
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "alltrades":
					return result;
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	private Quote readQuote(XMLStreamReader reader) throws XMLStreamException {
		String sec_code = null, board_code = null, source = null;
		CDecimal price = null;
		Long yield = null, buy = null, sell = null;
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "board":
					board_code = readCharacters(reader);
					break;
				case "seccode":
					sec_code = readCharacters(reader);
					break;
				case "price":
					price = readDecimal(reader);
					break;
				case "source":
					source = readCharacters(reader);
					break;
				case "yield":
					yield = readLong(reader);
					break;
				case "buy":
					buy = readLong(reader);
					break;
				case "sell":
					sell = readLong(reader);
					break;
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "quote":
					checkNotNull(sec_code, "seccode");
					checkNotNull(board_code, "board");
					checkNotNull(price, "price");
					return new Quote(new TQSecIDT(sec_code, board_code), price.withUnit(source), yield, buy, sell);
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	public List<Quote> readQuotes(XMLStreamReader reader) throws XMLStreamException {
		if ( ! "quotes".equals(reader.getLocalName()) ) {
			throw new IllegalStateException("Unexpected current element: " + reader.getLocalName());
		}
		List<Quote> result = new ArrayList<>();
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "quote":
					result.add(readQuote(reader));
					break;
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "quotes":
					return result;
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	public ServerStatus readServerStatus(XMLStreamReader reader) throws XMLStreamException {
		if ( ! "server_status".equals(reader.getLocalName()) ) {
			throw new IllegalStateException("Unexpected current element: " + reader.getLocalName());
		}
		String str_connected = getAttribute(reader, "connected");
		String str_recover = getAttribute(reader, "recover");
		boolean connected = false, recover = false;
		String error_msg = null;
		if ( str_connected != null ) {
			switch ( str_connected ) {
			case "true":
				connected = true;
				break;
			case "false":
				connected = false;
				break;
			case "error":
				connected = false;
				error_msg = readCharacters(reader);
				break;
			default:
				throw new XMLStreamException("Unexpected connection status: " + str_connected);
			}
		}
		if ( str_recover != null ) {
			switch ( str_recover ) {
			case "true":
				recover = true;
				break;
			case "false":
				recover = false;
				break;
			default:
				throw new XMLStreamException("Unexpected recover status: " + str_recover);
			}
		}
		skipElement(reader);
		return new ServerStatus(connected, recover, error_msg);
	}
	
	public TQStateUpdate<String> readClient(XMLStreamReader reader) throws XMLStreamException {
		if ( ! "client".equals(reader.getLocalName()) ) {
			throw new IllegalStateException("Unexpected current element: " + reader.getLocalName());
		}
		String client_id = null;
		DeltaUpdateBuilder builder = new DeltaUpdateBuilder()
				.withToken(FClient.ID, client_id = getAttribute(reader, "id"))
				.withToken(FClient.REMOVE, getAttributeBool(reader, "remove"));
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "type":
					builder.withToken(FClient.TYPE, readCharacters(reader));
					break;
				case "currency":
					builder.withToken(FClient.CURRENCY, readCharacters(reader));
					break;
				case "market":
					builder.withToken(FClient.MARKET_ID, readInt(reader));
					break;
				case "union":
					builder.withToken(FClient.UNION_CODE, readCharacters(reader));
					break;
				case "forts_acc":
					builder.withToken(FClient.FORTS_ACCOUNT, readCharacters(reader));
					break;
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "client":
					checkNotNull(client_id, "id");
					return new TQStateUpdate<>(client_id, builder.buildUpdate());
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	
	
}

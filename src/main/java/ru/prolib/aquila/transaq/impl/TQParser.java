package ru.prolib.aquila.transaq.impl;

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
import ru.prolib.aquila.transaq.impl.TQField.FBoard;
import ru.prolib.aquila.transaq.impl.TQField.FCKind;
import ru.prolib.aquila.transaq.impl.TQField.FMarket;
import ru.prolib.aquila.transaq.impl.TQField.FSecurity;
import ru.prolib.aquila.transaq.impl.TQField.FSecurityBoard;

public class TQParser {
	private static final Logger logger;
	private static final TQParser instance;
	
	static {
		logger = LoggerFactory.getLogger(TQParser.class);
		instance = new TQParser();
	}
	
	public static TQParser getInstance() {
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
	
	public LocalDateTime readDate(XMLStreamReader reader) throws XMLStreamException {
		String str_date = readCharacters(reader);
		int len = str_date.length();
		if ( len == 8 ) {
			return LocalDateTime.of(
					LocalDate.now(),
					LocalTime.parse(str_date, DateTimeFormatter.ofPattern("HH:mm:ss"))
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
	
	private TQStateUpdate<TQSecID_F> readSecurity(XMLStreamReader reader) throws XMLStreamException {
		Integer market_id = null;
		String sec_code = null, short_name = null;
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
					builder.withToken(FSecurity.DEFAULT_BOARDCODE, readCharacters(reader));
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
					return new TQStateUpdate<>(
							new TQSecID_F(sec_code, market_id, short_name, sec_type),
							builder.buildUpdate()
						);
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	public List<TQStateUpdate<TQSecID_F>> readSecurities(XMLStreamReader reader) throws XMLStreamException {
		if ( ! "securities".equals(reader.getLocalName()) ) {
			throw new IllegalStateException("Unexpected current element: " + reader.getLocalName());
		}
		List<TQStateUpdate<TQSecID_F>> result = new ArrayList<>();
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
	
	public TQStateUpdate<TQSecID1> readSecInfo(XMLStreamReader reader) throws XMLStreamException {
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
					return new TQStateUpdate<>(new TQSecID1(sec_code,market_id), builder.buildUpdate());
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	public TQStateUpdate<TQSecID1> readSecInfoUpd(XMLStreamReader reader) throws XMLStreamException {
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
					return new TQStateUpdate<>(new TQSecID1(sec_code, market_id), builder.buildUpdate());
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	private TQStateUpdate<TQSecID2> readPit(XMLStreamReader reader) throws XMLStreamException {
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
					return new TQStateUpdate<>(new TQSecID2(sec_code, board_code), builder.buildUpdate());
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}

	public List<TQStateUpdate<TQSecID2>> readPits(XMLStreamReader reader) throws XMLStreamException {
		if ( ! "pits".equals(reader.getLocalName()) ) {
			throw new IllegalStateException("Unexpected current element: " + reader.getLocalName());
		}
		List<TQStateUpdate<TQSecID2>> result = new ArrayList<>();
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
	
}

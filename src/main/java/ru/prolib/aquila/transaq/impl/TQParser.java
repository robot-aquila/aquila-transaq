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
import ru.prolib.aquila.transaq.entity.Board;
import ru.prolib.aquila.transaq.entity.CandleKind;
import ru.prolib.aquila.transaq.entity.Market;
import ru.prolib.aquila.transaq.entity.SecType;

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
	
	private Market readMarket(XMLStreamReader reader) throws XMLStreamException {
		String str_id = reader.getAttributeValue(null, "id");
		String str_name = readCharacters(reader).trim();
		if ( str_name.length() == 0 ) {
			str_name = str_id + " [N/A]";
		}
		try {
			return new Market(Integer.parseInt(str_id), str_name);
		} catch ( NumberFormatException e ) {
			throw new XMLStreamException("Cannot parse ID: " + str_id, e);
		}
	}
	
	public List<Market> readMarkets(XMLStreamReader reader) throws XMLStreamException {
		if ( ! "markets".equals(reader.getLocalName()) ) {
			throw new IllegalStateException("Unexpected current element: " + reader.getLocalName());
		}
		List<Market> result = new ArrayList<>();
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
	
	private Board readBoard(XMLStreamReader reader) throws XMLStreamException {
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
					return new Board(str_code, str_name, market, type);					
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	public List<Board> readBoards(XMLStreamReader reader) throws XMLStreamException {
		if ( ! "boards".equals(reader.getLocalName()) ) {
			throw new IllegalStateException("Unexpected current element: " + reader.getLocalName());
		}
		List<Board> result = new ArrayList<>();
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
	
	private CandleKind readCandleKind(XMLStreamReader reader) throws XMLStreamException {
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
					return new CandleKind(id, period, name);
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	public List<CandleKind> readCandleKinds(XMLStreamReader reader) throws XMLStreamException {
		if ( ! "candlekinds".equals(reader.getLocalName()) ) {
			throw new IllegalStateException("Unexpected current element: " + reader.getLocalName());
		}
		List<CandleKind> result = new ArrayList<>();
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
	
	private TQSecurityUpdate3 readSecurity(XMLStreamReader reader) throws XMLStreamException {
		Integer market_id = null;
		String sec_code = null, short_name = null;
		SecType sec_type = null;
		DeltaUpdateBuilder builder = new DeltaUpdateBuilder()
				.withToken(TQSecField.SECID, getAttributeInt(reader, "secid"))
				.withToken(TQSecField.ACTIVE, getAttributeBool(reader, "active"));
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "seccode":
					builder.withToken(TQSecField.SECCODE, sec_code = readCharacters(reader));
					break;
				case "instrclass":
					builder.withToken(TQSecField.SECCLASS, readCharacters(reader));
					break;
				case "board":
					builder.withToken(TQSecField.DEFAULT_BOARDCODE, readCharacters(reader));
					break;
				case "market":
					builder.withToken(TQSecField.MARKETID, market_id = readInt(reader));
					break;
				case "shortname":
					builder.withToken(TQSecField.SHORT_NAME, short_name = readCharacters(reader));
					break;
				case "decimals":
					builder.withToken(TQSecField.DECIMALS, readInt(reader));
					break;
				case "minstep":
					builder.withToken(TQSecField.MINSTEP, readDecimal(reader));
					break;
				case "lotsize":
					builder.withToken(TQSecField.LOTSIZE, readDecimal(reader));
					break;
				case "point_cost":
					builder.withToken(TQSecField.POINT_COST, readDecimal(reader));
					break;
				case "opmask":
					int opmask = 0;
					opmask |= getAttributeBool(reader, "usecredit", "yes") ? OPMASK_USECREDIT : 0;
					opmask |= getAttributeBool(reader, "bymarket", "yes") ? OPMASK_BYMARKET : 0;
					opmask |= getAttributeBool(reader, "nosplit", "yes") ? OPMASK_NOSPLIT : 0;
					opmask |= getAttributeBool(reader, "fok", "yes") ? OPMASK_FOK : 0;
					opmask |= getAttributeBool(reader, "ioc", "yes") ? OPMASK_IOC : 0;
					reader.nextTag();
					builder.withToken(TQSecField.OPMASK, opmask);
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
					builder.withToken(TQSecField.SECTYPE, sec_type);
					break;
				case "sec_tz":
					builder.withToken(TQSecField.SECTZ, readCharacters(reader));
					break;
				case "quotestype":
					builder.withToken(TQSecField.QUOTESTYPE, readInt(reader));
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
					return new TQSecurityUpdate3(
							new TQSecID_F(sec_code, market_id, short_name, sec_type),
							builder.buildUpdate()
						);
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	public List<TQSecurityUpdate3> readSecurities(XMLStreamReader reader) throws XMLStreamException {
		if ( ! "securities".equals(reader.getLocalName()) ) {
			throw new IllegalStateException("Unexpected current element: " + reader.getLocalName());
		}
		List<TQSecurityUpdate3> result = new ArrayList<>();
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
	
	public TQSecurityUpdate1 readSecInfo(XMLStreamReader reader) throws XMLStreamException {
		if ( ! "sec_info".equals(reader.getLocalName()) ) {
			throw new IllegalStateException("Unexpected current element: " + reader.getLocalName());
		}
		Integer market_id = null;
		String sec_code = null;
		DeltaUpdateBuilder builder = new DeltaUpdateBuilder()
				.withToken(TQSecField.SECID, getAttributeInt(reader, "secid"));
		while ( reader.hasNext() ) {
			switch ( reader.next() ) {
			case XMLStreamReader.START_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "secname":
					builder.withToken(TQSecField.SECNAME, readCharacters(reader));
					break;
				case "seccode":
					builder.withToken(TQSecField.SECCODE, sec_code = readCharacters(reader));
					break;
				case "market":
					builder.withToken(TQSecField.MARKETID,  market_id = readInt(reader));
					break;
				case "pname":
					builder.withToken(TQSecField.PNAME, readCharacters(reader));
					break;
				case "mat_date":
					builder.withToken(TQSecField.MAT_DATE, readDate(reader));
					break;
				case "clearing_price":
					builder.withToken(TQSecField.CLEARING_PRICE, readDecimal(reader));
					break;
				case "minprice":
					builder.withToken(TQSecField.MINPRICE, readDecimal(reader));
					break;
				case "maxprice":
					builder.withToken(TQSecField.MAXPRICE, readDecimal(reader));
					break;
				case "buy_deposit":
					builder.withToken(TQSecField.BUY_DEPOSIT, readDecimal(reader));
					break;
				case "sell_deposit":
					builder.withToken(TQSecField.SELL_DEPOSIT, readDecimal(reader));
					break;
				case "bgo_c":
					builder.withToken(TQSecField.BGO_C, readDecimal(reader));
					break;
				case "bgo_nc":
					builder.withToken(TQSecField.BGO_NC, readDecimal(reader));
					break;
				case "accruedint":
					builder.withToken(TQSecField.ACCRUED_INT, readDecimal(reader));
					break;
				case "coupon_value":
					builder.withToken(TQSecField.COUPON_VALUE, readDecimal(reader));
					break;
				case "coupon_date":
					builder.withToken(TQSecField.COUPON_DATE, readDate(reader));
					break;
				case "coupon_period":
					builder.withToken(TQSecField.COUPON_PERIOD, readInt(reader));
					break;
				case "facevalue":
					builder.withToken(TQSecField.FACE_VALUE, readDecimal(reader));
					break;
				case "put_call":
					builder.withToken(TQSecField.PUT_CALL, readCharacters(reader));
					break;
				case "opt_type":
					builder.withToken(TQSecField.OPT_TYPE, readCharacters(reader));
					break;
				case "lot_volume":
					builder.withToken(TQSecField.LOT_VOLUME, readInt(reader));
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
					return new TQSecurityUpdate1(
							new TQSecID1(sec_code,market_id),
							builder.buildUpdate()
						);
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	public TQSecurityUpdate1 readSecInfoUpd(XMLStreamReader reader) throws XMLStreamException {
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
					builder.withToken(TQSecField.SECID, readInt(reader));
					break;
				case "market":
					builder.withToken(TQSecField.MARKETID, market_id = readInt(reader));
					break;
				case "seccode":
					builder.withToken(TQSecField.SECCODE, sec_code = readCharacters(reader));
					break;
				case "minprice":
					builder.withToken(TQSecField.MINPRICE, readDecimal(reader));
					break;
				case "maxprice":
					builder.withToken(TQSecField.MAXPRICE, readDecimal(reader));
					break;
				case "buy_deposit":
					builder.withToken(TQSecField.BUY_DEPOSIT, readDecimal(reader));
					break;
				case "sell_deposit":
					builder.withToken(TQSecField.SELL_DEPOSIT, readDecimal(reader));
					break;
				case "bgo_c":
					builder.withToken(TQSecField.BGO_C, readDecimal(reader));
					break;
				case "bgo_nc":
					builder.withToken(TQSecField.BGO_NC, readDecimal(reader));
					break;
				case "bgo_buy":
					builder.withToken(TQSecField.BGO_BUY, readDecimal(reader));
					break;
				case "point_cost":
					builder.withToken(TQSecField.POINT_COST, readDecimal(reader));
					break;
				}
				break;
			case XMLStreamReader.END_ELEMENT:
				switch ( reader.getLocalName() ) {
				case "sec_info_upd":
					checkNotNull(sec_code, "seccode");
					checkNotNull(market_id, "market");
					return new TQSecurityUpdate1(
							new TQSecID1(sec_code, market_id),
							builder.buildUpdate()
						);
				}
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}

}

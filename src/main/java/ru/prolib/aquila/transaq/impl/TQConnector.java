package ru.prolib.aquila.transaq.impl;

import java.util.Set;

import org.ini4j.Profile.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.JTransaq.JTransaqHandler;
import ru.prolib.JTransaq.JTransaqServer;

public class TQConnector {
	public static final int SUBSCR_TYPE_QUOTATIONS = 0x01;
	public static final int SUBSCR_TYPE_ALL_TRADES = 0x02;
	public static final int SUBSCR_TYPE_QUOTES = 0x04;
	
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(TQConnector.class);
	}
	
	private final Section config;
	private final JTransaqServer server;
	private final JTransaqHandler handler;
	
	public TQConnector(Section config, JTransaqServer server, JTransaqHandler handler) {
		this.config = config;
		this.server = server;
		this.handler = handler;
	}
	
	private String cfg_var(String key) throws TQConnectorException {
		String x = config.get(key);
		if ( x == null ) {
			throw new TQConnectorException("Parameter was not defined: " + key);
		}
		return x;
	}
	
	public void init() throws TQConnectorException {
		try {
			server.Initialize(cfg_var("log_path"), Integer.parseInt(cfg_var("log_level")));
		} catch ( TQConnectorException e ) {
			throw e;
		} catch ( Exception e ) {
			throw new TQConnectorException("Initialization failed", e);
		}
	}
	
	public void connect() throws TQConnectorException {
		try {
			server.SendCommand("<command id=\"connect\">"
					+ "<login>" + cfg_var("login") + "</login>"
					+ "<password>" + cfg_var("password") + "</password>"
					+ "<host>" + cfg_var("host") + "</host>"
					+ "<port>" + cfg_var("port") + "</port>"
					+ "<language>en</language>"
					+ "<autopos>true</autopos>"
					+ "<micex_registers>true</micex_registers>"
					+ "<milliseconds>true</milliseconds>"
					+ "<utc_time>false</utc_time>"
					+ "<rqdelay>1000</rqdelay>"
					+ "<push_u_limits>5</push_u_limits>"
					+ "<push_pos_equity>5</push_pos_equity>"
					+ "</command>");
		} catch ( TQConnectorException e ) {
			throw e;
		} catch ( Exception e ) {
			throw new TQConnectorException("Failed to connect: ", e);
		}
	}
	
	public void disconnect() {
		try {
			server.SendCommand("<command id=\"disconnect\"/>");
		} catch ( Exception e ) {
			logger.error("Disconnect error: ", e);
		}
	}
	
	public void close() {
		try {
			server.UnInitialize();
		} catch ( Exception e ) {
			logger.error("Error shutting down: ", e);
		}
		handler.Handle("<dump_stats/>");
		handler.delete();
	}
	
	private void manageSubscriptions(Set<TQSecID2> symbols, int subscr_type, String command) throws TQConnectorException {
		String str_symbols = "";
		for ( TQSecID2 symbol : symbols ) {
			str_symbols +=
				"\t\t<security>\n" +
				"\t\t\t<board>" + symbol.getBoardCode() + "</board>\n" +
				"\t\t\t<seccode>" + symbol.getSecCode() + "</seccode>\n" +
				"\t\t</security>\n";
		}
		String x = "<command id=\"" + command + "\">\n";
		if ( (SUBSCR_TYPE_ALL_TRADES & subscr_type) != 0 ) {
			x += "\t<alltrades>\n" + str_symbols + "\t</alltrades>\n";
		}
		if ( (SUBSCR_TYPE_QUOTATIONS & subscr_type) != 0 ) {
			x += "\t<quotations>\n" + str_symbols + "\t</quotations>\n";
		}
		if ( (SUBSCR_TYPE_QUOTES & subscr_type) != 0 ) {
			x += "\t<quotes>\n" + str_symbols + "\t</quotes>\n";
		}
		x += "</command>";
		try {
			server.SendCommand(x);
		} catch ( TQConnectorException e ) {
			throw e;
		} catch ( Exception e ) {
			throw new TQConnectorException("Failed to connect: ", e);
		}
	}
	
	/**
	 * Subscribe for symbol data feed.
	 * <p>
	 * @param symbols - list of symbols to subscribe
	 * @param subscr_type - {@link #SUBSCR_TYPE_QUOTATIONS}, {@link #SUBSCR_TYPE_ALL_TRADES},
	 * {@link #SUBSCR_TYPE_QUOTES}. Can be combined using bitwise OR.
	 * @throws TQConnectorException - an error occurred
	 */
	public void subscribe(Set<TQSecID2> symbols, int subscr_type) throws TQConnectorException {
		manageSubscriptions(symbols, subscr_type, "subscribe");
	}
	
	/**
	 * Unsubscribe symbol data feed.
	 * <p>
	 * @param symbols - list of symbols to unsubscribe
	 * @param subscr_type - subscription data type. See {@link #subscribe(Set, int)} for details. 
	 * @throws TQConnectorException - an error occurred
	 */
	public void unsubscribe(Set<TQSecID2> symbols, int subscr_type) throws TQConnectorException {
		manageSubscriptions(symbols, subscr_type, "unsubscribe");
	}
	
}

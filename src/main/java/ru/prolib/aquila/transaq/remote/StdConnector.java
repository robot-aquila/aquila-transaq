package ru.prolib.aquila.transaq.remote;

import java.util.Set;

import org.ini4j.Profile.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.JTransaq.JTransaqHandler;
import ru.prolib.JTransaq.JTransaqServer;
import ru.prolib.aquila.transaq.engine.mp.DefaultMessageProcessor;
import ru.prolib.aquila.transaq.impl.TransaqException;

public class StdConnector implements Connector {
	public static final int SUBSCR_TYPE_QUOTATIONS = 0x01;
	public static final int SUBSCR_TYPE_ALL_TRADES = 0x02;
	public static final int SUBSCR_TYPE_QUOTES = 0x04;
	
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(StdConnector.class);
	}
	
	private final Section config;
	private final JTransaqServer server;
	private final JTransaqHandler handler;
	
	public StdConnector(Section config, JTransaqServer server, JTransaqHandler handler) {
		this.config = config;
		this.server = server;
		this.handler = handler;
	}
	
	private String cfg_var(String key) throws TransaqException {
		String x = config.get(key);
		if ( x == null ) {
			throw new TransaqException("Parameter was not defined: " + key);
		}
		return x;
	}
	
	private void SendCommand(String data) throws Exception {
		if ( logger.isDebugEnabled() ) {
			logger.debug("OUT> {}", data);
		}
		server.SendCommand(data);
	}
	
	@Override
	public void init() throws TransaqException {
		try {
			server.Initialize(cfg_var("log_path"), Integer.parseInt(cfg_var("log_level")));
		} catch ( TransaqException e ) {
			throw e;
		} catch ( Exception e ) {
			throw new TransaqException("Initialization failed", e);
		}
	}
	
	@Override
	public void connect() throws TransaqException {
		try {
			SendCommand("<command id=\"connect\">"
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
		} catch ( TransaqException e ) {
			throw e;
		} catch ( Exception e ) {
			throw new TransaqException("Failed to connect: ", e);
		}
	}
	
	@Override
	public void disconnect() {
		try {
			SendCommand("<command id=\"disconnect\"/>");
		} catch ( Exception e ) {
			logger.error("Disconnect error: ", e);
		}
	}
	
	@Override
	public void close() {
		try {
			server.UnInitialize();
		} catch ( Exception e ) {
			logger.error("Error shutting down: ", e);
		}
		handler.Handle(DefaultMessageProcessor.DUMP_PROC_TAG);
		handler.delete();
	}
	
	private void manageSubscriptions(Set<TQSecIDT> symbols, int subscr_type, String command) throws TransaqException {
		String str_symbols = "";
		for ( TQSecIDT symbol : symbols ) {
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
			SendCommand(x);
		} catch ( TransaqException e ) {
			throw e;
		} catch ( Exception e ) {
			throw new TransaqException("Failed to connect: ", e);
		}
	}
	
	/**
	 * Subscribe for symbol data feed.
	 * <p>
	 * @param symbols - list of symbols to subscribe
	 * @param subscr_type - {@link #SUBSCR_TYPE_QUOTATIONS}, {@link #SUBSCR_TYPE_ALL_TRADES},
	 * {@link #SUBSCR_TYPE_QUOTES}. Can be combined using bitwise OR.
	 * @throws TransaqException - an error occurred
	 */
	@Deprecated
	public void subscribe(Set<TQSecIDT> symbols, int subscr_type) throws TransaqException {
		manageSubscriptions(symbols, subscr_type, "subscribe");
	}
	
	private String _to_security_list(Set<ISecIDT> ids) {
		StringBuilder sb = new StringBuilder();
		for ( ISecIDT id : ids ) {
			sb.append("\t\t<security>\n")
				.append("\t\t\t<board>").append(id.getBoardCode()).append("</board>\n")
				.append("\t\t\t<seccode>").append(id.getSecCode()).append("</seccode>\n")
				.append("\t\t</security>\n");
		}
		return sb.toString();
	}
	
	private void _manage_subscriptions(
			Set<ISecIDT> alltrades,
			Set<ISecIDT> quotations,
			Set<ISecIDT> quotes,
			String command)
					throws TransaqException
	{
		if ( alltrades.size() + quotations.size() + quotes.size() <= 0 ) {
			return;
		}
		StringBuilder sb = new StringBuilder().append("<command id=\"").append(command).append("\">\n");
		if ( alltrades.size() > 0 ) {
			sb.append("\t<alltrades>\n").append(_to_security_list(alltrades)).append("\t</alltrades>\n");
		}
		if ( quotations.size() > 0 ) {
			sb.append("\t<quotations>\n").append(_to_security_list(quotations)).append("\t</quotations>\n");
		}
		if ( quotes.size() > 0 ) {
			sb.append("\t<quotes>\n").append(_to_security_list(quotes)).append("\t</quotes>\n");
		}
		sb.append("</command>");
		try {
			SendCommand(sb.toString());
		} catch ( TransaqException e ) {
			throw e;
		} catch ( Exception e ) {
			throw new TransaqException("Error sending command: ", e);
		}
	}
	
	@Override
	public void subscribe(Set<ISecIDT> trades, Set<ISecIDT> quotations, Set<ISecIDT> quotes) throws TransaqException {
		_manage_subscriptions(trades, quotations, quotes, "subscribe");
	}
	
	/**
	 * Unsubscribe symbol data feed.
	 * <p>
	 * @param symbols - list of symbols to unsubscribe
	 * @param subscr_type - subscription data type. See {@link #subscribe(Set, int)} for details. 
	 * @throws TransaqException - an error occurred
	 */
	@Deprecated
	public void unsubscribe(Set<TQSecIDT> symbols, int subscr_type) throws TransaqException {
		manageSubscriptions(symbols, subscr_type, "unsubscribe");
	}
	
	@Override
	public void unsubscribe(Set<ISecIDT> trades, Set<ISecIDT> quotations, Set<ISecIDT> quotes) throws TransaqException {
		_manage_subscriptions(trades, quotations, quotes, "unsubscribe");
	}
	
}

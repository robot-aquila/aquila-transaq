package ru.prolib.aquila.transaq.impl;

import org.ini4j.Profile.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.JTransaq.JTransaqServer;

public class TQConnector {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(TQConnector.class);
	}
	
	private final Section config;
	private final JTransaqServer server;
	
	public TQConnector(Section config, JTransaqServer server) {
		this.config = config;
		this.server = server;
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
	}
	
}

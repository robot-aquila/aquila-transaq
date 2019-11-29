package ru.prolib.aquila.transaq.impl;

import org.ini4j.Profile.Section;

import ru.prolib.JTransaq.JTransaqHandler;
import ru.prolib.JTransaq.JTransaqServer;
import ru.prolib.aquila.transaq.engine.Connector;
import ru.prolib.aquila.transaq.engine.ConnectorFactory;

public class StdConnectorFactory implements ConnectorFactory {
	private final Section config;
	
	public StdConnectorFactory(Section config) {
		this.config = config;
	}

	@Override
	public Connector produce(JTransaqHandler handler) throws TransaqException {
		Connector connector = null;
		try {
			connector = new StdConnector(config, new JTransaqServer(handler), handler);
			// The reason call init here - because this method usually called at app
			// init and if it fails then whole app fail. If calling init moved to
			// dedicated thread then bad config will cause exception which can't be
			// handled as usual. And we get broken app which are got stuck in
			// non-operable state.
			connector.init();
			return connector;
		} catch ( TransaqException e ) {
			throw e;
		} catch ( Exception e ) {
			throw new TransaqException(e);
		}
	}

}

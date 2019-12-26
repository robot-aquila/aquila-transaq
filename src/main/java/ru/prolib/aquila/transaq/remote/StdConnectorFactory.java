package ru.prolib.aquila.transaq.remote;

import org.ini4j.Profile.Section;

import ru.prolib.JTransaq.JTransaqHandler;
import ru.prolib.JTransaq.JTransaqServer;
import ru.prolib.aquila.transaq.impl.TransaqException;

public class StdConnectorFactory implements ConnectorFactory {
	private final Section config;
	private final MessageInterceptor interceptor;
	
	public StdConnectorFactory(Section config, MessageInterceptor interceptor) {
		this.config = config;
		this.interceptor = interceptor;
	}
	
	public StdConnectorFactory(Section config) {
		this(config, new MessageInterceptorStub());
	}
	
	protected JTransaqServer createServer(JTransaqHandler handler) throws Exception {
		return new JTransaqServer(handler);
	}

	@Override
	public Connector produce(JTransaqHandler handler) throws TransaqException {
		Connector connector = null;
		try {
			connector = new StdConnector(config, createServer(handler), handler, interceptor);
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

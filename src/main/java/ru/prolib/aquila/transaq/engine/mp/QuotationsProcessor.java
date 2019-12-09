package ru.prolib.aquila.transaq.engine.mp;

import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.impl.TQStateUpdate;
import ru.prolib.aquila.transaq.remote.ISecIDT;

public class QuotationsProcessor implements MessageProcessor {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(QuotationsProcessor.class);
	}
	
	private final ServiceLocator services;
	
	public QuotationsProcessor(ServiceLocator services) {
		this.services = services;
	}

	@Override
	public void processRawMessage(String message) throws Exception {
		if ( logger.isDebugEnabled() ) {
			logger.debug("RAW: {}", message);
		}
	}

	@Override
	public void processMessage(XMLStreamReader reader) throws Exception {
		TQReactor reactor = services.getReactor();
		for ( TQStateUpdate<ISecIDT> uptabe : services.getParser().readQuotations(reader) ) {
			reactor.updateSecurityQuotations(uptabe);
		}
	}

}

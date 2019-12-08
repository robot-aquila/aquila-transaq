package ru.prolib.aquila.transaq.engine.mp;

import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.transaq.engine.ServiceLocator;

public class ServerStatusProcessor implements MessageProcessor {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(ServerStatusProcessor.class);
	}
	
	private final ServiceLocator services;
	
	public ServerStatusProcessor(ServiceLocator services) {
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
		services.getReactor().updateServerStatus(services.getParser().readServerStatus(reader));
	}

}

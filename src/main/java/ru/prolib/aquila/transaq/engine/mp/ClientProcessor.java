package ru.prolib.aquila.transaq.engine.mp;

import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientProcessor implements MessageProcessor {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(ClientProcessor.class);
	}

	@Override
	public void processRawMessage(String message) throws Exception {
		logger.debug("Client: {}", message);
	}

	@Override
	public void processMessage(XMLStreamReader reader) throws Exception {
		
	}

}

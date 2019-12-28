package ru.prolib.aquila.transaq.engine.mp;

import javax.xml.stream.XMLStreamReader;

import ru.prolib.aquila.transaq.engine.ServiceLocator;

public class QuotesProcessor implements MessageProcessor {
	private final ServiceLocator services;
	
	public QuotesProcessor(ServiceLocator services) {
		this.services = services;
	}

	@Override
	public void processRawMessage(String message) throws Exception {
		
	}

	@Override
	public void processMessage(XMLStreamReader reader) throws Exception {
		services.getReactor().registerQuotes(services.getParser().readQuotes(reader));
	}

}

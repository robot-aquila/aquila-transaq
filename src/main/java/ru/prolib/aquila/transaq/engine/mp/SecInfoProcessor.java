package ru.prolib.aquila.transaq.engine.mp;

import javax.xml.stream.XMLStreamReader;

import ru.prolib.aquila.transaq.engine.ServiceLocator;

public class SecInfoProcessor implements MessageProcessor {
	private final ServiceLocator services;

	public SecInfoProcessor(ServiceLocator services) {
		this.services = services;
	}
	
	@Override
	public void processMessage(XMLStreamReader reader) throws Exception {
		services.getReactor().updateSecurity1(services.getParser().readSecInfo(reader));
	}

	@Override
	public void processRawMessage(String message) throws Exception {
		
	}

}

package ru.prolib.aquila.transaq.engine.mp;

import javax.xml.stream.XMLStreamReader;

import ru.prolib.aquila.transaq.engine.ServiceLocator;

public class ServerStatusProcessor implements MessageProcessor {
	private final ServiceLocator services;
	
	public ServerStatusProcessor(ServiceLocator services) {
		this.services = services;
	}

	@Override
	public void processRawMessage(String message) throws Exception {

	}

	@Override
	public void processMessage(XMLStreamReader reader) throws Exception {
		services.getReactor().updateServerStatus(services.getParser().readServerStatus(reader));
	}

}

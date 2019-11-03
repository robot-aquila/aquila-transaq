package ru.prolib.aquila.transaq.engine.mp;

import javax.xml.stream.XMLStreamReader;

import ru.prolib.aquila.transaq.engine.ServiceLocator;

public class SecInfoUpdProcessor  implements MessageProcessor {
	private final ServiceLocator services;

	public SecInfoUpdProcessor(ServiceLocator services) {
		this.services = services;
	}

	@Override
	public void processMessage(XMLStreamReader reader) throws Exception {
		services.getReactor().updateSecurity1(services.getParser().readSecInfoUpd(reader));
	}

	@Override
	public void processRawMessage(String message) throws Exception {
		
	}

}

package ru.prolib.aquila.transaq.engine.mp;

import javax.xml.stream.XMLStreamReader;

import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.impl.TQStateUpdate;

public class MarketsProcessor implements MessageProcessor {
	private final ServiceLocator services;
	
	public MarketsProcessor(ServiceLocator services) {
		this.services = services;
	}

	@Override
	public void processMessage(XMLStreamReader reader) throws Exception {
		TQReactor reactor = services.getReactor();
		for ( TQStateUpdate<Integer> update : services.getParser().readMarkets(reader) ) {
			reactor.updateMarket(update);
		}
	}

	@Override
	public void processRawMessage(String message) throws Exception {
		
	}

}

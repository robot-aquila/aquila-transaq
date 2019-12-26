package ru.prolib.aquila.transaq.engine.mp;

import javax.xml.stream.XMLStreamReader;

import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.impl.TQStateUpdate;
import ru.prolib.aquila.transaq.remote.ISecIDT;

public class AlltradesProcessor implements MessageProcessor {
	private final ServiceLocator services;
	
	public AlltradesProcessor(ServiceLocator services) {
		this.services = services;
	}

	@Override
	public void processRawMessage(String message) throws Exception {
		
	}

	@Override
	public void processMessage(XMLStreamReader reader) throws Exception {
		TQReactor reactor = services.getReactor();
		for ( TQStateUpdate<ISecIDT> update : services.getParser().readAlltrades(reader) ) {
			reactor.registerTrade(update);
		}
	}

}

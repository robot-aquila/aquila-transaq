package ru.prolib.aquila.transaq.engine.mp;

import javax.xml.stream.XMLStreamReader;

import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.impl.TQStateUpdate;
import ru.prolib.aquila.transaq.remote.TQSecIDT;

public class PitsProcessor implements MessageProcessor {
	private final ServiceLocator services;
	
	public PitsProcessor(ServiceLocator services) {
		this.services = services;
	}
	
	@Override
	public void processMessage(XMLStreamReader reader) throws Exception {
		TQReactor reactor = services.getReactor();
		for ( TQStateUpdate<TQSecIDT> update : services.getParser().readPits(reader) ) {
			reactor.updateSecurityBoard(update);
		}
	}

	@Override
	public void processRawMessage(String message) throws Exception {
		
	}

}

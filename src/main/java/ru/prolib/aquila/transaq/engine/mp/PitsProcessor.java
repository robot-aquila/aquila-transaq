package ru.prolib.aquila.transaq.engine.mp;

import javax.xml.stream.XMLStreamReader;

import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.impl.TQSecID2;
import ru.prolib.aquila.transaq.impl.TQStateUpdate;

public class PitsProcessor implements MessageProcessor {
	private final ServiceLocator services;
	
	public PitsProcessor(ServiceLocator services) {
		this.services = services;
	}
	
	@Override
	public void processMessage(XMLStreamReader reader) throws Exception {
		TQReactor reactor = services.getReactor();
		for ( TQStateUpdate<TQSecID2> update : services.getParser().readPits(reader) ) {
			reactor.updateSecurityBoard(update);
		}
	}

	@Override
	public void processRawMessage(String message) throws Exception {
		
	}

}

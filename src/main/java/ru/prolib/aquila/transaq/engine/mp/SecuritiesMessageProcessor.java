package ru.prolib.aquila.transaq.engine.mp;

import javax.xml.stream.XMLStreamReader;

import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.impl.TQStateUpdate;
import ru.prolib.aquila.transaq.remote.ISecIDF;

public class SecuritiesMessageProcessor implements MessageProcessor {
	private final ServiceLocator services;
	
	public SecuritiesMessageProcessor(ServiceLocator services) {
		this.services = services;
	}

	@Override
	public void processMessage(XMLStreamReader reader) throws Exception {
		TQReactor reactor = services.getReactor();
		for ( TQStateUpdate<ISecIDF> update : services.getParser().readSecurities(reader) ) {
			reactor.updateSecurityF(update);
		}
	}

	@Override
	public void processRawMessage(String message) throws Exception {
		
	}

}

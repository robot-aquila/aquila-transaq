package ru.prolib.aquila.transaq.engine.mp;

import javax.xml.stream.XMLStreamReader;

import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.impl.TQStateUpdate;

public class BoardsProcessor implements MessageProcessor {
	private final ServiceLocator services;
	
	public BoardsProcessor(ServiceLocator services) {
		this.services = services;
	}

	@Override
	public void processMessage(XMLStreamReader reader) throws Exception {
		TQReactor reactor = services.getReactor();
		for ( TQStateUpdate<String> update : services.getParser().readBoards(reader) ) {
			reactor.updateBoard(update);
		}
	}

	@Override
	public void processRawMessage(String message) throws Exception {
		
	}

}

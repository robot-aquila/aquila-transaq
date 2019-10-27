package ru.prolib.aquila.transaq.impl.mp;

import javax.xml.stream.XMLStreamReader;

import ru.prolib.aquila.transaq.impl.TQMessageProcessor;
import ru.prolib.aquila.transaq.impl.TQParser;
import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.impl.TQSecID2;
import ru.prolib.aquila.transaq.impl.TQStateUpdate;

public class PitsProcessor implements TQMessageProcessor {
	private final TQParser parser;
	private final TQReactor reactor;
	
	public PitsProcessor(TQReactor reactor, TQParser parser) {
		this.reactor = reactor;
		this.parser = parser;
	}
	
	public PitsProcessor(TQReactor reactor) {
		this(reactor, TQParser.getInstance());
	}
	
	@Override
	public void processMessage(XMLStreamReader reader) throws Exception {
		for ( TQStateUpdate<TQSecID2> update : parser.readPits(reader) ) {
			reactor.updateSecurityBoard(update);
		}
	}

	@Override
	public void processRawMessage(String message) throws Exception {
		
	}

}

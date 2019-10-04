package ru.prolib.aquila.transaq.impl.mp;

import javax.xml.stream.XMLStreamReader;

import ru.prolib.aquila.transaq.impl.TQMessageProcessor;
import ru.prolib.aquila.transaq.impl.TQParser;
import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.impl.TQStateUpdate;

public class CandleKindsProcessor implements TQMessageProcessor {
	private final TQReactor reactor;
	private final TQParser parser;
	
	public CandleKindsProcessor(TQReactor reactor, TQParser parser) {
		this.reactor = reactor;
		this.parser = parser;
	}
	
	public CandleKindsProcessor(TQReactor reactor) {
		this(reactor, TQParser.getInstance());
	}
	
	@Override
	public void processMessage(XMLStreamReader reader) throws Exception {
		for ( TQStateUpdate<Integer> update : parser.readCandleKinds(reader) ) {
			reactor.updateCandleKind(update);
		}
	}

	@Override
	public void processRawMessage(String message) throws Exception {
		
	}

}

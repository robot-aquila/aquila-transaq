package ru.prolib.aquila.transaq.impl.mp;

import javax.xml.stream.XMLStreamReader;

import ru.prolib.aquila.transaq.impl.TQMessageProcessor;
import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.impl.TQSecID_F;
import ru.prolib.aquila.transaq.impl.TQParser;
import ru.prolib.aquila.transaq.impl.TQStateUpdate;

public class SecuritiesMessageProcessor implements TQMessageProcessor {
	private final TQParser parser;
	private final TQReactor reactor;
	
	public SecuritiesMessageProcessor(TQReactor reactor, TQParser parser) {
		this.reactor = reactor;
		this.parser = parser;
	}
	
	public SecuritiesMessageProcessor(TQReactor reactor) {
		this(reactor, TQParser.getInstance());
	}

	@Override
	public void processMessage(XMLStreamReader reader) throws Exception {
		for ( TQStateUpdate<TQSecID_F> update : parser.readSecurities(reader) ) {
			reactor.updateSecurityF(update);
		}
	}

	@Override
	public void processRawMessage(String message) throws Exception {
		
	}

}

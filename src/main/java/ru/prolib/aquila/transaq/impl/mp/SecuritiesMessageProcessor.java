package ru.prolib.aquila.transaq.impl.mp;

import javax.xml.stream.XMLStreamReader;

import ru.prolib.aquila.transaq.impl.TQMessageProcessor;
import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.impl.TQParser;
import ru.prolib.aquila.transaq.impl.TQSecurityUpdate3;

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
		for ( TQSecurityUpdate3 update : parser.readSecurities(reader) ) {
			reactor.updateSecurity(update);
		}
	}

}

package ru.prolib.aquila.transaq.impl.mp;

import javax.xml.stream.XMLStreamReader;

import ru.prolib.aquila.transaq.impl.TQMessageProcessor;
import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.impl.TQParser;

public class SecInfoUpdProcessor  implements TQMessageProcessor {
	private final TQParser parser;
	private final TQReactor reactor;

	public SecInfoUpdProcessor(TQReactor reactor, TQParser parser) {
		this.parser = parser;
		this.reactor = reactor;
	}

	@Override
	public void processMessage(XMLStreamReader reader) throws Exception {
		reactor.updateSecurity(parser.readSecInfoUpd(reader));
	}

}

package ru.prolib.aquila.transaq.impl.mp;

import javax.xml.stream.XMLStreamReader;

import ru.prolib.aquila.transaq.impl.IMessageProcessor;
import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.impl.Parser;

public class SecInfoProcessor implements IMessageProcessor {
	private final Parser parser;
	private final TQReactor receiver;

	public SecInfoProcessor(TQReactor receiver, Parser parser) {
		this.parser = parser;
		this.receiver = receiver;
	}
	
	@Override
	public void processMessage(XMLStreamReader reader) throws Exception {
		receiver.updateSecurity(parser.readSecInfo(reader));
	}

}

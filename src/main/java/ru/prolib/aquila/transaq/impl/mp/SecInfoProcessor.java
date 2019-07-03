package ru.prolib.aquila.transaq.impl.mp;

import javax.xml.stream.XMLStreamReader;

import ru.prolib.aquila.transaq.impl.IMessageProcessor;
import ru.prolib.aquila.transaq.impl.IUpdateReceiver;
import ru.prolib.aquila.transaq.impl.Parser;

public class SecInfoProcessor implements IMessageProcessor {
	private final Parser parser;
	private final IUpdateReceiver receiver;

	public SecInfoProcessor(IUpdateReceiver receiver, Parser parser) {
		this.parser = parser;
		this.receiver = receiver;
	}
	
	@Override
	public void processMessage(XMLStreamReader reader) throws Exception {
		receiver.updateSecurity(parser.readSecInfo(reader));
	}

}

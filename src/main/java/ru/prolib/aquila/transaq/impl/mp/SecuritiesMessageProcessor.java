package ru.prolib.aquila.transaq.impl.mp;

import javax.xml.stream.XMLStreamReader;

import ru.prolib.aquila.transaq.entity.SecurityUpdate1;
import ru.prolib.aquila.transaq.impl.IMessageProcessor;
import ru.prolib.aquila.transaq.impl.IUpdateReceiver;
import ru.prolib.aquila.transaq.impl.Parser;

public class SecuritiesMessageProcessor implements IMessageProcessor {
	private final Parser parser;
	private final IUpdateReceiver receiver;
	
	public SecuritiesMessageProcessor(IUpdateReceiver receiver, Parser parser) {
		this.receiver = receiver;
		this.parser = parser;
	}
	
	public SecuritiesMessageProcessor(IUpdateReceiver receiver) {
		this(receiver, Parser.getInstance());
	}

	@Override
	public void processMessage(XMLStreamReader reader) throws Exception {
		for ( SecurityUpdate1 update : parser.readSecurities(reader) ) {
			receiver.updateSecurity(update);
		}
	}

}

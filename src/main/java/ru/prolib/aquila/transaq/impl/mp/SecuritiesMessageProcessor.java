package ru.prolib.aquila.transaq.impl.mp;

import javax.xml.stream.XMLStreamReader;

import ru.prolib.aquila.transaq.impl.IMessageProcessor;
import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.impl.Parser;
import ru.prolib.aquila.transaq.impl.TQSecurityUpdate3;

public class SecuritiesMessageProcessor implements IMessageProcessor {
	private final Parser parser;
	private final TQReactor receiver;
	
	public SecuritiesMessageProcessor(TQReactor receiver, Parser parser) {
		this.receiver = receiver;
		this.parser = parser;
	}
	
	public SecuritiesMessageProcessor(TQReactor receiver) {
		this(receiver, Parser.getInstance());
	}

	@Override
	public void processMessage(XMLStreamReader reader) throws Exception {
		for ( TQSecurityUpdate3 update : parser.readSecurities(reader) ) {
			receiver.updateSecurity(update);
		}
	}

}

package ru.prolib.aquila.transaq.impl.mp;

import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.transaq.impl.TQMessageProcessor;

public class RawMessageDumper implements TQMessageProcessor {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(RawMessageDumper.class);
	}

	@Override
	public void processRawMessage(String message) throws Exception {
		logger.debug("RAW: {}", message);
	}

	@Override
	public void processMessage(XMLStreamReader reader) throws Exception {
		
	}

}

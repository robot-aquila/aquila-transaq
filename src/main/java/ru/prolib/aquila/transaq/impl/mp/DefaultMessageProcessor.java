package ru.prolib.aquila.transaq.impl.mp;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.transaq.impl.IMessageProcessor;
import ru.prolib.aquila.transaq.xml.Parser;

public class DefaultMessageProcessor implements IMessageProcessor {
	private static final String CLOSE_PROC_ID = "close";
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(DefaultMessageProcessor.class);
	}
	
	private final Map<String, Integer> unprocMessagesCount;
	private final Parser parser;
	
	public DefaultMessageProcessor(Map<String, Integer> unproc_count, Parser parser) {
		this.unprocMessagesCount = unproc_count;
		this.parser = parser;
	}
	
	public DefaultMessageProcessor() {
		this(new LinkedHashMap<>(), Parser.getInstance());
	}

	@Override
	public void processMessage(XMLStreamReader reader) throws Exception {
		String proc_id = reader.getLocalName();
		parser.skipElement(reader);
		if ( CLOSE_PROC_ID.equals(proc_id) ) {
			close();
		} else {
			Integer x = unprocMessagesCount.get(proc_id);
			if ( x == null ) {
				x = 1;
			} else {
				x ++;
			}
			unprocMessagesCount.put(proc_id, x);
		}
	}

	private void close() {
		logger.debug("Dumping map of unprocessed messages: ");
		Iterator<Map.Entry<String, Integer>> it = unprocMessagesCount.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<String, Integer> entry = it.next();
			logger.debug("{} -> {}", entry.getKey(), entry.getValue());
		}
		unprocMessagesCount.clear();
		logger.debug("Map of unprocessed messages has been cleared");
	}

}

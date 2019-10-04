package ru.prolib.aquila.transaq.impl.mp;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.transaq.impl.TQMessageProcessor;
import ru.prolib.aquila.transaq.impl.TQParser;

public class DefaultMessageProcessor implements TQMessageProcessor {
	public static final String DUMP_PROC_ID = "dump_stats";
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(DefaultMessageProcessor.class);
	}
	
	private final Map<String, Integer> unprocMessagesCount;
	private final TQParser parser;
	
	public DefaultMessageProcessor(Map<String, Integer> unproc_count, TQParser parser) {
		this.unprocMessagesCount = unproc_count;
		this.parser = parser;
	}
	
	public DefaultMessageProcessor(TQParser parser) {
		this(new LinkedHashMap<>(), parser);
	}
	
	public DefaultMessageProcessor() {
		this(new LinkedHashMap<>(), TQParser.getInstance());
	}

	@Override
	public void processMessage(XMLStreamReader reader) throws Exception {
		String proc_id = reader.getLocalName();
		parser.skipElement(reader);
		if ( DUMP_PROC_ID.equals(proc_id) ) {
			dump_stats();
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

	private void dump_stats() {
		logger.debug("Dumping map of unprocessed messages: ");
		Iterator<Map.Entry<String, Integer>> it = unprocMessagesCount.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<String, Integer> entry = it.next();
			logger.debug("{} -> {}", entry.getKey(), entry.getValue());
		}
		unprocMessagesCount.clear();
		logger.debug("Map of unprocessed messages has been cleared");
	}

	@Override
	public void processRawMessage(String message) throws Exception {
		
	}

}

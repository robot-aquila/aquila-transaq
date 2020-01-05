package ru.prolib.aquila.transaq.engine.mp;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.transaq.engine.ServiceLocator;

public class DefaultMessageProcessor implements MessageProcessor {
	public static final String DUMP_PROC_ID = "dump_stats";
	public static final String DUMP_PROC_TAG = "<dump_stats/>";
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(DefaultMessageProcessor.class);
	}
	
	private final Map<String, Integer> unprocMessagesCount;
	private final ServiceLocator services;
	
	public DefaultMessageProcessor(Map<String, Integer> unproc_count, ServiceLocator services) {
		this.unprocMessagesCount = unproc_count;
		this.services = services;
	}
	
	public DefaultMessageProcessor(ServiceLocator services) {
		this(new LinkedHashMap<>(), services);
	}

	@Override
	public void processMessage(XMLStreamReader reader) throws Exception {
		String proc_id = reader.getLocalName();
		services.getParser().skipElement(reader);
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
		if ( unprocMessagesCount.size() == 0 ) {
			logger.debug("No unprocessed messages registered");
			return;
		}
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

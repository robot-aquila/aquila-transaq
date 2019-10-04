package ru.prolib.aquila.transaq.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TQMessageRouter {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(TQMessageRouter.class);
	}
	
	private final XMLInputFactory factory;
	private final TQMessageProcessorRegistry registry;
	
	public TQMessageRouter(XMLInputFactory factory, TQMessageProcessorRegistry registry) {
		this.factory = factory;
		this.registry = registry;
	}
	
	public TQMessageRouter(TQMessageProcessorRegistry registry) {
		this(XMLInputFactory.newFactory(), registry);
	}

	public void dispatchMessage(String message) {
		try {
			try ( InputStream is = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8)) ) {
				XMLStreamReader reader = factory.createXMLStreamReader(is);
				try {
					String processor_id = null;
					while ( reader.hasNext() ) {
						if ( reader.next() == XMLStreamReader.START_ELEMENT ) {
							processor_id = reader.getLocalName();
							break;
						}
					}
					if ( processor_id == null ) {
						throw new XMLStreamException("Malformed message");
					}
					TQMessageProcessor processor = registry.get(processor_id);
					processor.processRawMessage(message);
					processor.processMessage(reader);
				} finally {
					reader.close();
				}
			}
		} catch ( Exception e ) {
			logger.error("Error processing message: {}", message, e);
		}
	}

}

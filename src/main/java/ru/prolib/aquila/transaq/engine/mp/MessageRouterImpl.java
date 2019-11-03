package ru.prolib.aquila.transaq.engine.mp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageRouterImpl implements MessageRouter {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(MessageRouterImpl.class);
	}
	
	private final XMLInputFactory factory;
	private final ProcessorRegistry registry;
	
	public MessageRouterImpl(XMLInputFactory factory, ProcessorRegistry registry) {
		this.factory = factory;
		this.registry = registry;
	}
	
	public MessageRouterImpl(ProcessorRegistry registry) {
		this(XMLInputFactory.newFactory(), registry);
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.transaq.impl.TQMessageRouter#dispatchMessage(java.lang.String)
	 */
	@Override
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
					MessageProcessor processor = registry.get(processor_id);
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

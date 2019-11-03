package ru.prolib.aquila.transaq.engine.mp;

import javax.xml.stream.XMLStreamReader;

public interface MessageProcessor {
	void processRawMessage(String message) throws Exception;
	void processMessage(XMLStreamReader reader) throws Exception;
}

package ru.prolib.aquila.transaq.impl;

import javax.xml.stream.XMLStreamReader;

public interface TQMessageProcessor {
	void processRawMessage(String message) throws Exception;
	void processMessage(XMLStreamReader reader) throws Exception;
}

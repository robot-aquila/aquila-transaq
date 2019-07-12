package ru.prolib.aquila.transaq.impl;

import javax.xml.stream.XMLStreamReader;

public interface TQMessageProcessor {
	void processMessage(XMLStreamReader reader) throws Exception;
}

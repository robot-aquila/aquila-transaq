package ru.prolib.aquila.transaq.impl;

import org.ini4j.Profile.Section;

import ru.prolib.JTransaq.JTransaqServer;
import ru.prolib.aquila.transaq.engine.Engine;

public class TQConnectorFactory {

	public TQConnector createInstance(Section config, Engine engine) throws Exception {
		TransaqHandler handler = new TransaqHandler(engine);
		TQConnector connector = new TQConnector(config, new JTransaqServer(handler), handler);
		connector.init();
		return connector;
	}

}

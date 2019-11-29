package ru.prolib.aquila.transaq.engine;

import ru.prolib.JTransaq.JTransaqHandler;
import ru.prolib.aquila.transaq.impl.TransaqException;

public interface ConnectorFactory {
	Connector produce(JTransaqHandler handler) throws TransaqException;
}

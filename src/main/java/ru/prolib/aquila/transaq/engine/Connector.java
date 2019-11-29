package ru.prolib.aquila.transaq.engine;

import java.util.Set;

import ru.prolib.aquila.transaq.impl.TransaqException;
import ru.prolib.aquila.transaq.impl.TQSecID2;

public interface Connector {
	void init() throws TransaqException;
	void connect() throws TransaqException;
	void disconnect();
	void close();
	void subscribe(Set<TQSecID2> quotations, Set<TQSecID2> trades, Set<TQSecID2> quotes);
	void unsubscribe(Set<TQSecID2> quotations, Set<TQSecID2> trades, Set<TQSecID2> quotes);
}

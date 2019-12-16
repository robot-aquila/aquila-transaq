package ru.prolib.aquila.transaq.remote;

import java.util.Set;

import ru.prolib.aquila.transaq.impl.TransaqException;

public interface Connector {
	void init() throws TransaqException;
	void connect() throws TransaqException;
	void disconnect();
	void close();
	void subscribe(Set<ISecIDT> trades, Set<ISecIDT> quotations, Set<ISecIDT> quotes) throws TransaqException;
	void unsubscribe(Set<ISecIDT> trades, Set<ISecIDT> quotations, Set<ISecIDT> quotes) throws TransaqException;
	void getSecurities() throws TransaqException;
}

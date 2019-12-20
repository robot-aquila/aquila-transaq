package ru.prolib.aquila.transaq.remote;

public interface MessageInterceptor {
	void incoming(String message);
	void outgoing(String message);
	void close();
}

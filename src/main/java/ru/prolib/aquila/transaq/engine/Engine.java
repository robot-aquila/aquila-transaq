package ru.prolib.aquila.transaq.engine;

public interface Engine {
	void shutdown();
	void messageFromServer(String message);
}

package ru.prolib.aquila.transaq.engine;

import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public interface Engine {
	void connect();
	void disconnect();
	void shutdown();
	void messageFromServer(String message);
	void subscribeSymbol(Symbol symbol, MDLevel level);
	void unsubscribeSymbol(Symbol symbol, MDLevel level);
}

package ru.prolib.aquila.transaq.engine;

import java.util.concurrent.CompletableFuture;

import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public interface Engine {
	void connect();
	void disconnect();
	CompletableFuture<Boolean> shutdown();
	void messageFromServer(String message);
	CompletableFuture<Boolean> subscribeSymbol(Symbol symbol, MDLevel level);
	void unsubscribeSymbol(Symbol symbol, MDLevel level);
}

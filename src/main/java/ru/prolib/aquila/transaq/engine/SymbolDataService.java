package ru.prolib.aquila.transaq.engine;

import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public interface SymbolDataService {
	void onSubscribe(Symbol symbol, MDLevel level);
	void onUnsubscribe(Symbol symbol, MDLevel level);
	void onConnectionStatusChange(boolean connected);
}

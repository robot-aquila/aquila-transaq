package ru.prolib.aquila.transaq.engine;

import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public interface SecurityDataService {
	void subscribe(Symbol symbol, MDLevel level);
	void unsubscribe(Symbol symbol, MDLevel level);
}

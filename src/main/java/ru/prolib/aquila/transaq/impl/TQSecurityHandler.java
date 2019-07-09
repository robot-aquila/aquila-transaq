package ru.prolib.aquila.transaq.impl;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public interface TQSecurityHandler {
	TQSecID_F getSecID3();
	Symbol getSymbol();
	void update(DeltaUpdate update);
}

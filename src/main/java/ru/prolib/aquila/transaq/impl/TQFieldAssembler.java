package ru.prolib.aquila.transaq.impl;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public interface TQFieldAssembler {
	Symbol determineSymbol(DeltaUpdate initial_update);
}

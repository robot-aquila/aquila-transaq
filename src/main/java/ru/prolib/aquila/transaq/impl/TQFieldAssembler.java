package ru.prolib.aquila.transaq.impl;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

public interface TQFieldAssembler {
	Symbol toSymbol(TQSecID_F id);
}

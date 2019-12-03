package ru.prolib.aquila.transaq.engine.sds;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.transaq.remote.ISecIDF;
import ru.prolib.aquila.transaq.remote.ISecIDT;

public interface IIDConverter {
	SymbolTID toSymbolTID(Symbol symbol);
	SymbolTID toSymbolTID(ISecIDF id);
	SymbolTID toSymbolTID(ISecIDT id);
}

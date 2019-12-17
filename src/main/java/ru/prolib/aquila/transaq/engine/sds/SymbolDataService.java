package ru.prolib.aquila.transaq.engine.sds;

import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.transaq.impl.TQStateUpdate;
import ru.prolib.aquila.transaq.remote.ISecIDF;
import ru.prolib.aquila.transaq.remote.ISecIDG;
import ru.prolib.aquila.transaq.remote.ISecIDT;

public interface SymbolDataService {
	void onSubscribe(Symbol symbol, MDLevel level);
	void onUnsubscribe(Symbol symbol, MDLevel level);
	void onConnectionStatusChange(boolean connected);
	void onSecurityUpdateF(TQStateUpdate<ISecIDF> update);
	void onSecurityUpdateG(TQStateUpdate<ISecIDG> update);
	void onSecurityBoardUpdate(TQStateUpdate<ISecIDT> update);
	void onSecurityQuotationUpdate(TQStateUpdate<ISecIDT> update);
	void onSecurityTrades(List<TQStateUpdate<ISecIDT>> update_list);
	void onSecurityQuotes(List<TQStateUpdate<ISecIDT>> update_list);
}
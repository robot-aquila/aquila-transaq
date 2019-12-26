package ru.prolib.aquila.transaq.impl;

import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.engine.sds.SymbolDataService;
import ru.prolib.aquila.transaq.remote.ISecIDF;
import ru.prolib.aquila.transaq.remote.ISecIDG;
import ru.prolib.aquila.transaq.remote.ISecIDT;
import ru.prolib.aquila.transaq.remote.entity.ServerStatus;

public class TQReactor {
	private final ServiceLocator services;
	private boolean connected = false;
	
	public TQReactor(ServiceLocator services) {
		this.services = services;
	}
	
	private TQDirectory getDir() {
		return services.getDirectory();
	}
	
	private SymbolDataService getSDS() {
		return services.getSymbolDataService();
	}

	public void updateMarket(TQStateUpdate<Integer> update) {
		getDir().updateMarket(update);
	}

	public void updateBoard(TQStateUpdate<String> update) {
		getDir().updateBoard(update);
	}

	public void updateCandleKind(TQStateUpdate<Integer> update) {
		getDir().updateCKind(update);
	}

	public void updateSecurity1(TQStateUpdate<ISecIDG> update) {
		getSDS().onSecurityUpdateG(update);
	}

	public void updateSecurityF(TQStateUpdate<ISecIDF> update) {
		getSDS().onSecurityUpdateF(update);
	}
	
	public void updateSecurityBoard(TQStateUpdate<ISecIDT> update) {
		getSDS().onSecurityBoardUpdate(update);
	}
	
	public void updateServerStatus(ServerStatus status) {
		if ( status.isConnected() != connected ) {
			getDir().updateConnectionStatus(status.isConnected());
			getSDS().onConnectionStatusChange(connected = status.isConnected());
		}
	}
	
	public void updateSecurityQuotations(TQStateUpdate<ISecIDT> update) {
		getSDS().onSecurityQuotationUpdate(update);
	}
	
	public void registerTrade(TQStateUpdate<ISecIDT> update) {
		getSDS().onSecurityTrade(update);
	}

}

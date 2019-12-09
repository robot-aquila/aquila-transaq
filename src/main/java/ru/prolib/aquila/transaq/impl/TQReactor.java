package ru.prolib.aquila.transaq.impl;

import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.engine.SymbolDataService;
import ru.prolib.aquila.transaq.remote.ISecIDF;
import ru.prolib.aquila.transaq.remote.ISecIDG;
import ru.prolib.aquila.transaq.remote.ISecIDT;
import ru.prolib.aquila.transaq.remote.entity.ServerStatus;

public class TQReactor {
	private final ServiceLocator services;
	private final TQSecurityHandlerRegistry shr;
	private final TQSecurityHandlerFactory shf;
	private boolean connected = false;
	
	public TQReactor(ServiceLocator services,
					 TQSecurityHandlerRegistry sh_registry,
					 TQSecurityHandlerFactory sh_factory)
	{
		this.services = services;
		this.shr = sh_registry;
		this.shf = sh_factory;
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
		getDir().updateSecurityParamsP(update);
		shr.getHandler(update.getID()).update(update.getUpdate());
	}

	public void updateSecurityF(TQStateUpdate<ISecIDF> update) {
		ISecIDF sec_id = update.getID();
		getDir().updateSecurityParamsF(update);
		TQSecurityHandler x = shr.getHandlerOrNull(sec_id);
		if ( x == null ) {
			x = shf.createHandler(sec_id);
			x.update(update.getUpdate());
			shr.registerHandler(x);
		} else {
			x.update(update.getUpdate());
		}
	}
	
	public void updateSecurityBoard(TQStateUpdate<ISecIDT> update) {
		getDir().updateSecurityBoardParams(update);
	}
	
	public void updateServerStatus(ServerStatus status) {
		if ( status.isConnected() != connected ) {
			getSDS().onConnectionStatusChange(connected = status.isConnected());
		}
	}
	
	public void updateSecurityQuotations(TQStateUpdate<ISecIDT> update) {
		getDir().updateSecurityQuotations(update);
	}

}

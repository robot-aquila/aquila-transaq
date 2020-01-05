package ru.prolib.aquila.transaq.impl;

import java.util.List;

import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.engine.sds.SymbolDataService;
import ru.prolib.aquila.transaq.remote.ID;
import ru.prolib.aquila.transaq.remote.ID.FC;
import ru.prolib.aquila.transaq.remote.ID.FM;
import ru.prolib.aquila.transaq.remote.ID.FP;
import ru.prolib.aquila.transaq.remote.ID.MP;
import ru.prolib.aquila.transaq.remote.ID.SL;
import ru.prolib.aquila.transaq.remote.ID.SP;
import ru.prolib.aquila.transaq.remote.ID.UL;
import ru.prolib.aquila.transaq.remote.ISecIDF;
import ru.prolib.aquila.transaq.remote.ISecIDG;
import ru.prolib.aquila.transaq.remote.ISecIDT;
import ru.prolib.aquila.transaq.remote.entity.Quote;
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
	
	public void registerQuotes(List<Quote> quotes) {
		getSDS().onSecurityQuotes(quotes);
	}
	
	public void updateClient(TQStateUpdate<String> update) {
		getDir().updateClient(update);
	}
	
	@SuppressWarnings("unchecked")
	public void updatePositions(List<TQStateUpdate<? extends ID>> updates) {
		TQDirectory dir = getDir();
		for ( TQStateUpdate<? extends ID> update : updates ) {
			switch ( update.getID().getType() ) {
			case MONEY_POSITION:
				dir.updateMoneyPosition((TQStateUpdate<MP>) update);
				break;
			case SEC_POSITION:
				dir.updateSecPosition((TQStateUpdate<SP>) update);
				break;
			case FORTS_MONEY:
				dir.updateFortsMoney((TQStateUpdate<FM>) update);
				break;
			case FORTS_POSITION:
				dir.updateFortsPosition((TQStateUpdate<FP>) update);
				break;
			case FORTS_COLLATERALS:
				dir.updateFortsCollaterals((TQStateUpdate<FC>) update);
				break;
			case SPOT_LIMIT:
				dir.updateSpotLimits((TQStateUpdate<SL>) update);
				break;
			case UNITED_LIMITS:
				dir.updateUnitedLimits((TQStateUpdate<UL>) update);
				break;
			}
		}
	}

}

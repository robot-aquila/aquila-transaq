package ru.prolib.aquila.transaq.remote.entity;

import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainerImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FFortsCollaterals;

public class FortsCollaterals extends ObservableStateContainerImpl {

	public FortsCollaterals(OSCParams params) {
		super(params);
	}
	
	public String getClientID() {
		return getString(FFortsCollaterals.CLIENT_ID);
	}
	
	public String getUnionCode() {
		return getString(FFortsCollaterals.UNION_CODE);
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getMarkets() {
		return (List<Integer>) getObject(FFortsCollaterals.MARKETS);
	}
	
	public String getShortName() {
		return getString(FFortsCollaterals.SHORT_NAME);
	}
	
	public CDecimal getCurrent() {
		return getCDecimal(FFortsCollaterals.CURRENT);
	}
	
	public CDecimal getBlocked() {
		return getCDecimal(FFortsCollaterals.BLOCKED);
	}
	
	public CDecimal getFree() {
		return getCDecimal(FFortsCollaterals.FREE);
	}

}

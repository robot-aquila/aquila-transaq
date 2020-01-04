package ru.prolib.aquila.transaq.remote.entity;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainerImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FUnitedLimits;

public class UnitedLimits extends ObservableStateContainerImpl {

	public UnitedLimits(OSCParams params) {
		super(params);
	}
	
	public String getUnionCode() {
		return getString(FUnitedLimits.UNION_CODE);
	}
	
	public CDecimal getOpenEquity() {
		return getCDecimal(FUnitedLimits.OPEN_EQUITY);
	}
	
	public CDecimal getEquity() {
		return getCDecimal(FUnitedLimits.EQUITY);
	}
	
	public CDecimal getRequirements() {
		return getCDecimal(FUnitedLimits.REQUIREMENTS);
	}
	
	public CDecimal getFree() {
		return getCDecimal(FUnitedLimits.FREE);
	}
	
	public CDecimal getVarMargin() {
		return getCDecimal(FUnitedLimits.VAR_MARGIN);
	}
	
	public CDecimal getFinRes() {
		return getCDecimal(FUnitedLimits.FIN_RES);
	}
	
	public CDecimal getGo() {
		return getCDecimal(FUnitedLimits.GO);
	}

}

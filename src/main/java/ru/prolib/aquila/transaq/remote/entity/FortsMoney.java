package ru.prolib.aquila.transaq.remote.entity;

import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainerImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FFortsMoney;

public class FortsMoney extends ObservableStateContainerImpl {

	public FortsMoney(OSCParams params) {
		super(params);
	}
	
	public String getClientID() {
		return getString(FFortsMoney.CLIENT_ID);
	}
	
	public String getUnionCode() {
		return getString(FFortsMoney.UNION_CODE);
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getMarkets() {
		return (List<Integer>) getObject(FFortsMoney.MARKETS);
	}
	
	public String getShortName() {
		return getString(FFortsMoney.SHORT_NAME);
	}
	
	public CDecimal getCurrent() {
		return getCDecimal(FFortsMoney.CURRENT);
	}
	
	public CDecimal getBlocked() {
		return getCDecimal(FFortsMoney.BLOCKED);
	}
	
	public CDecimal getFree() {
		return getCDecimal(FFortsMoney.FREE);
	}
	
	public CDecimal getVarMargin() {
		return getCDecimal(FFortsMoney.VAR_MARGIN);
	}

}

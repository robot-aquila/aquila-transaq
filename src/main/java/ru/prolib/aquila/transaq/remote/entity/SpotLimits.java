package ru.prolib.aquila.transaq.remote.entity;

import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainerImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FSpotLimits;

public class SpotLimits extends ObservableStateContainerImpl {

	public SpotLimits(OSCParams params) {
		super(params);
	}
	
	public String getClientID() {
		return getString(FSpotLimits.CLIENT_ID);
	}
	
	public String getUnionCode() {
		return getString(FSpotLimits.UNION_CODE);
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getMarkets() {
		return (List<Integer>) getObject(FSpotLimits.MARKETS);
	}
	
	public String getShortName() {
		return getString(FSpotLimits.SHORT_NAME);
	}
	
	public CDecimal getBuyLimit() {
		return getCDecimal(FSpotLimits.BUY_LIMIT);
	}
	
	public CDecimal getBuyLimitUsed() {
		return getCDecimal(FSpotLimits.BUY_LIMIT_USED);
	}

}

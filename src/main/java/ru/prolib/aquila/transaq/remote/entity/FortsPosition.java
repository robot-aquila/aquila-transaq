package ru.prolib.aquila.transaq.remote.entity;

import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainerImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FFortsPosition;

public class FortsPosition extends ObservableStateContainerImpl {

	public FortsPosition(OSCParams params) {
		super(params);
	}
	
	public Integer getSecID() {
		return getInteger(FFortsPosition.SEC_ID);
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getMarkets() {
		return (List<Integer>) getObject(FFortsPosition.MARKETS);
	}
	
	public String getSecCode() {
		return getString(FFortsPosition.SEC_CODE);
	}
	
	public String getClientID() {
		return getString(FFortsPosition.CLIENT_ID);
	}
	
	public String getUnionCode() {
		return getString(FFortsPosition.UNION_CODE);
	}
	
	public CDecimal getStartNet() {
		return getCDecimal(FFortsPosition.START_NET);
	}
	
	public CDecimal getOpenBuys() {
		return getCDecimal(FFortsPosition.OPEN_BUYS);
	}
	
	public CDecimal getOpenSells() {
		return getCDecimal(FFortsPosition.OPEN_SELLS);
	}
	
	public CDecimal getTotalNet() {
		return getCDecimal(FFortsPosition.TOTAL_NET);
	}
	
	public CDecimal getTodayBuy() {
		return getCDecimal(FFortsPosition.TODAY_BUY);
	}
	
	public CDecimal getTodaySell() {
		return getCDecimal(FFortsPosition.TODAY_SELL);
	}
	
	public CDecimal getOptMargin() {
		return getCDecimal(FFortsPosition.OPT_MARGIN);
	}
	
	public CDecimal getVarMargin() {
		return getCDecimal(FFortsPosition.VAR_MARGIN);
	}
	
	public CDecimal getExpirationPos() {
		return getCDecimal(FFortsPosition.EXPIRATION_POS);
	}
	
	public CDecimal getUsedSellSpotLimit() {
		return getCDecimal(FFortsPosition.USED_SELL_SPOT_LIMIT);
	}
	
	public CDecimal getSellSpotLimit() {
		return getCDecimal(FFortsPosition.SELL_SPOT_LIMIT);
	}
	
	public CDecimal getNetto() {
		return getCDecimal(FFortsPosition.NETTO);
	}
	
	public CDecimal getKgo() {
		return getCDecimal(FFortsPosition.KGO);
	}

}

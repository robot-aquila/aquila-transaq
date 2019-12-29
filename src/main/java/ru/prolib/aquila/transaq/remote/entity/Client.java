package ru.prolib.aquila.transaq.remote.entity;

import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainerImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;
import ru.prolib.aquila.transaq.remote.MessageFields.FClient;

public class Client extends ObservableStateContainerImpl {

	public Client(OSCParams params) {
		super(params);
	}
	
	public String getID() {
		return getString(FClient.ID);
	}
	
	public Boolean getRemove() {
		return getBoolean(FClient.REMOVE);
	}
	
	public String getType() {
		return getString(FClient.TYPE);
	}
	
	public String getCurrencyCode() {
		return getString(FClient.CURRENCY);
	}
	
	public Integer getMarketID() {
		return getInteger(FClient.MARKET_ID);
	}
	
	public String getUnionCode() {
		return getString(FClient.UNION_CODE);
	}
	
	public String getFortsAccount() {
		return getString(FClient.FORTS_ACCOUNT);
	}

}

package ru.prolib.aquila.transaq.entity;

import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainerImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;
import ru.prolib.aquila.transaq.impl.TQField.FMarket;

public class Market extends ObservableStateContainerImpl {
	
	public Market(OSCParams params) {
		super(params);
	}
	
	public int getID() {
		return this.getInteger(FMarket.ID);
	}
	
	public String getName() {
		return this.getString(FMarket.NAME);
	}
	
}

package ru.prolib.aquila.transaq.entity;

import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainerImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;
import ru.prolib.aquila.transaq.impl.TQMarketField;

public class Market extends ObservableStateContainerImpl {
	
	public Market(OSCParams params) {
		super(params);
	}
	
	public int getID() {
		return this.getInteger(TQMarketField.ID);
	}
	
	public String getName() {
		return this.getString(TQMarketField.NAME);
	}
	
}

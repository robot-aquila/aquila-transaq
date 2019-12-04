package ru.prolib.aquila.transaq.entity;

import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainerImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;
import ru.prolib.aquila.transaq.remote.MessageFields.FBoard;

public class Board extends ObservableStateContainerImpl {
	
	public Board(OSCParams params) {
		super(params);
	}
	
	public String getCode() {
		return this.getString(FBoard.CODE);
	}
	
	public String getName() {
		return this.getString(FBoard.NAME);
	}
	
	public int getMarketID() {
		return this.getInteger(FBoard.MARKET_ID);
	}
	
	public int getTypeID() {
		return this.getInteger(FBoard.TYPE);
	}

}

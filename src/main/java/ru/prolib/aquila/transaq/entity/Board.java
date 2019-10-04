package ru.prolib.aquila.transaq.entity;

import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainerImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;
import ru.prolib.aquila.transaq.impl.TQBoardField;

public class Board extends ObservableStateContainerImpl {
	
	public Board(OSCParams params) {
		super(params);
	}
	
	public String getCode() {
		return this.getString(TQBoardField.CODE);
	}
	
	public String getName() {
		return this.getString(TQBoardField.NAME);
	}
	
	public int getMarketID() {
		return this.getInteger(TQBoardField.MARKET_ID);
	}
	
	public int getTypeID() {
		return this.getInteger(TQBoardField.TYPE);
	}

}

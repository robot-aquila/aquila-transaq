package ru.prolib.aquila.transaq.entity;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainerImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;
import ru.prolib.aquila.transaq.impl.TQField.FSecurityBoard;

public class SecurityBoardParams extends ObservableStateContainerImpl {
	
	public SecurityBoardParams(OSCParams params) {
		super(params);
	}
	
	public String getSecCode() {
		return this.getString(FSecurityBoard.SECCODE);
	}
	
	public String getBoardCode() {
		return this.getString(FSecurityBoard.BOARD);
	}
	
	public int getMarketID() {
		return this.getInteger(FSecurityBoard.MARKET);
	}
	
	public int getDecimals() {
		return this.getInteger(FSecurityBoard.DECIMALS);
	}
	
	public CDecimal getMinStep() {
		return this.getCDecimal(FSecurityBoard.MINSTEP);
	}
	
	public CDecimal getLotSize() {
		return this.getCDecimal(FSecurityBoard.LOTSIZE);
	}
	
	public CDecimal getPointCost() {
		return this.getCDecimal(FSecurityBoard.POINT_COST);
	}

}

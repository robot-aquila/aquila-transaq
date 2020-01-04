package ru.prolib.aquila.transaq.remote.entity;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainerImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FSecPosition;

public class SecPosition extends ObservableStateContainerImpl {

	public SecPosition(OSCParams params) {
		super(params);
	}
	
	public Integer getSecID() {
		return getInteger(FSecPosition.SEC_ID);
	}
	
	public Integer getMarketID() {
		return getInteger(FSecPosition.MARKET_ID);
	}
	
	public String getSecCode() {
		return getString(FSecPosition.SEC_CODE);
	}

	public String getRegister() {
		return getString(FSecPosition.REGISTER);
	}

	public String getClientID() {
		return getString(FSecPosition.CLIENT_ID);
	}

	public String getUnionCode() {
		return getString(FSecPosition.UNION_CODE);
	}

	public String getShortName() {
		return getString(FSecPosition.SHORT_NAME);
	}

	public CDecimal getSaldoIn() {
		return getCDecimal(FSecPosition.SALDO_IN);
	}
	
	public CDecimal getSaldoMin() {
		return getCDecimal(FSecPosition.SALDO_MIN);
	}
	
	public CDecimal getBought() {
		return getCDecimal(FSecPosition.BOUGHT);
	}
	
	public CDecimal getSold() {
		return getCDecimal(FSecPosition.SOLD);
	}
	
	public CDecimal getSaldo() {
		return getCDecimal(FSecPosition.SALDO);
	}

	public CDecimal getOrdBuy() {
		return getCDecimal(FSecPosition.ORD_BUY);
	}
	
	public CDecimal getOrdSell() {
		return getCDecimal(FSecPosition.ORD_SELL);
	}

	public CDecimal getAmount() {
		return getCDecimal(FSecPosition.AMOUNT);
	}
	
	public CDecimal getEquity() {
		return getCDecimal(FSecPosition.EQUITY);
	}

}

package ru.prolib.aquila.transaq.remote.entity;

import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainerImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FMoneyPosition;

public class MoneyPosition extends ObservableStateContainerImpl {

	public MoneyPosition(OSCParams params) {
		super(params);
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getMarkets() {
		return (List<Integer>) getObject(FMoneyPosition.MARKETS);
	}
	
	public String getRegister() {
		return getString(FMoneyPosition.REGISTER);
	}
	
	public String getAsset() {
		return getString(FMoneyPosition.ASSET);
	}
	
	public String getClientID() {
		return getString(FMoneyPosition.CLIENT_ID);
	}

	public String getUnionCode() {
		return getString(FMoneyPosition.UNION_CODE);
	}
	
	public String getShortName() {
		return getString(FMoneyPosition.SHORT_NAME);
	}

	public CDecimal getSaldoIn() {
		return getCDecimal(FMoneyPosition.SALDO_IN);
	}
	
	public CDecimal getBought() {
		return getCDecimal(FMoneyPosition.BOUGHT);
	}

	public CDecimal getSold() {
		return getCDecimal(FMoneyPosition.SOLD);
	}
	
	public CDecimal getSaldo() {
		return getCDecimal(FMoneyPosition.SALDO);
	}

	public CDecimal getOrdBuy() {
		return getCDecimal(FMoneyPosition.ORD_BUY);
	}

	public CDecimal getOrdBuyCond() {
		return getCDecimal(FMoneyPosition.ORB_BUY_COND);
	}

	public CDecimal getComission() {
		return getCDecimal(FMoneyPosition.COMISSION);
	}

}

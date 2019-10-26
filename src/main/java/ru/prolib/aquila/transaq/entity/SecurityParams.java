package ru.prolib.aquila.transaq.entity;

import java.time.LocalDateTime;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainerImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;
import ru.prolib.aquila.transaq.impl.TQSecField;

public class SecurityParams extends ObservableStateContainerImpl {

	public SecurityParams(OSCParams params) {
		super(params);
	}

	public String getSecCode() {
		return this.getString(TQSecField.SECCODE);
	}
	
	public int getMarketID() {
		return this.getInteger(TQSecField.MARKETID);
	}
	
	public boolean isActive() {
		return this.getBoolean(TQSecField.ACTIVE);
	}
	
	public String getSecClass() {
		return this.getString(TQSecField.SECCLASS);
	}
	
	public String getDefaultBoard() {
		return this.getString(TQSecField.DEFAULT_BOARDCODE);
	}
	
	public String getShortName() {
		return this.getString(TQSecField.SHORT_NAME);
	}
	
	public int getDecimals() {
		return this.getInteger(TQSecField.DECIMALS);
	}
	
	public CDecimal getMinStep() {
		return this.getCDecimal(TQSecField.MINSTEP);
	}
	
	public CDecimal getLotSize() {
		return this.getCDecimal(TQSecField.LOTSIZE);
	}
	
	public CDecimal getPointCost() {
		return this.getCDecimal(TQSecField.POINT_COST);
	}
	
	public int getOpMask() {
		return this.getInteger(TQSecField.OPMASK);
	}
	
	public SecType getSecType() {
		return (SecType) this.getObject(TQSecField.SECTYPE);
	}
	
	public String getSecTZ() {
		return this.getString(TQSecField.SECTZ);
	}
	
	public int getQuotesType() {
		return this.getInteger(TQSecField.QUOTESTYPE);
	}
	
	public String getSecName() {
		return this.getString(TQSecField.SECNAME);
	}
	
	public String getPName() {
		return this.getString(TQSecField.PNAME);
	}
	
	public LocalDateTime getMatDate() {
		return (LocalDateTime) this.getObject(TQSecField.MAT_DATE);
	}
	
	public CDecimal getClearingPrice() {
		return this.getCDecimal(TQSecField.CLEARING_PRICE);
	}
	
	public CDecimal getMinPrice() {
		return this.getCDecimal(TQSecField.MINPRICE);
	}
	
	public CDecimal getMaxPrice() {
		return this.getCDecimal(TQSecField.MAXPRICE);
	}
	
	public CDecimal getBuyDeposit() {
		return this.getCDecimal(TQSecField.BUY_DEPOSIT);
	}
	
	public CDecimal getSellDeposit() {
		return this.getCDecimal(TQSecField.SELL_DEPOSIT);
	}
	
	public CDecimal getBGO_C() {
		return this.getCDecimal(TQSecField.BGO_C);
	}
	
	public CDecimal getBGO_NC() {
		return this.getCDecimal(TQSecField.BGO_NC);
	}
	
	public CDecimal getAccruedInt() {
		return this.getCDecimal(TQSecField.ACCRUED_INT);
	}
	
	public CDecimal getCouponValue() {
		return this.getCDecimal(TQSecField.COUPON_VALUE);
	}
	
	public LocalDateTime getCouponDate() {
		return (LocalDateTime) this.getObject(TQSecField.COUPON_DATE);
	}
	
	public int getCouponPeriod() {
		return this.getInteger(TQSecField.COUPON_PERIOD);
	}
	
	public CDecimal getFaceValue() {
		return this.getCDecimal(TQSecField.FACE_VALUE);
	}
	
	public String getPutCall() {
		return this.getString(TQSecField.PUT_CALL);
	}
	
	public String getOptType() {
		return this.getString(TQSecField.OPT_TYPE);
	}
	
	public int getLotVolume() {
		return this.getInteger(TQSecField.LOT_VOLUME);
	}
	
	public CDecimal getBGO_BUY() {
		return this.getCDecimal(TQSecField.BGO_BUY);
	}
	
}

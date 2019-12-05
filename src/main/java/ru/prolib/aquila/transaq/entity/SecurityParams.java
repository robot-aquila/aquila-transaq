package ru.prolib.aquila.transaq.entity;

import java.time.LocalDateTime;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainerImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;
import ru.prolib.aquila.transaq.remote.MessageFields.FSecurity;

public class SecurityParams extends ObservableStateContainerImpl {

	public SecurityParams(OSCParams params) {
		super(params);
	}

	public String getSecCode() {
		return this.getString(FSecurity.SECCODE);
	}
	
	public int getMarketID() {
		return this.getInteger(FSecurity.MARKETID);
	}
	
	public Boolean isActive() {
		return this.getBoolean(FSecurity.ACTIVE);
	}
	
	public String getSecClass() {
		return this.getString(FSecurity.SECCLASS);
	}
	
	public String getDefaultBoard() {
		return this.getString(FSecurity.DEFAULT_BOARDCODE);
	}
	
	public String getShortName() {
		return this.getString(FSecurity.SHORT_NAME);
	}
	
	public Integer getDecimals() {
		return this.getInteger(FSecurity.DECIMALS);
	}
	
	public CDecimal getMinStep() {
		return this.getCDecimal(FSecurity.MINSTEP);
	}
	
	public CDecimal getLotSize() {
		return this.getCDecimal(FSecurity.LOTSIZE);
	}
	
	public CDecimal getPointCost() {
		return this.getCDecimal(FSecurity.POINT_COST);
	}
	
	public Integer getOpMask() {
		return this.getInteger(FSecurity.OPMASK);
	}
	
	public SecType getSecType() {
		return (SecType) this.getObject(FSecurity.SECTYPE);
	}
	
	public String getSecTZ() {
		return this.getString(FSecurity.SECTZ);
	}
	
	public Integer getQuotesType() {
		return this.getInteger(FSecurity.QUOTESTYPE);
	}
	
	public String getSecName() {
		return this.getString(FSecurity.SECNAME);
	}
	
	public String getPName() {
		return this.getString(FSecurity.PNAME);
	}
	
	public LocalDateTime getMatDate() {
		return (LocalDateTime) this.getObject(FSecurity.MAT_DATE);
	}
	
	public CDecimal getClearingPrice() {
		return this.getCDecimal(FSecurity.CLEARING_PRICE);
	}
	
	public CDecimal getMinPrice() {
		return this.getCDecimal(FSecurity.MINPRICE);
	}
	
	public CDecimal getMaxPrice() {
		return this.getCDecimal(FSecurity.MAXPRICE);
	}
	
	public CDecimal getBuyDeposit() {
		return this.getCDecimal(FSecurity.BUY_DEPOSIT);
	}
	
	public CDecimal getSellDeposit() {
		return this.getCDecimal(FSecurity.SELL_DEPOSIT);
	}
	
	public CDecimal getBGO_C() {
		return this.getCDecimal(FSecurity.BGO_C);
	}
	
	public CDecimal getBGO_NC() {
		return this.getCDecimal(FSecurity.BGO_NC);
	}
	
	public CDecimal getAccruedInt() {
		return this.getCDecimal(FSecurity.ACCRUED_INT);
	}
	
	public CDecimal getCouponValue() {
		return this.getCDecimal(FSecurity.COUPON_VALUE);
	}
	
	public LocalDateTime getCouponDate() {
		return (LocalDateTime) this.getObject(FSecurity.COUPON_DATE);
	}
	
	public Integer getCouponPeriod() {
		return this.getInteger(FSecurity.COUPON_PERIOD);
	}
	
	public CDecimal getFaceValue() {
		return this.getCDecimal(FSecurity.FACE_VALUE);
	}
	
	public String getPutCall() {
		return this.getString(FSecurity.PUT_CALL);
	}
	
	public String getOptType() {
		return this.getString(FSecurity.OPT_TYPE);
	}
	
	public Integer getLotVolume() {
		return this.getInteger(FSecurity.LOT_VOLUME);
	}
	
	public CDecimal getBGO_BUY() {
		return this.getCDecimal(FSecurity.BGO_BUY);
	}
	
}

package ru.prolib.aquila.transaq.entity;

import java.time.LocalDateTime;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainerImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;
import ru.prolib.aquila.transaq.remote.MessageFields.FQuotation;

public class SecurityQuotations extends ObservableStateContainerImpl {

	public SecurityQuotations(OSCParams params) {
		super(params);
	}
	
	public int getSecID() {
		return getInteger(FQuotation.SECID);
	}
	
	public String getBoardCode() {
		return getString(FQuotation.BOARD);
	}
	
	public String getSecCode() {
		return getString(FQuotation.SECCODE);
	}
	
	public CDecimal getPointCost() {
		return getCDecimal(FQuotation.POINT_COST);
	}
	
	public CDecimal getAccruedIntValue() {
		return getCDecimal(FQuotation.ACCRUED_INT_VALUE);
	}
	
	public CDecimal getOpen() {
		return getCDecimal(FQuotation.OPEN);
	}
	
	public CDecimal getWAPrice() {
		return getCDecimal(FQuotation.WA_PRICE);
	}
	
	public Integer getBidDepth() {
		return getInteger(FQuotation.BID_DEPTH);
	}
	
	public Integer getBidDepthT() {
		return getInteger(FQuotation.BID_DEPTH_T);
	}
	
	public Integer getNumBids() {
		return getInteger(FQuotation.NUM_BIDS);
	}
	
	public Integer getOfferDepth() {
		return getInteger(FQuotation.OFFER_DEPTH);
	}
	
	public Integer getOfferDepthT() {
		return getInteger(FQuotation.OFFER_DEPTH_T);
	}
	
	public CDecimal getBid() {
		return getCDecimal(FQuotation.BID);
	}
	
	public CDecimal getOffer() {
		return getCDecimal(FQuotation.OFFER);
	}
	
	public Integer getNumOffers() {
		return getInteger(FQuotation.NUM_OFFERS);
	}
	
	public Integer getNumTrades() {
		return getInteger(FQuotation.NUM_TRADES);
	}
	
	public Integer getVolToday() {
		return getInteger(FQuotation.VOL_TODAY);
	}
	
	public Integer getOpenPositions() {
		return getInteger(FQuotation.OPEN_POSITIONS);
	}
	
	public Integer getDeltaPositions() {
		return getInteger(FQuotation.DELTA_POSITIONS);
	}
	
	public CDecimal getLast() {
		return getCDecimal(FQuotation.LAST);
	}
	
	public Integer getQuantity() {
		return getInteger(FQuotation.QUANTITY);
	}
	
	public LocalDateTime getTime() {
		return (LocalDateTime) getObject(FQuotation.TIME);
	}
	
	public CDecimal getChange() {
		return getCDecimal(FQuotation.CHANGE);
	}
	
	public CDecimal getPriceMinusPrevWAPrice() {
		return getCDecimal(FQuotation.PRICE_MINUS_PREV_WA_PRICE);
	}
	
	public CDecimal getValToday() {
		return getCDecimal(FQuotation.VAL_TODAY);
	}
	
	public CDecimal getYield() {
		return getCDecimal(FQuotation.YIELD);
	}
	
	public CDecimal getYieldAtWAPrice() {
		return getCDecimal(FQuotation.YIELD_AT_WA_PRICE);
	}

	public CDecimal getMarketPriceToday() {
		return getCDecimal(FQuotation.MARKET_PRICE_TODAY);
	}
	
	public CDecimal getHighBid() {
		return getCDecimal(FQuotation.HIGH_BID);
	}
	
	public CDecimal getLowOffer() {
		return getCDecimal(FQuotation.LOW_OFFER);
	}
	
	public CDecimal getHigh() {
		return getCDecimal(FQuotation.HIGH);
	}
	
	public CDecimal getLow() {
		return getCDecimal(FQuotation.LOW);
	}
	
	public CDecimal getClosePrice() {
		return getCDecimal(FQuotation.CLOSE_PRICE);
	}
	
	public CDecimal getCloseYield() {
		return getCDecimal(FQuotation.CLOSE_YIELD);
	}
	
	public String getStatus() {
		return getString(FQuotation.STATUS);
	}
	
	public String getTradingStatus() {
		return getString(FQuotation.TRADING_STATUS);
	}
	
	public CDecimal getBuyDeposit() {
		return getCDecimal(FQuotation.BUY_DEPOSIT);
	}
	
	public CDecimal getSellDeposit() {
		return getCDecimal(FQuotation.SELL_DEPOSIT);
	}
	
	public CDecimal getVolatility() {
		return getCDecimal(FQuotation.VOLATILITY);
	}
	
	public CDecimal getTheoreticalPrice() {
		return getCDecimal(FQuotation.THEORETICAL_PRICE);
	}
	
	public CDecimal getBGO_BUY() {
		return getCDecimal(FQuotation.BGO_BUY);
	}
	
	public CDecimal getLCurrentPrice() {
		return getCDecimal(FQuotation.L_CURRENT_PRICE);
	}
	
}

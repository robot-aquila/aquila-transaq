package ru.prolib.aquila.transaq.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.transaq.entity.SecurityBoardParams;
import ru.prolib.aquila.transaq.entity.SecurityParams;
import ru.prolib.aquila.transaq.entity.SecurityQuotations;
import ru.prolib.aquila.transaq.remote.MessageFields.FQuotation;
import ru.prolib.aquila.transaq.remote.MessageFields.FSecurity;
import ru.prolib.aquila.transaq.remote.MessageFields.FSecurityBoard;

public class TQFieldAssembler {
	private static final ZoneId ZONE_ID = ZoneId.of("Europe/Moscow");

	/*
	SecurityField.DISPLAY_NAME			= sp#ShortName@board#Code;
	SecurityField.INITIAL_MARGIN		= MAX(sp#BuyDeposit, sp#SellDeposit) (FORTS only)
	SecurityField.UPPER_PRICE_LIMIT		= sp#MinPrice (FORTS only)
	SecurityField.LOWER_PRICE_LIMIT		= sp#MaxPrice (FORTS only)
	SecurityField.SETTLEMENT_PRICE		= N/A (получение данных не подтверждено) sp#ClearingPrice (FORTS only)
	SecurityField.EXPIRATION_TIME		= N/A (получение данных не подтверждено) sp@MatDate
	
	Проблема: settlement price определить не удается. Не передается clearing_price даже для фортса.
				
	SecurityField.LOT_SIZE 				= sbp#LotSize
	SecurityField.TICK_SIZE				= sbp#MinStep
	SecurityField.TICK_VALUE			= 10 ^ sbp#Decimals * sbp#PointCost * sbp#MinStep / 100

	SecurityField.OPEN_PRICE			= quot#Open
	SecurityField.HIGH_PRICE			= quot#High
	SecurityField.LOW_PRICE				= quot#Low
	SecurityField.CLOSE_PRICE			= quot#ClosePrice
	
	BestBid								= price=quot#Bid, qty=quot#BidDepth, time=NOW
	BestAsk								= price=quot#Offer, qty=quot#OfferDepth, time=NOW
	Last								= Поток alltrades 
	MarketDepth							= Поток quotes
	 */

	public void toSecDisplayName(SecurityParams tq_sec_state, String board_name, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( ! tq_sec_state.isDefined(FSecurity.SHORT_NAME) || board_name == null ) {
			return;
		}
		aq_sec_upd_builder.withToken(SecurityField.DISPLAY_NAME,
				String.format("%s @ %s", tq_sec_state.getString(FSecurity.SHORT_NAME), board_name));
	}
	
	public void toSecInitialMargin(SecurityParams tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		// SecurityField.INITIAL_MARGIN = MAX(TQSecField.BUY_DEPOSIT, TQSecField.SELL_DEPOSIT)
		CDecimal one_rub5 = CDecimalBD.ofRUB5("1");
		CDecimal b_depo = tq_sec_state.getCDecimal(FSecurity.BUY_DEPOSIT);
		CDecimal s_depo = tq_sec_state.getCDecimal(FSecurity.SELL_DEPOSIT);
		if ( b_depo != null && s_depo != null ) {
			aq_sec_upd_builder.withToken(SecurityField.INITIAL_MARGIN, one_rub5.multiply(b_depo.max(s_depo)));
		} else if ( b_depo != null ) {
			aq_sec_upd_builder.withToken(SecurityField.INITIAL_MARGIN, one_rub5.multiply(b_depo));
		} else if ( s_depo != null ) {
			aq_sec_upd_builder.withToken(SecurityField.INITIAL_MARGIN, one_rub5.multiply(s_depo));
		}
	}

	public void toSecLotSize(SecurityBoardParams tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( tq_sec_state.isDefined(FSecurityBoard.LOTSIZE) ) {
			aq_sec_upd_builder.withToken(SecurityField.LOT_SIZE, tq_sec_state.getCDecimal(FSecurityBoard.LOTSIZE));
		}
	}

	public void toSecTickSize(SecurityBoardParams tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( tq_sec_state.isDefined(FSecurityBoard.MINSTEP) ) {
			aq_sec_upd_builder.withToken(SecurityField.TICK_SIZE, tq_sec_state.getCDecimal(FSecurityBoard.MINSTEP));
		}
	}

	public void toSecTickValue(SecurityBoardParams tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		// SecurityField.TICK_VALUE = TQSecField.POINT_COST * TQSecField.MINSTEP * 10 ^ TQSecField.DECIMALS
		Integer decimals = tq_sec_state.getInteger(FSecurityBoard.DECIMALS);
		CDecimal point_cost = tq_sec_state.getCDecimal(FSecurityBoard.POINT_COST);
		CDecimal min_step = tq_sec_state.getCDecimal(FSecurityBoard.MINSTEP);
		if ( decimals == null || point_cost == null || min_step == null ) {
			return;
		}
		CDecimal tick_value = CDecimalBD.ofRUB5("10")
				.pow(decimals)
				.multiply(point_cost)
				.multiply(min_step)
				.divideExact(100L, 5);
		aq_sec_upd_builder.withToken(SecurityField.TICK_VALUE, tick_value);
	}

	public void toSecSettlementPrice(SecurityParams tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( tq_sec_state.isDefined(FSecurity.CLEARING_PRICE) ) {
			aq_sec_upd_builder.withToken(SecurityField.SETTLEMENT_PRICE, tq_sec_state.getCDecimal(FSecurity.CLEARING_PRICE));
		}
	}

	public void toSecLowerPriceLimit(SecurityParams tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( tq_sec_state.isDefined(FSecurity.MINPRICE) ) {
			aq_sec_upd_builder.withToken(SecurityField.LOWER_PRICE_LIMIT, tq_sec_state.getCDecimal(FSecurity.MINPRICE));
		}
	}

	public void toSecUpperPriceLimit(SecurityParams tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( tq_sec_state.isDefined(FSecurity.MAXPRICE) ) {
			aq_sec_upd_builder.withToken(SecurityField.UPPER_PRICE_LIMIT, tq_sec_state.getCDecimal(FSecurity.MAXPRICE));
		}
	}

	public void toSecOpenPrice(SecurityQuotations tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( tq_sec_state.isDefined(FQuotation.OPEN) ) {
			aq_sec_upd_builder.withToken(SecurityField.OPEN_PRICE, tq_sec_state.getCDecimal(FQuotation.OPEN));
		}
	}

	public void toSecHighPrice(SecurityQuotations tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( tq_sec_state.isDefined(FQuotation.HIGH) ) {
			aq_sec_upd_builder.withToken(SecurityField.HIGH_PRICE, tq_sec_state.getCDecimal(FQuotation.HIGH));	
		}
	}

	public void toSecLowPrice(SecurityQuotations tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( tq_sec_state.isDefined(FQuotation.LOW) ) {
			aq_sec_upd_builder.withToken(SecurityField.LOW_PRICE, tq_sec_state.getCDecimal(FQuotation.LOW));
		}
	}

	public void toSecClosePrice(SecurityQuotations tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( tq_sec_state.isDefined(FQuotation.CLOSE_PRICE) ) {
			aq_sec_upd_builder.withToken(SecurityField.CLOSE_PRICE, tq_sec_state.getCDecimal(FQuotation.CLOSE_PRICE));
		}
	}

	public void toSecExpirationTime(SecurityParams tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( tq_sec_state.isDefined(FSecurity.MAT_DATE) ) {
			LocalDateTime x = (LocalDateTime) tq_sec_state.getObject(FSecurity.MAT_DATE);
			aq_sec_upd_builder.withToken(SecurityField.EXPIRATION_TIME, x.atZone(ZONE_ID).toInstant());
		}
	}

}

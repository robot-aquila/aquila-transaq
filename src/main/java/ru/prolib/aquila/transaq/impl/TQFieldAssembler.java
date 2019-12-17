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

	public int toSecDisplayName(SecurityParams tq_sec_state, String board_name, DeltaUpdateBuilder aq_sec_upd_builder) {
		String short_name = tq_sec_state.getString(FSecurity.SHORT_NAME);
		if ( short_name == null || board_name == null ) {
			return 0;
		}
		aq_sec_upd_builder.withToken(SecurityField.DISPLAY_NAME, String.format("%s @ %s", short_name, board_name));
		return 1;
	}
	
	public int toSecInitialMargin(SecurityParams tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		// SecurityField.INITIAL_MARGIN = MAX(TQSecField.BUY_DEPOSIT, TQSecField.SELL_DEPOSIT)
		int expected_tokens[] = { FSecurity.BUY_DEPOSIT, FSecurity.SELL_DEPOSIT };
		if( ! tq_sec_state.atLeastOneHasChanged(expected_tokens) ) {
			return 0;
		}
		CDecimal one_rub5 = CDecimalBD.ofRUB5("1");
		CDecimal b_depo = tq_sec_state.getCDecimal(FSecurity.BUY_DEPOSIT);
		CDecimal s_depo = tq_sec_state.getCDecimal(FSecurity.SELL_DEPOSIT);
		if ( b_depo != null && s_depo != null ) {
			aq_sec_upd_builder.withToken(SecurityField.INITIAL_MARGIN, one_rub5.multiply(b_depo.max(s_depo)));
			return 1;
		}
		if ( b_depo != null ) {
			aq_sec_upd_builder.withToken(SecurityField.INITIAL_MARGIN, one_rub5.multiply(b_depo));
			return 1;
		}
		if ( s_depo != null ) {
			aq_sec_upd_builder.withToken(SecurityField.INITIAL_MARGIN, one_rub5.multiply(s_depo));
			return 1;
		}
		return 0;
	}

	public int toSecLotSize(SecurityBoardParams tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( ! tq_sec_state.hasChanged(FSecurityBoard.LOTSIZE) ) {
			return 0;
		}
		aq_sec_upd_builder.withToken(SecurityField.LOT_SIZE, tq_sec_state.getCDecimal(FSecurityBoard.LOTSIZE));
		return 1;
	}

	public int toSecTickSize(SecurityBoardParams tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( ! tq_sec_state.hasChanged(FSecurityBoard.MINSTEP) ) {
			return 0;
		}
		aq_sec_upd_builder.withToken(SecurityField.TICK_SIZE, tq_sec_state.getCDecimal(FSecurityBoard.MINSTEP));
		return 1;
	}

	public int toSecTickValue(SecurityBoardParams tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		// SecurityField.TICK_VALUE = TQSecField.POINT_COST * TQSecField.MINSTEP * 10 ^ TQSecField.DECIMALS
		int expected_tokens[] = { FSecurityBoard.POINT_COST, FSecurityBoard.MINSTEP, FSecurityBoard.DECIMALS };
		if ( ! tq_sec_state.atLeastOneHasChanged(expected_tokens) ) {
			return 0;
		}
		Integer decimals = tq_sec_state.getInteger(FSecurityBoard.DECIMALS);
		CDecimal point_cost = tq_sec_state.getCDecimal(FSecurityBoard.POINT_COST);
		CDecimal min_step = tq_sec_state.getCDecimal(FSecurityBoard.MINSTEP);
		if ( decimals == null || point_cost == null || min_step == null ) {
			return 0;
		}
		CDecimal tick_value = CDecimalBD.ofRUB5("10")
				.pow(decimals)
				.multiply(point_cost)
				.multiply(min_step)
				.divideExact(100L, 5);
		aq_sec_upd_builder.withToken(SecurityField.TICK_VALUE, tick_value);
		return 1;
	}

	public int toSecSettlementPrice(SecurityParams tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( ! tq_sec_state.hasChanged(FSecurity.CLEARING_PRICE) ) {
			return 0;
		}
		aq_sec_upd_builder.withToken(SecurityField.SETTLEMENT_PRICE, tq_sec_state.getCDecimal(FSecurity.CLEARING_PRICE));
		return 1;
	}

	public int toSecLowerPriceLimit(SecurityParams tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( ! tq_sec_state.hasChanged(FSecurity.MINPRICE) ) {
			return 0;
		}
		aq_sec_upd_builder.withToken(SecurityField.LOWER_PRICE_LIMIT, tq_sec_state.getCDecimal(FSecurity.MINPRICE));
		return 1;
	}

	public int toSecUpperPriceLimit(SecurityParams tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( ! tq_sec_state.hasChanged(FSecurity.MAXPRICE) ) {
			return 0;
		}
		aq_sec_upd_builder.withToken(SecurityField.UPPER_PRICE_LIMIT, tq_sec_state.getCDecimal(FSecurity.MAXPRICE));
		return 1;
	}

	public int toSecOpenPrice(SecurityQuotations tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( ! tq_sec_state.hasChanged(FQuotation.OPEN) ) {
			return 0;
		}
		aq_sec_upd_builder.withToken(SecurityField.OPEN_PRICE, tq_sec_state.getCDecimal(FQuotation.OPEN));
		return 1;
	}

	public int toSecHighPrice(SecurityQuotations tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( ! tq_sec_state.hasChanged(FQuotation.HIGH) ) {
			return 0;
		}
		aq_sec_upd_builder.withToken(SecurityField.HIGH_PRICE, tq_sec_state.getCDecimal(FQuotation.HIGH));
		return 1;
	}

	public int toSecLowPrice(SecurityQuotations tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( ! tq_sec_state.hasChanged(FQuotation.LOW) ) {
			return 0;
		}
		aq_sec_upd_builder.withToken(SecurityField.LOW_PRICE, tq_sec_state.getCDecimal(FQuotation.LOW));
		return 1;
	}

	public int toSecClosePrice(SecurityQuotations tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( ! tq_sec_state.hasChanged(FQuotation.CLOSE_PRICE) ) {
			return 0;
		}
		aq_sec_upd_builder.withToken(SecurityField.CLOSE_PRICE, tq_sec_state.getCDecimal(FQuotation.CLOSE_PRICE));
		return 1;
	}

	public int toSecExpirationTime(SecurityParams tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( ! tq_sec_state.hasChanged(FSecurity.MAT_DATE) ) {
			return 0;
		}
		LocalDateTime x = (LocalDateTime) tq_sec_state.getObject(FSecurity.MAT_DATE);
		aq_sec_upd_builder.withToken(SecurityField.EXPIRATION_TIME, x.atZone(ZONE_ID).toInstant());
		return 1;
	}

}

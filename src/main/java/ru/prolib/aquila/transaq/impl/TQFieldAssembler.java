package ru.prolib.aquila.transaq.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainer;
import ru.prolib.aquila.transaq.entity.Board;
import ru.prolib.aquila.transaq.entity.SecType;
import ru.prolib.aquila.transaq.entity.SecurityBoardParams;
import ru.prolib.aquila.transaq.impl.TQField.FSecurity;

public class TQFieldAssembler {
	private static final Map<SecType, SymbolType> TYPE_MAP;
	private static final ZoneId ZONE_ID = ZoneId.of("Europe/Moscow");
	
	static {
		TYPE_MAP = new HashMap<>();
		TYPE_MAP.put(SecType.BOND, SymbolType.BOND);
		TYPE_MAP.put(SecType.CURRENCY, SymbolType.CURRENCY);
		TYPE_MAP.put(SecType.ETS_CURRENCY, SymbolType.CURRENCY);
		TYPE_MAP.put(SecType.FOB, SymbolType.FUTURES);
		TYPE_MAP.put(SecType.FUT, SymbolType.FUTURES);
		TYPE_MAP.put(SecType.GKO, SymbolType.BOND);
		TYPE_MAP.put(SecType.OPT, SymbolType.OPTION);
		TYPE_MAP.put(SecType.SHARE, SymbolType.STOCK);
	}
	
	private final TQDirectory dir;
	
	public TQFieldAssembler(TQDirectory directory) {
		this.dir = directory;
	}

	public Symbol toSymbol(TQSecID_F id) {
		SymbolType type = TYPE_MAP.get(id.getType());
		if ( type == null ) {
			type =  SymbolType.UNKNOWN;
		}
		String secCode = id.getSecCode();
		if ( id.getType() == SecType.FUT ) {
			secCode = id.getShortName();
		}
		return new Symbol(secCode, id.getDefaultBoard(), CDecimalBD.RUB, type);
	}
	
	/**
	 * Convert symbol to transaq security ID.
	 * <p>
	 * @param symbol - symbol
	 * @return - transaq equivalent
	 */
	public TQSecID2 toSecID2(Symbol symbol) {		
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public int toSecDisplayName(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( ! tq_sec_state.hasChanged(FSecurity.SHORT_NAME) ) {
			return 0;
		}
		aq_sec_upd_builder.withToken(SecurityField.DISPLAY_NAME, tq_sec_state.getString(FSecurity.SHORT_NAME));
		return 1;
	}

	public int toSecLotSize(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( ! tq_sec_state.hasChanged(FSecurity.LOTSIZE) ) {
			return 0;
		}
		aq_sec_upd_builder.withToken(SecurityField.LOT_SIZE, tq_sec_state.getCDecimal(FSecurity.LOTSIZE));
		return 1;
	}

	public int toSecTickSize(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( ! tq_sec_state.hasChanged(FSecurity.MINSTEP) ) {
			return 0;
		}
		aq_sec_upd_builder.withToken(SecurityField.TICK_SIZE, tq_sec_state.getCDecimal(FSecurity.MINSTEP));
		return 1;
	}

	public int toSecTickValue(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		// SecurityField.TICK_VALUE = TQSecField.POINT_COST * TQSecField.MINSTEP * 10 ^ TQSecField.DECIMALS
		int expected_tokens[] = { FSecurity.POINT_COST, FSecurity.MINSTEP, FSecurity.DECIMALS };
		if ( ! tq_sec_state.atLeastOneHasChanged(expected_tokens) ) {
			return 0;
		}
		Integer decimals = tq_sec_state.getInteger(FSecurity.DECIMALS);
		CDecimal point_cost = tq_sec_state.getCDecimal(FSecurity.POINT_COST);
		CDecimal min_step = tq_sec_state.getCDecimal(FSecurity.MINSTEP);
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

	public int toSecInitialMargin(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
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

	public int toSecSettlementPrice(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( ! tq_sec_state.hasChanged(FSecurity.CLEARING_PRICE) ) {
			return 0;
		}
		aq_sec_upd_builder.withToken(SecurityField.SETTLEMENT_PRICE, tq_sec_state.getCDecimal(FSecurity.CLEARING_PRICE));
		return 1;
	}

	public int toSecLowerPriceLimit(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( ! tq_sec_state.hasChanged(FSecurity.MINPRICE) ) {
			return 0;
		}
		aq_sec_upd_builder.withToken(SecurityField.LOWER_PRICE_LIMIT, tq_sec_state.getCDecimal(FSecurity.MINPRICE));
		return 1;
	}

	public int toSecUpperPriceLimit(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( ! tq_sec_state.hasChanged(FSecurity.MAXPRICE) ) {
			return 0;
		}
		aq_sec_upd_builder.withToken(SecurityField.UPPER_PRICE_LIMIT, tq_sec_state.getCDecimal(FSecurity.MAXPRICE));
		return 1;
	}

	public int toSecOpenPrice(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		// TODO: not yet done
		return 0;
	}

	public int toSecHighPrice(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		// TODO: not yet done
		return 0;
	}

	public int toSecLowPrice(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		// TODO: not yet done
		return 0;
	}

	public int toSecClosePrice(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		// TODO: not yet done
		return 0;
	}

	public int toSecExpirationTime(UpdatableStateContainer tq_sec_state, DeltaUpdateBuilder aq_sec_upd_builder) {
		if ( ! tq_sec_state.hasChanged(FSecurity.MAT_DATE) ) {
			return 0;
		}
		LocalDateTime x = (LocalDateTime) tq_sec_state.getObject(FSecurity.MAT_DATE);
		aq_sec_upd_builder.withToken(SecurityField.EXPIRATION_TIME, x.atZone(ZONE_ID).toInstant());
		return 1;
	}

}

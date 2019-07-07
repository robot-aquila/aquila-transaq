package ru.prolib.aquila.transaq.impl;

import java.util.Set;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainerImpl;

public class TQSecurityHandlerImpl implements TQSecurityHandler {
	private final TQSecID_F id;
	private final UpdatableStateContainerImpl state;
	private volatile DeltaUpdateConsumer consumer;
	
	public TQSecurityHandlerImpl(TQSecID_F id) {
		this.id = id;
		state = new UpdatableStateContainerImpl("TQ-SEC");
	}
	
	@Override
	public TQSecID_F getSecID3() {
		return id;
	}
	
	@Override
	public Symbol getSymbol() {
		return null;
	}
	
	@Override
	public void setConsumer(DeltaUpdateConsumer consumer) {
		this.consumer = consumer;
		// TODO: send snapshot
	}
	
	@Override
	public void update(DeltaUpdate update) {
		DeltaUpdateBuilder builder = null;
		DeltaUpdateConsumer consumer = this.consumer;
		state.lock();
		try {
			state.consume(update);
			if ( consumer == null || ! state.hasChanged() ) {
				return;
			}
			builder = new DeltaUpdateBuilder();
			Set<Integer> changed_tokens = state.getUpdatedTokens();
			
			// 1) to detect symbol (aquila style) required:
			// TQSecField.SECTYPE to convert to aquila type
			// TQSecField.SHORT_NAME as code
			// TQSecField.MARKET
			// currency is always RUB


			// SecurityField.DISPLAY_NAME = TQSecField.SHORT_NAME
			// SecurityField.LOT_SIZE = TQSecField.LOTSIZE
			// SecurityField.TICK_SIZE = TQSecField.MINSTEP
			// SecurityField.TICK_VALUE = TQSecField.POINT_COST * TQSecField.MINSTEP * 10 ^ TQSecField.DECIMALS
			// SecurityField.INITIAL_MARGIN = MAX(TQSecField.BUY_DEPOSIT, TQSecField.SELL_DEPOSIT)
			// SecurityField.SETTLEMENT_PRICE = TQSecField.CLEARING_PRICE
			// SecurityField.LOWER_PRICE_LIMIT = TQSecField.MINPRICE
			// SecurityField.UPPER_PRICE_LIMIT = TQSecField.MAXPRICE
			// SecurityField.OPEN_PRICE
			// SecurityField.HIGH_PRICE
			// SecurityField.LOW_PRICE
			// SecurityField.CLOSE_PRICE
			// SecurityField.EXPIRATION_TIME = TQSecField.MAT_DATE
			
		} finally {
			state.unlock();
		}
	}

}

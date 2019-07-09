package ru.prolib.aquila.transaq.impl;

import java.util.Set;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainer;
import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainerImpl;

public class TQSecurityHandlerImpl implements TQSecurityHandler {
	private final TQSecID_F id;
	private final Symbol symbol;
	private final DeltaUpdateConsumer consumer;
	private final UpdatableStateContainer state;
	
	public TQSecurityHandlerImpl(TQSecID_F id,
			Symbol symbol,
			DeltaUpdateConsumer consumer,
			UpdatableStateContainer state)
	{
		this.id = id;
		this.symbol = symbol;
		this.consumer = consumer;
		this.state = state;
	}
	
	public TQSecurityHandlerImpl(TQSecID_F id,
			Symbol symbol,
			DeltaUpdateConsumer consumer)
	{
		this(id, symbol, consumer, new UpdatableStateContainerImpl("TQ-SEC-" + symbol.toString()));
	}
	
	@Override
	public TQSecID_F getSecID3() {
		return id;
	}
	
	@Override
	public Symbol getSymbol() {
		return symbol;
	}
	
	@Override
	public void update(DeltaUpdate update) {
		state.lock();
		try {
			state.consume(update);
			if ( ! state.hasChanged() ) {
				return;
			}
			DeltaUpdateBuilder builder = new DeltaUpdateBuilder();
			
			
			Set<Integer> changed_tokens = state.getUpdatedTokens();
			if ( changed_tokens.contains(TQSecField.SHORT_NAME) ) {
				builder.withToken(SecurityField.DISPLAY_NAME, state.getString(TQSecField.SHORT_NAME));
			}
			if ( changed_tokens.contains(TQSecField.LOTSIZE) ) {
			//	builder.withToken(SecurityField.LOT_SIZE, value)
			}


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

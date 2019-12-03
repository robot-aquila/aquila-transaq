package ru.prolib.aquila.transaq.impl;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainer;
import ru.prolib.aquila.transaq.remote.TQSecIDF;

public class TQSecurityHandler {
	private final TQSecIDF id;
	private final Symbol symbol;
	private final DeltaUpdateConsumer consumer;
	private final UpdatableStateContainer state;
	private final TQFieldAssembler assembler;
	
	public TQSecurityHandler(TQSecIDF id,
			Symbol symbol,
			DeltaUpdateConsumer consumer,
			UpdatableStateContainer state,
			TQFieldAssembler assembler)
	{
		this.id = id;
		this.symbol = symbol;
		this.consumer = consumer;
		this.state = state;
		this.assembler = assembler;
	}
	
	public TQSecIDF getSecID3() {
		return id;
	}
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	public DeltaUpdateConsumer getUpdateConsumer() {
		return consumer;
	}
	
	public UpdatableStateContainer getStateContainer() {
		return state;
	}
	
	public TQFieldAssembler getAssembler() {
		return assembler;
	}
	
	public void update(DeltaUpdate update) {
		int consume = 0;
		DeltaUpdateBuilder builder = null;
		state.lock();
		try {
			state.consume(update);
			if ( ! state.hasChanged() ) {
				return;
			}
			builder = new DeltaUpdateBuilder();
			consume = assembler.toSecDisplayName(state, builder)
				| assembler.toSecLotSize(state, builder)
				| assembler.toSecTickSize(state, builder)
				| assembler.toSecTickValue(state, builder)
				| assembler.toSecInitialMargin(state, builder)
				| assembler.toSecSettlementPrice(state, builder)
				| assembler.toSecLowerPriceLimit(state, builder)
				| assembler.toSecUpperPriceLimit(state, builder)
				| assembler.toSecOpenPrice(state, builder)
				| assembler.toSecHighPrice(state, builder)
				| assembler.toSecLowPrice(state, builder)
				| assembler.toSecClosePrice(state, builder)
				| assembler.toSecExpirationTime(state, builder);
		} finally {
			state.unlock();
		}
		if ( consume != 0 ) {
			consumer.consume(builder.buildUpdate());
		}
	}

}

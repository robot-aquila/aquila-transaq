package ru.prolib.aquila.transaq.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.transaq.engine.Engine;

public class TQSymbolSubscrHandler implements SubscrHandler {
	private final Engine engine;
	private final Symbol symbol;
	private final MDLevel level;
	private final AtomicBoolean closed;
	
	public TQSymbolSubscrHandler(Engine engine, Symbol symbol, MDLevel level) {
		this.engine = engine;
		this.symbol = symbol;
		this.level = level;
		this.closed = new AtomicBoolean(false);
	}
	
	public Engine getEngine() {
		return engine;
	}
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	public MDLevel getLevel() {
		return level;
	}
	
	public boolean isClosed() {
		return closed.get();
	}

	@Override
	public void close() {
		if ( closed.compareAndSet(false, true) ) {
			engine.unsubscribeSymbol(symbol, level);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TQSymbolSubscrHandler.class ) {
			return false;
		}
		TQSymbolSubscrHandler o = (TQSymbolSubscrHandler) other;
		return new EqualsBuilder()
				.append(o.engine, engine)
				.append(o.symbol, symbol)
				.append(o.level, level)
				.append(o.closed.get(), closed.get())
				.build();
	}

}

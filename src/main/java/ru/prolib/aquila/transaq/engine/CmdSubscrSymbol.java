package ru.prolib.aquila.transaq.engine;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class CmdSubscrSymbol extends Cmd {
	private final Symbol symbol;
	private final MDLevel level;

	public CmdSubscrSymbol(Symbol symbol, MDLevel level) {
		super(CmdType.SUBSCR_SYMBOL);
		this.symbol = symbol;
		this.level = level;
	}
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	public MDLevel getLevel() {
		return level;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(187822101, 984511)
				.append(getType())
				.append(symbol)
				.append(level)
				.build();
	}

	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CmdSubscrSymbol.class ) {
			return false;
		}
		CmdSubscrSymbol o = (CmdSubscrSymbol) other;
		return new EqualsBuilder()
				.append(o.symbol, symbol)
				.append(o.level, level)
				.build();
	}

}

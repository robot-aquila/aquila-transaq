package ru.prolib.aquila.transaq.engine;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class CmdUnsubscrSymbol extends Cmd {
	private final Symbol symbol;
	private final MDLevel level;
	
	public CmdUnsubscrSymbol(Symbol symbol, MDLevel level) {
		super(CmdType.UNSUBSCR_SYMBOL);
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
		return new HashCodeBuilder(91234773, 9005)
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
		if ( other == null || other.getClass() != CmdUnsubscrSymbol.class ) {
			return false;
		}
		CmdUnsubscrSymbol o = (CmdUnsubscrSymbol) other;
		return new EqualsBuilder()
				.append(o.getType(), getType())
				.append(o.symbol, symbol)
				.append(o.level, level)
				.build();
	}

}

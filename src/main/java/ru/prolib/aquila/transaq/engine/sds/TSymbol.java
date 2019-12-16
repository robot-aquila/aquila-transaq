package ru.prolib.aquila.transaq.engine.sds;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;

/**
 * Tradable Symbol Identifier.
 * <p>
 * See {@link GSymbol} for more info.
 */
public class TSymbol extends Symbol {
	private static final long serialVersionUID = 1L;
	
	private static SymbolType checkType(SymbolType type) {
		if ( type == null ) {
			throw new IllegalArgumentException("Type must be not null");
		}
		return type;
	}
	
	private static String checkCode(String sec_code) {
		if ( sec_code == null ) {
			throw new IllegalArgumentException("Security code must be not null");
		}
		return sec_code;
	}
	
	public TSymbol(String sec_code, String board_code, String currency_code, SymbolType type) {
		super(checkCode(sec_code), board_code, currency_code, checkType(type));
		if ( board_code == null ) {
			throw new IllegalArgumentException("Board code must be not null");
		}
		if ( currency_code == null ) {
			throw new IllegalArgumentException("Currency code must be not null");
		}

	}
	
	public String getBoardCode() {
		return getExchangeID();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TSymbol.class ) {
			return false;
		}
		return super.equals(other);
	}

}

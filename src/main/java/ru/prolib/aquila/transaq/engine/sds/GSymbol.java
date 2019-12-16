package ru.prolib.aquila.transaq.engine.sds;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;

/**
 * General Symbol Identifier - This class represents unique symbol of security.
 * <p>
 * The first problem of MOEX is that securities of some types (futures, options)
 * have repeated codes which causes ambiguity. That mean you cannot use provided
 * security codes without risk of conflict. Although this problem will arise one
 * time per decade per symbol we must guarantee that the software will work
 * properly any length of time.
 * <p>
 * This class introduced to draw attention to the problem and identify a way
 * how it'd solved. To solve this issue we use a special ticker code which make
 * sure that the security is always unique identified. For some securities it
 * is security code as it is in MOEX (for example shares). But for some
 * specific types (futures, options) it is some kind derivative of security
 * properties.
 * <p>
 * The next issue is that there is no guarantee that security with same
 * security codes wouldn't appear on different markets. To solve that we'll use
 * market ID as part of security primary key. Thus if there are several
 * securities with same codes but different markets no conflicts on updates
 * will appear.
 * <p>
 * The third problem with MOEX securities is that there are different boards
 * where security can be traded and those boards can declare different values
 * of similar parameters of the same security. So we have single security but
 * it is different while trading at different boards, but still same while
 * counted in account. To solve that we'll identity same security which quoted
 * on different boards as different securities. 
 * <p>
 * To make that possible the {@link TSymbol} class is used to extend this
 * class with board code. Thus to get quotes or place an order the full
 * identifier (including board code) should be provided. To count security into
 * account without breaking the rule above default board must be used to point to
 * a security despite which board it was bought from or on which board it
 * was sold. Thus there is only one identifier is possible for securities
 * displayed in accounts: ticker with default board. 
 */
public class GSymbol extends Symbol {
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
	
	public GSymbol(String sec_code, String market_code, String currency_code, SymbolType type) {
		super(checkCode(sec_code), market_code, currency_code, checkType(type));
		if ( market_code == null ) {
			throw new IllegalArgumentException("Market code must be not null");
		}
		if ( currency_code == null ) {
			throw new IllegalArgumentException("Currency code must be not null");
		}
	}
	
	public String getMarketCode() {
		return getExchangeID();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != GSymbol.class ) {
			return false;
		}
		return super.equals(other);
	}

}

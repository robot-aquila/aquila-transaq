package ru.prolib.aquila.transaq.engine.sds;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Symbol General Identifier - This class represents unique symbol of security.
 * <p>
 * The first problem of MOEX is that securities of some types (futures, options)
 * have repeated codes which causes ambiguity. That mean you cannot use provided
 * security codes without risk of conflict. Although this problem will arise one
 * time per decade per symbol we must guarantee that the software will work
 * properly any length of time.
 * <p>
 * This class introduced to draw attention to the problem and identify a way
 * how it solved. To solve this issue we use a special ticker code which make
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
 * it is different while trading on different boards, but still same while
 * counted in account. To solve that we'll identity same security which quoted
 * on different boards as different securities. 
 * <p>
 * To make that possible the {@link SymbolTID} class is used to extend this
 * class by board code. Thus to get quotes or place an order the full
 * identifier (including board code) should be provided. To count security into
 * account without breaking the rule above we'll use default board to point to
 * a security independently which board it was bought from or on which board it
 * was sold. Thus there is only one identifier is possible for securities
 * displayed in accounts: ticker with default board. 
 */
public class SymbolGID {
	protected final String ticker;
	protected final int marketID;
	
	public SymbolGID(String ticker, int market_id) {
		this.ticker = ticker;
		this.marketID = market_id;
	}
	
	
	public String getTicker() {
		return ticker;
	}
	
	public int getMarketID() {
		return marketID;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(12377015, 507)
				.append(ticker)
				.append(marketID)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SymbolGID.class ) {
			return false;
		}
		SymbolGID o = (SymbolGID) other;
		return new EqualsBuilder()
				.append(o.ticker, ticker)
				.append(o.marketID, marketID)
				.build();
	}

}

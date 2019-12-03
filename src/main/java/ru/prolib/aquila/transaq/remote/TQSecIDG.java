package ru.prolib.aquila.transaq.remote;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TQSecIDG implements ISecIDG {
	protected final String secCode;
	protected final int marketID;
	
	public TQSecIDG(String sec_code, int market_id) {
		if ( sec_code == null || sec_code.length() == 0 ) {
			throw new IllegalArgumentException("Invalid security code: " + sec_code);
		}
		this.secCode = sec_code;
		this.marketID = market_id;
	}
	
	public TQSecIDG(TQSecIDF sec_id) {
		this(sec_id.getSecCode(), sec_id.getMarketID());
	}
	
	@Override
	public String getSecCode() {
		return secCode;
	}
	
	@Override
	public int getMarketID() {
		return marketID;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(876172303, 8123)
				.append(secCode)
				.append(marketID)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TQSecIDG.class ) {
			return false;
		}
		TQSecIDG o = (TQSecIDG) other;
		return new EqualsBuilder()
				.append(o.secCode, secCode)
				.append(o.marketID, marketID)
				.build();
	}

}

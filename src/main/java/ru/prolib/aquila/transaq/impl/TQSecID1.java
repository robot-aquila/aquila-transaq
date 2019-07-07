package ru.prolib.aquila.transaq.impl;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TQSecID1 {
	private final String secCode;
	private final int marketID;
	
	public TQSecID1(String sec_code, int market_id) {
		if ( sec_code == null || sec_code.length() == 0 ) {
			throw new IllegalArgumentException("Invalid security code: " + sec_code);
		}
		this.secCode = sec_code;
		this.marketID = market_id;
	}
	
	public TQSecID1(TQSecID_F sec_id3) {
		this(sec_id3.getSecCode(), sec_id3.getMarketID());
	}
	
	public String getSecCode() {
		return secCode;
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
		if ( other == null || other.getClass() != TQSecID1.class ) {
			return false;
		}
		TQSecID1 o = (TQSecID1) other;
		return new EqualsBuilder()
				.append(o.secCode, secCode)
				.append(o.marketID, marketID)
				.build();
	}

}

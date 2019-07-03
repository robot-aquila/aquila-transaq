package ru.prolib.aquila.transaq.impl;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TQSecID3 {
	private final String secCode;
	private final int marketID;
	private final String shortName;
	
	public TQSecID3(String sec_code, int market_id, String short_name) {
		if ( sec_code == null || sec_code.length() == 0 ) {
			throw new IllegalArgumentException("Invalid security code: " + sec_code);
		}
		if ( short_name == null || short_name.length() == 0 ) {
			throw new IllegalArgumentException("Invalid short name: " + short_name);
		}
		this.secCode = sec_code;
		this.marketID = market_id;
		this.shortName = short_name;
	}

	public String getSecCode() {
		return secCode;
	}
	
	public int getMarketID() {
		return marketID;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(234103, 1515)
				.append(secCode)
				.append(marketID)
				.append(shortName)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TQSecID3.class ) {
			return false;
		}
		TQSecID3 o = (TQSecID3) other;
		return new EqualsBuilder()
				.append(o.secCode, secCode)
				.append(o.marketID, marketID)
				.append(o.shortName, shortName)
				.build();
	}

}

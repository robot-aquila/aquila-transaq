package ru.prolib.aquila.transaq.impl;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.prolib.aquila.transaq.entity.SecType;

public class TQSecID_F {
	private final String secCode;
	private final int marketID;
	private final String shortName;
	private final SecType type;
	
	public TQSecID_F(String sec_code, int market_id, String short_name, SecType type) {
		if ( sec_code == null || sec_code.length() == 0 ) {
			throw new IllegalArgumentException("Invalid security code: " + sec_code);
		}
		if ( short_name == null || short_name.length() == 0 ) {
			throw new IllegalArgumentException("Invalid short name: " + short_name);
		}
		if ( type == null ) {
			throw new IllegalArgumentException("Type must be defined");
		}
		this.secCode = sec_code;
		this.marketID = market_id;
		this.shortName = short_name;
		this.type = type;
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
	
	public SecType getType() {
		return type;
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
				.append(type)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TQSecID_F.class ) {
			return false;
		}
		TQSecID_F o = (TQSecID_F) other;
		return new EqualsBuilder()
				.append(o.secCode, secCode)
				.append(o.marketID, marketID)
				.append(o.shortName, shortName)
				.append(o.type, type)
				.build();
	}

}

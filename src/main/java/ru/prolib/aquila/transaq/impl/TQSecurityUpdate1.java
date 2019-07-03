package ru.prolib.aquila.transaq.impl;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;

public class TQSecurityUpdate1 {
	private final TQSecID1 secID;
	private final DeltaUpdate update;
	
	public TQSecurityUpdate1(TQSecID1 secID, DeltaUpdate update) {
		this.secID = secID;
		this.update = update;
	}
	
	public TQSecID1 getSecID() {
		return secID;
	}
	
	public DeltaUpdate getUpdate() {
		return update;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TQSecurityUpdate1.class ) {
			return false;
		}
		TQSecurityUpdate1 o = (TQSecurityUpdate1) other;
		return new EqualsBuilder()
				.append(o.secID, secID)
				.append(o.update, update)
				.build();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(10087251, 7129)
				.append(secID)
				.append(update)
				.build();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}

package ru.prolib.aquila.transaq.impl;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;

public class TQSecurityUpdate3 {
	private final TQSecID3 secID;
	private final DeltaUpdate update;
	
	public TQSecurityUpdate3(TQSecID3 secID, DeltaUpdate update) {
		this.secID = secID;
		this.update = update;
	}
	
	public TQSecID3 getSecID() {
		return secID;
	}
	
	public DeltaUpdate getUpdate() {
		return update;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(1236564123, 9114243)
				.append(secID)
				.append(update)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TQSecurityUpdate3.class ) {
			return false;
		}
		TQSecurityUpdate3 o = (TQSecurityUpdate3) other;
		return new EqualsBuilder()
				.append(o.secID, secID)
				.append(o.update, update)
				.build();
	}

}

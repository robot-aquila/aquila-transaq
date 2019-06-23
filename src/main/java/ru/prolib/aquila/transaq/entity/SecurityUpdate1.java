package ru.prolib.aquila.transaq.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.transaq.impl.TQSecID1;

public class SecurityUpdate1 {
	public static final int OPMASK_USECREDIT = 0x01;
	public static final int OPMASK_BYMARKET = 0x02;
	public static final int OPMASK_NOSPLIT = 0x04;
	public static final int OPMASK_FOK = 0x08;
	public static final int OPMASK_IOC = 0x10;
	
	private final TQSecID1 secID;
	private final DeltaUpdate update;
	
	public SecurityUpdate1(TQSecID1 secID, DeltaUpdate update) {
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
		if ( other == null || other.getClass() != SecurityUpdate1.class ) {
			return false;
		}
		SecurityUpdate1 o = (SecurityUpdate1) other;
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

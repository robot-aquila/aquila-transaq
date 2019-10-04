package ru.prolib.aquila.transaq.impl;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;

public class TQStateUpdate<KeyType> {
	private final KeyType id;
	private final DeltaUpdate update;

	public TQStateUpdate(KeyType id, DeltaUpdate update) {
		this.id = id;
		this.update = update;
	}
	
	public KeyType getID() {
		return id;
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
		return new HashCodeBuilder(216688123, 5651)
				.append(id)
				.append(update)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TQStateUpdate.class ) {
			return false;
		}
		TQStateUpdate<?> o = (TQStateUpdate<?>) other;
		return new EqualsBuilder()
				.append(o.id, id)
				.append(o.update, update)
				.build();
	}
	
}

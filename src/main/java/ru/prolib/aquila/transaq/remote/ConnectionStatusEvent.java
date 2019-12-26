package ru.prolib.aquila.transaq.remote;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.EventImpl;
import ru.prolib.aquila.core.EventType;

public class ConnectionStatusEvent extends EventImpl {
	private final boolean connected;

	public ConnectionStatusEvent(EventType type, boolean connected) {
		super(type);
		this.connected = connected;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ConnectionStatusEvent.class ) {
			return false;
		}
		ConnectionStatusEvent o = (ConnectionStatusEvent) other;
		return new EqualsBuilder()
				.append(o.getType(), getType())
				.append(o.connected, connected)
				.build();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(6901127, 7103)
				.append(getType())
				.append(connected)
				.build();
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append(getClass().getSimpleName())
				.append("[")
				.append(getType().getId())
				.append(" ")
				.append(connected ? "" : "dis").append("connected]")
				.toString();
	}

}

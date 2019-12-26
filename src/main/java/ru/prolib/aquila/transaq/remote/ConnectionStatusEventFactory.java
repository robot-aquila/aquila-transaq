package ru.prolib.aquila.transaq.remote;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventFactory;
import ru.prolib.aquila.core.EventType;

public class ConnectionStatusEventFactory implements EventFactory {
	private final boolean connected;
	
	public ConnectionStatusEventFactory(boolean connected) {
		this.connected = connected;
	}

	@Override
	public Event produceEvent(EventType type) {
		return new ConnectionStatusEvent(type, connected);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(667891, 901)
				.append(connected)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ConnectionStatusEventFactory.class ) {
			return false;
		}
		ConnectionStatusEventFactory o = (ConnectionStatusEventFactory) other;
		return o.connected == connected;
	}

}

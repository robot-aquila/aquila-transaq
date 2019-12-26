package ru.prolib.aquila.transaq.remote;

import java.util.concurrent.locks.Lock;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventFactory;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsBuilder;

public class ConnectionStatus {
	private final EventDispatcher dispatcher;
	private final Lock lock;
	private final EventType onConnected, onDisconnected;
	private boolean connected = false;
	
	public ConnectionStatus(OSCParams params) {
		this.dispatcher = params.getEventDispatcher();
		this.lock = params.getLock();
		this.onConnected = new EventTypeImpl(params.getID() + ".CONNECTED");
		this.onDisconnected = new EventTypeImpl(params.getID() + ".DISCONNECTED");
	}
	
	public ConnectionStatus(EventQueue event_queue, String service_id) {
		this(new OSCParamsBuilder(event_queue)
				.withID(service_id)
				.buildParams());
	}
	
	protected EventFactory createFactory(boolean connected) {
		return new ConnectionStatusEventFactory(connected);
	}
	
	protected void setStatus(boolean connected) {
		lock.lock();
		try {
			if ( connected != this.connected ) {
				this.connected = connected;
				dispatcher.dispatch(connected ? onConnected : onDisconnected, createFactory(connected));
			}
		} finally {
			lock.unlock();
		}
	}
	
	public void setConnected() {
		setStatus(true);
	}
	
	public void setDisconnected() {
		setStatus(false);
	}
	
	public boolean isConnected() {
		lock.lock();
		try {
			return connected;
		} finally {
			lock.unlock();
		}
	}
	
	public EventType onConnected() {
		return onConnected;
	}
	
	public EventType onDisconnected() {
		return onDisconnected;
	}

}

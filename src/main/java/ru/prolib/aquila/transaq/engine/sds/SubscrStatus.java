package ru.prolib.aquila.transaq.engine.sds;

public enum SubscrStatus {
	
	/**
	 * Not subscribed to data feed.
	 */
	NOT_SUBSCR,
	
	/**
	 * Subscription request received.
	 */
	PENDING_SUBSCR,

	/**
	 * Subscribed to data feed.
	 */
	SUBSCR,

	/**
	 * Unsubscription request received.
	 */
	PENDING_UNSUBSCR,
	
	/**
	 * Unable to subscribe because feed is not available.
	 */
	NOT_AVAILABLE
	
}

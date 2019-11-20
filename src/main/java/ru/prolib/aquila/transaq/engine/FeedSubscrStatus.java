package ru.prolib.aquila.transaq.engine;

public enum FeedSubscrStatus {
	
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
	PENDING_UNSUBSCR
	
}

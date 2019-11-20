package ru.prolib.aquila.transaq.engine;

public abstract class FeedSubscr {
	protected FeedSubscrStatus status;
	
	public FeedSubscr(FeedSubscrStatus status) {
		this.status = status;
	}
	
	public FeedSubscr() {
		this(FeedSubscrStatus.NOT_SUBSCR);
	}
	
	public FeedSubscrStatus getStatus() {
		return status;
	}
	
	protected void throwCannotSwitchTo(FeedSubscrStatus new_status) {
		throw new IllegalStateException("Cannot switch from " + status + " to " + new_status);
	}
	
	public abstract void switchTo(FeedSubscrStatus new_status) throws IllegalStateException, IllegalArgumentException;

}

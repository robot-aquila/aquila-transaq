package ru.prolib.aquila.transaq.engine.sds;

/**
 * Subscription state of regular feed.
 */
public class FeedSubscrReg extends FeedSubscr {
	
	public FeedSubscrReg() {
		super();
	}
	
	public FeedSubscrReg(FeedSubscrStatus status) {
		super(status);
	}

	@Override
	public void switchTo(FeedSubscrStatus new_status) throws IllegalStateException, IllegalArgumentException {
		switch ( status ) {
		case NOT_SUBSCR:
			if ( new_status != FeedSubscrStatus.PENDING_SUBSCR ) {
				throwCannotSwitchTo(new_status);
			}
			break;
		case SUBSCR:
			if ( new_status != FeedSubscrStatus.PENDING_UNSUBSCR ) {
				throwCannotSwitchTo(new_status);
			}
			break;
		case PENDING_SUBSCR:
		case PENDING_UNSUBSCR:
			if ( new_status != FeedSubscrStatus.NOT_SUBSCR
			  && new_status != FeedSubscrStatus.SUBSCR )
			{
				throwCannotSwitchTo(new_status);
			}
			break;
		default:
			throw new IllegalArgumentException("Unsupported target status: " + new_status);		
		}
		status = new_status;
	}		

}

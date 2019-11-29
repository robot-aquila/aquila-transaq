package ru.prolib.aquila.transaq.engine.sds;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.transaq.entity.SecurityBoardParams;
import ru.prolib.aquila.transaq.entity.SecurityParams;
import ru.prolib.aquila.transaq.impl.TQSecID2;

public class SymbolStateHandler {
	
	public SymbolStateHandler(Symbol aq_symbol, TQSecID2 tq_symbol) {
		
	}
	
	public Symbol getSymbol() {
		return null;
	}
	
	public TQSecID2 getSecID2() {
		return null;
	}
	
	public boolean isMarkedAsNotFound() {
		return false;
	}
	
	public void markAsFound() {
		
	}
	
	public void markAsNotFound() {
		
	}
	
	public boolean markToSubscribe(FeedID feed_id) {
		return false;
	}
	
	public boolean markToUnsubscribe(FeedID feed_id) {
		return false;
	}
	
	public FeedSubscrStatus getFeedState(FeedID feed_id) {
		return null;
	}
	
	public void setFeedState(FeedID feed_id, FeedSubscrStatus new_status) {
		
	}
	
	public FeedHandler getFeedHandler(FeedID feed_id) {
		return null;
	}
	
	@Deprecated
	public void setRelatedObjects(
			SecurityBoardParams sbp,
			SecurityParams sp,
			EditableSecurity security)
	{
		throw new UnsupportedOperationException("Do not use this way");
	}

}

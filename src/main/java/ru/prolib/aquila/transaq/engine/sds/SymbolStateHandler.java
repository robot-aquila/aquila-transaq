package ru.prolib.aquila.transaq.engine.sds;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.transaq.remote.ISecIDT;

public interface SymbolStateHandler {
	
	Symbol getSymbol();
	ISecIDT getSecIDT();
	boolean isMarkedAsNotFound();
	void markAsFound();
	void markAsNotFound();
	boolean markToSubscribe(FeedID feed_id);
	boolean markToUnsubscribe(FeedID feed_id);
	FeedSubscrStatus getFeedState(FeedID feed_id);
	void setFeedState(FeedID feed_id, FeedSubscrStatus new_status);

}

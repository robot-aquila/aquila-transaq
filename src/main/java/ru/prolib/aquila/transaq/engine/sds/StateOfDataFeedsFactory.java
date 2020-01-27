package ru.prolib.aquila.transaq.engine.sds;

import ru.prolib.aquila.data.DFGroupFactory;
import ru.prolib.aquila.transaq.remote.ISecIDT;

public class StateOfDataFeedsFactory implements DFGroupFactory<ISecIDT, FeedID> {
	
	@Override
	public StateOfDataFeeds produce(ISecIDT idt) {
		return new StateOfDataFeeds(idt);
	}

}

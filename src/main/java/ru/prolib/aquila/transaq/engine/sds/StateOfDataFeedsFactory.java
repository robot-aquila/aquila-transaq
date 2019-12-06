package ru.prolib.aquila.transaq.engine.sds;

import ru.prolib.aquila.transaq.remote.ISecIDT;

public class StateOfDataFeedsFactory {
	
	public StateOfDataFeeds produce(ISecIDT idt) {
		return new StateOfDataFeeds(idt);
	}

}

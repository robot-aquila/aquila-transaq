package ru.prolib.aquila.transaq.entity;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCFactory;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;

public class MarketFactory implements OSCFactory<Integer, Market> {
	private final EventQueue queue;
	
	public MarketFactory(EventQueue queue) {
		this.queue = queue;
	}

	@Override
	public Market produce(OSCRepository<Integer, Market> owner, Integer key) {
		return new Market(new OSCParamsBuilder(queue)
				.withID("Market#" + key)
				.buildParams()
			);
	}

}

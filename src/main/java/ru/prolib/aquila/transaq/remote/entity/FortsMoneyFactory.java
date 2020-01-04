package ru.prolib.aquila.transaq.remote.entity;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCFactory;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.transaq.remote.ID;

public class FortsMoneyFactory implements OSCFactory<ID.FM, FortsMoney> {
	private final EventQueue queue;
	
	public FortsMoneyFactory(EventQueue queue) {
		this.queue = queue;
	}

	@Override
	public FortsMoney produce(OSCRepository<ID.FM, FortsMoney> owner, ID.FM key) {
		return new FortsMoney(new OSCParamsBuilder(queue)
				.withID("FortsMoney#" + key)
				.buildParams()
			);
	}

}

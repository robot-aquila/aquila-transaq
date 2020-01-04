package ru.prolib.aquila.transaq.remote.entity;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCFactory;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.transaq.remote.ID;

public class FortsPositionFactory implements OSCFactory<ID.FP, FortsPosition> {
	private final EventQueue queue;
	
	public FortsPositionFactory(EventQueue queue) {
		this.queue = queue;
	}

	@Override
	public FortsPosition produce(OSCRepository<ID.FP, FortsPosition> owner, ID.FP key) {
		return new FortsPosition(new OSCParamsBuilder(queue)
				.withID("FortsPosition#" + key)
				.buildParams()
			);
	}

}

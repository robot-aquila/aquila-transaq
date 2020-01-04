package ru.prolib.aquila.transaq.remote.entity;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCFactory;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.transaq.remote.ID;

public class SpotLimitsFactory implements OSCFactory<ID.SL, SpotLimits> {
	private final EventQueue queue;
	
	public SpotLimitsFactory(EventQueue queue) {
		this.queue = queue;
	}

	@Override
	public SpotLimits produce(OSCRepository<ID.SL, SpotLimits> owner, ID.SL key) {
		return new SpotLimits(new OSCParamsBuilder(queue)
				.withID("SpotLimits#" + key)
				.buildParams()
			);
	}

}

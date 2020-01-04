package ru.prolib.aquila.transaq.remote.entity;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCFactory;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.transaq.remote.ID;

public class UnitedLimitsFactory implements OSCFactory<ID.UL, UnitedLimits> {
	private final EventQueue queue;
	
	public UnitedLimitsFactory(EventQueue queue) {
		this.queue = queue;
	}

	@Override
	public UnitedLimits produce(OSCRepository<ID.UL, UnitedLimits> owner, ID.UL key) {
		return new UnitedLimits(new OSCParamsBuilder(queue)
				.withID("UnitedLimits#" + key)
				.buildParams()
			);
	}

}

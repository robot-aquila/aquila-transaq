package ru.prolib.aquila.transaq.remote.entity;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCFactory;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.transaq.remote.ID;
import ru.prolib.aquila.transaq.remote.ID.SP;

public class SecPositionFactory implements OSCFactory<ID.SP, SecPosition> {
	private final EventQueue queue;
	
	public SecPositionFactory(EventQueue queue) {
		this.queue = queue;
	}

	@Override
	public SecPosition produce(OSCRepository<SP, SecPosition> owner, SP key) {
		return new SecPosition(new OSCParamsBuilder(queue)
				.withID("SecPosition#" + key)
				.buildParams()
			);
	}

}

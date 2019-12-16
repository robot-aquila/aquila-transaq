package ru.prolib.aquila.transaq.entity;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCFactory;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.transaq.engine.sds.GSymbol;

public class SecurityParamsFactory implements OSCFactory<GSymbol, SecurityParams> {
	private final EventQueue queue;
	
	public SecurityParamsFactory(EventQueue queue) {
		this.queue = queue;
	}
	
	@Override
	public SecurityParams produce(OSCRepository<GSymbol, SecurityParams> owner, GSymbol key) {
		return new SecurityParams(new OSCParamsBuilder(queue)
				.withID("SecurityParams#" + key)
				.buildParams()
			);
	}

}

package ru.prolib.aquila.transaq.entity;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCFactory;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.transaq.engine.sds.SymbolTID;

public class SecurityQuotationsFactory implements OSCFactory<SymbolTID, SecurityQuotations> {
	private final EventQueue queue;
	
	public SecurityQuotationsFactory(EventQueue queue) {
		this.queue = queue;
	}

	@Override
	public SecurityQuotations produce(OSCRepository<SymbolTID, SecurityQuotations> owner, SymbolTID key) {
		return new SecurityQuotations(new OSCParamsBuilder(queue)
				.withID("SecurityQuotations#" + key)
				.buildParams()
			);
	}

}

package ru.prolib.aquila.transaq.remote.entity;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCFactory;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.transaq.remote.ID;
import ru.prolib.aquila.transaq.remote.ID.MP;

public class MoneyPositionFactory implements OSCFactory<ID.MP, MoneyPosition> {
	private final EventQueue queue;
	
	public MoneyPositionFactory(EventQueue queue) {
		this.queue = queue;
	}

	@Override
	public MoneyPosition produce(OSCRepository<MP, MoneyPosition> owner, MP key) {
		return new MoneyPosition(new OSCParamsBuilder(queue)
				.withID("MoneyPosition#" + key)
				.buildParams()
			);
	}

}

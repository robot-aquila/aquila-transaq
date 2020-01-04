package ru.prolib.aquila.transaq.remote.entity;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCFactory;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.transaq.remote.ID;

public class FortsCollateralsFactory implements OSCFactory<ID.FC, FortsCollaterals> {
	private final EventQueue queue;
	
	public FortsCollateralsFactory(EventQueue queue) {
		this.queue = queue;
	}

	@Override
	public FortsCollaterals produce(OSCRepository<ID.FC, FortsCollaterals> owner, ID.FC key) {
		return new FortsCollaterals(new OSCParamsBuilder(queue)
				.withID("FortsCollaterals#" + key)
				.buildParams()
			);
	}

}

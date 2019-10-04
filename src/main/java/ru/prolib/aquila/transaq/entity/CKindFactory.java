package ru.prolib.aquila.transaq.entity;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCFactory;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;

public class CKindFactory implements OSCFactory<Integer, CKind> {
	private final EventQueue queue;
	
	public CKindFactory(EventQueue queue) {
		this.queue = queue;
	}

	@Override
	public CKind produce(OSCRepository<Integer, CKind> owner, Integer key) {
		return new CKind(new OSCParamsBuilder(queue)
				.withID("CKind#" + key)
				.buildParams()
			);
	}

}

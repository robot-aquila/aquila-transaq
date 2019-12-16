package ru.prolib.aquila.transaq.entity;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCFactory;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.transaq.engine.sds.TSymbol;

public class SecurityBoardParamsFactory implements OSCFactory<TSymbol, SecurityBoardParams> {
	private final EventQueue queue;
	
	public SecurityBoardParamsFactory(EventQueue queue) {
		this.queue = queue;
	}

	@Override
	public SecurityBoardParams produce(OSCRepository<TSymbol, SecurityBoardParams> owner, TSymbol key) {
		return new SecurityBoardParams(new OSCParamsBuilder(queue)
				.withID("SecurityBoardParams#" + key)
				.buildParams()
			);
	}

}

package ru.prolib.aquila.transaq.entity;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCFactory;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;

public class BoardFactory implements OSCFactory<String, Board> {
	private final EventQueue queue;
	
	public BoardFactory(EventQueue queue) {
		this.queue = queue;
	}

	@Override
	public Board produce(OSCRepository<String, Board> owner, String key) {
		return new Board(new OSCParamsBuilder(queue)
				.withID("Board#" + key)
				.buildParams()
			);
	}

}

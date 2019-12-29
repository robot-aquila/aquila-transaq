package ru.prolib.aquila.transaq.remote.entity;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCFactory;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParamsBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;

public class ClientFactory implements OSCFactory<String, Client> {
	private final EventQueue queue;
	
	public ClientFactory(EventQueue queue) {
		this.queue = queue;
	}
	
	@Override
	public Client produce(OSCRepository<String, Client> owner, String key) {
		return new Client(new OSCParamsBuilder(queue)
				.withID("Client#" + key)
				.buildParams()
			);
	}

}

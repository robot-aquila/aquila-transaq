package ru.prolib.aquila.transaq.impl;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainerImpl;
import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.remote.ISecIDF;

@Deprecated
public class TQSecurityHandlerFactory {
	private final ServiceLocator services;
	
	public TQSecurityHandlerFactory(ServiceLocator services) {
		this.services = services;
	}

	public TQSecurityHandler createHandler(ISecIDF sec_id) {
		Symbol symbol = services.getDirectory().toSymbol(sec_id);
		return new TQSecurityHandler(
				sec_id,
				symbol,
				services.getTerminal().getEditableSecurity(symbol),
				new UpdatableStateContainerImpl("TQ-SEC-" + symbol),
				services.getAssembler()
			);
	}

}

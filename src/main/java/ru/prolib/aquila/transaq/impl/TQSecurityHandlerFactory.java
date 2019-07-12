package ru.prolib.aquila.transaq.impl;

import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainerImpl;

public class TQSecurityHandlerFactory {
	private final EditableTerminal terminal;
	private final TQFieldAssembler assembler;
	
	public TQSecurityHandlerFactory(EditableTerminal terminal, TQFieldAssembler assembler) {
		this.terminal = terminal;
		this.assembler = assembler;
	}

	public TQSecurityHandler createHandler(TQSecID_F sec_id) {
		Symbol symbol = assembler.toSymbol(sec_id);
		return new TQSecurityHandler(
				sec_id,
				symbol,
				terminal.getEditableSecurity(symbol),
				new UpdatableStateContainerImpl("TQ-SEC-" + symbol),
				assembler
			);
	}

}

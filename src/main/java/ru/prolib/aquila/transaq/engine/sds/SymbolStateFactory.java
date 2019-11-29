package ru.prolib.aquila.transaq.engine.sds;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.entity.SecurityBoardParams;
import ru.prolib.aquila.transaq.entity.SecurityParams;
import ru.prolib.aquila.transaq.impl.TQDirectory;
import ru.prolib.aquila.transaq.impl.TQSecID1;
import ru.prolib.aquila.transaq.impl.TQSecID2;

public class SymbolStateFactory {
	private final ServiceLocator services;
	
	public SymbolStateFactory(ServiceLocator services) {
		this.services = services;
	}
	
	public SymbolStateHandler produce(Symbol symbol) {
		TQSecID2 sec_id2 = services.getAssembler().toSecID2(symbol);
		SymbolStateHandler symbol_state = new SymbolStateHandler(symbol, sec_id2);
		TQDirectory dir = services.getDirectory();
		if ( ! dir.isExistsSecurityBoardParams(sec_id2) ) {
			symbol_state.markAsNotFound();
			return symbol_state;
		}
		SecurityBoardParams sbp = dir.getSecurityBoardParams(sec_id2);
		TQSecID1 sec_id1 = sbp.toSecID1();
		if ( ! dir.isExistsSecurityParams(sec_id1) ) {
			symbol_state.markAsNotFound();
			return symbol_state;
		}
		SecurityParams sp = dir.getSecurityParams(sec_id1);
		EditableSecurity security = services.getTerminal().getEditableSecurity(symbol);
		symbol_state.setRelatedObjects(sbp, sp, security); // TODO: replace to handler
		symbol_state.markAsFound();
		return symbol_state;
	}

}

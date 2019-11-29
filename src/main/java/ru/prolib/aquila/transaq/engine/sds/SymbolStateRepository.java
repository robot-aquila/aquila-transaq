package ru.prolib.aquila.transaq.engine.sds;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.transaq.impl.TQSecID2;

public class SymbolStateRepository {
	private final Map<Symbol, SymbolStateHandler> stateBySymbol;
	private final Map<TQSecID2, SymbolStateHandler> stateBySecID;
	
	public SymbolStateRepository(
			Map<Symbol, SymbolStateHandler> stateBySymbol,
			Map<TQSecID2, SymbolStateHandler> stateBySecID)
	{
		this.stateBySymbol = stateBySymbol;
		this.stateBySecID = stateBySecID;
	}
	
	public SymbolStateRepository() {
		this(new HashMap<>(), new HashMap<>());
	}
	
	public SymbolStateHandler getBySymbol(Symbol symbol) {
		return null;
	}
	
	public SymbolStateHandler getBySecID(TQSecID2 sec_id) {
		return null;
	}
	
	public void register(SymbolStateHandler symbol_state) {
		
	}
	
	public Collection<SymbolStateHandler> getAll() {
		return null;
	}

}

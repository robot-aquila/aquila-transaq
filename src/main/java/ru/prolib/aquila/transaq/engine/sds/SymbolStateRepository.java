package ru.prolib.aquila.transaq.engine.sds;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.transaq.remote.ISecIDT;
import ru.prolib.aquila.transaq.remote.TQSecIDT;

public class SymbolStateRepository {
	private final Map<Symbol, StateOfDataFeeds> stateBySymbol;
	private final Map<TQSecIDT, StateOfDataFeeds> stateBySecID;
	
	public SymbolStateRepository(
			Map<Symbol, StateOfDataFeeds> stateBySymbol,
			Map<TQSecIDT, StateOfDataFeeds> stateBySecID)
	{
		this.stateBySymbol = stateBySymbol;
		this.stateBySecID = stateBySecID;
	}
	
	public SymbolStateRepository() {
		this(new HashMap<>(), new HashMap<>());
	}
	
	public StateOfDataFeeds getBySymbol(Symbol symbol) {
		return null;
	}
	
	public StateOfDataFeeds getBySecIDT(ISecIDT sec_id) {
		return null;
	}
	
	public void register(StateOfDataFeeds symbol_state) {
		
	}
	
	public Collection<StateOfDataFeeds> getAll() {
		return null;
	}

}

package ru.prolib.aquila.transaq.impl;

import java.util.HashMap;
import java.util.Map;

import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
import ru.prolib.aquila.transaq.entity.SecType;

public class TQFieldAssemblerImpl implements TQFieldAssembler {
	private static final Map<SecType, SymbolType> TYPE_MAP;
	
	static {
		TYPE_MAP = new HashMap<>();
		TYPE_MAP.put(SecType.BOND, SymbolType.BOND);
		TYPE_MAP.put(SecType.CURRENCY, SymbolType.CURRENCY);
		TYPE_MAP.put(SecType.ETS_CURRENCY, SymbolType.CURRENCY);
		TYPE_MAP.put(SecType.FOB, SymbolType.FUTURES);
		TYPE_MAP.put(SecType.FUT, SymbolType.FUTURES);
		TYPE_MAP.put(SecType.GKO, SymbolType.BOND);
		TYPE_MAP.put(SecType.OPT, SymbolType.OPTION);
		TYPE_MAP.put(SecType.SHARE, SymbolType.STOCK);
	}
	
	private final TQDirectory dir;
	
	public TQFieldAssemblerImpl(TQDirectory directory) {
		this.dir = directory;
	}

	@Override
	public Symbol toSymbol(TQSecID_F id) {
		SymbolType type = TYPE_MAP.get(id.getType());
		if ( type == null ) {
			type =  SymbolType.UNKNOWN;
		}
		String secCode = id.getSecCode();
		if ( id.getType() == SecType.FUT ) {
			secCode = id.getShortName();
		}
		return new Symbol(secCode, dir.getMarketName(id.getMarketID()), CDecimalBD.RUB, type);
	}

}

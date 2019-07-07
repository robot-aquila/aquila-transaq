package ru.prolib.aquila.transaq.impl;

import java.util.Map;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class TQFieldAssemblerImpl implements TQFieldAssembler {
	//private final TQDirectory dir;
	
	

	@Override
	public Symbol determineSymbol(DeltaUpdate initial_update) {
		Map<Integer, Object> x = initial_update.getContents();
		x.get(TQSecField.MARKETID);
		x.get(TQSecField.SECCODE);
		x.get(TQSecField.SHORT_NAME);
		x.get(TQSecField.SECTYPE);
		
		
		// TODO Auto-generated method stub
		return null;
	}

}

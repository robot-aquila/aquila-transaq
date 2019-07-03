package ru.prolib.aquila.transaq.impl;

import java.util.Map;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.transaq.entity.Board;
import ru.prolib.aquila.transaq.entity.CandleKind;
import ru.prolib.aquila.transaq.entity.Market;

public class UpdateReceiver implements IUpdateReceiver {

	@Override
	public void updateMarket(Market entry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateBoard(Board entry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateCandleKind(CandleKind entry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateSecurity(TQSecurityUpdate1 entry) {
		Map<Integer, Object> data = entry.getUpdate().getContents();
		// не сработает. придется как-то определять какой это апдейт,
		// что то бы определить набор доступных полей
		// лучше сразу делать отдельными методами.
		// Кстати, на security тоже может быть апдейт. Не факт что это
		// первый запрос.
		TQSecID1 sid = entry.getSecID();
		
		
		
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateSecurity(TQSecurityUpdate3 entry) {
		// TODO Auto-generated method stub
		
	}

}

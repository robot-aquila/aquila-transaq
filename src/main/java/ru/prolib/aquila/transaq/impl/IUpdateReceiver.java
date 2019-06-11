package ru.prolib.aquila.transaq.impl;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.transaq.entity.Board;
import ru.prolib.aquila.transaq.entity.CandleKind;
import ru.prolib.aquila.transaq.entity.Market;

public interface IUpdateReceiver {
	void updateMarket(Market entry);
	void updateBoard(Board entry);
	void updateCandleKind(CandleKind entry);
	void updateSecurity(DeltaUpdate entry);
	void updateSecInfo(DeltaUpdate entry);
	void updateSecInfoUpd(DeltaUpdate entry);
}

package ru.prolib.aquila.transaq.impl;

import ru.prolib.aquila.transaq.entity.Board;
import ru.prolib.aquila.transaq.entity.CandleKind;
import ru.prolib.aquila.transaq.entity.Market;
import ru.prolib.aquila.transaq.entity.SecurityUpdate1;

public interface IUpdateReceiver {
	void updateMarket(Market entry);
	void updateBoard(Board entry);
	void updateCandleKind(CandleKind entry);
	void updateSecurity(SecurityUpdate1 entry);
}

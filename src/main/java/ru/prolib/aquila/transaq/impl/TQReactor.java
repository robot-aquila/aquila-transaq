package ru.prolib.aquila.transaq.impl;

import java.util.List;

import ru.prolib.aquila.transaq.entity.Board;
import ru.prolib.aquila.transaq.entity.CandleKind;
import ru.prolib.aquila.transaq.entity.Market;

public interface TQReactor {
	void updateMarkets(List<Market> markets);
	void updateBoards(List<Board> boards);
	void updateCandleKinds(List<CandleKind> candle_kinds);
	void updateSecurity(TQSecurityUpdate1 update);
	void updateSecurity(TQSecurityUpdate3 update);
}

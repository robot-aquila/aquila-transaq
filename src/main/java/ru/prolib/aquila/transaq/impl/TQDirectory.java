package ru.prolib.aquila.transaq.impl;

import java.util.List;

import ru.prolib.aquila.transaq.entity.Board;
import ru.prolib.aquila.transaq.entity.CandleKind;
import ru.prolib.aquila.transaq.entity.Market;

public interface TQDirectory {
	void updateBoards(List<Board> boards);
	void updateMarkets(List<Market> markets);
	void updateCandleKinds(List<CandleKind> candle_kinds);
}

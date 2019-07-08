package ru.prolib.aquila.transaq.impl;

import java.util.ArrayList;
import java.util.List;

import ru.prolib.aquila.transaq.entity.Board;
import ru.prolib.aquila.transaq.entity.CandleKind;
import ru.prolib.aquila.transaq.entity.Market;

public class TQDirectoryImpl implements TQDirectory {
	private final List<Board> listBoards;
	private final List<Market> listMarkets;
	private final List<CandleKind> listCandleKinds;
	
	TQDirectoryImpl(List<Board> list_boards,
					List<Market> list_markets,
					List<CandleKind> list_candle_kinds)
	{
		this.listBoards = list_boards;
		this.listMarkets = list_markets;
		this.listCandleKinds = list_candle_kinds;
	}
	
	public TQDirectoryImpl() {
		this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
	}

	@Override
	public synchronized void updateBoards(List<Board> boards) {
		listBoards.clear();
		listBoards.addAll(boards);
	}

	@Override
	public synchronized void updateMarkets(List<Market> markets) {
		listMarkets.clear();
		listMarkets.addAll(markets);
	}

	@Override
	public synchronized void updateCandleKinds(List<CandleKind> candle_kinds) {
		listCandleKinds.clear();
		listCandleKinds.addAll(candle_kinds);
	}

	@Override
	public synchronized String getMarketName(int market_id) {
		for ( Market market : listMarkets ) {
			if ( market.getID() == market_id ) {
				return market.getName();
			}
		}
		throw new IllegalArgumentException("Market not found: " + market_id);
	}

}

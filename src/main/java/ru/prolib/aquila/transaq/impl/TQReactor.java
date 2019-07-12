package ru.prolib.aquila.transaq.impl;

import java.util.List;
import ru.prolib.aquila.transaq.entity.Board;
import ru.prolib.aquila.transaq.entity.CandleKind;
import ru.prolib.aquila.transaq.entity.Market;

public class TQReactor {
	private final TQDirectory dir;
	private final TQSecurityHandlerRegistry shr;
	private final TQSecurityHandlerFactory shf;
	
	public TQReactor(TQDirectory directory,
						 TQSecurityHandlerRegistry sh_registry,
						 TQSecurityHandlerFactory sh_factory)
	{
		this.dir = directory;
		this.shr = sh_registry;
		this.shf = sh_factory;
	}

	public void updateMarkets(List<Market> markets) {
		dir.updateMarkets(markets);
	}

	public void updateBoards(List<Board> boards) {
		dir.updateBoards(boards);
	}

	public void updateCandleKinds(List<CandleKind> candle_kinds) {
		dir.updateCandleKinds(candle_kinds);
	}

	public void updateSecurity(TQSecurityUpdate1 update) {
		shr.getHandler(update.getSecID()).update(update.getUpdate());
	}

	public void updateSecurity(TQSecurityUpdate3 update) {
		TQSecID_F sec_id = update.getSecID();
		TQSecurityHandler x = shr.getHandlerOrNull(sec_id);
		if ( x == null ) {
			x = shf.createHandler(sec_id);
			x.update(update.getUpdate());
			shr.registerHandler(x);
		} else {
			x.update(update.getUpdate());
		}
	}

}

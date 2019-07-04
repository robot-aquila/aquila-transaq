package ru.prolib.aquila.transaq.impl;

import java.util.List;
import ru.prolib.aquila.transaq.entity.Board;
import ru.prolib.aquila.transaq.entity.CandleKind;
import ru.prolib.aquila.transaq.entity.Market;

public class TQReactorImpl implements TQReactor {
	private final TQDirectory dir;
	private final TQSecurityHandlerRegistry shr;
	private final TQSecurityHandlerFactory shf;
	
	public TQReactorImpl(TQDirectory directory,
						 TQSecurityHandlerRegistry sh_registry,
						 TQSecurityHandlerFactory sh_factory)
	{
		this.dir = directory;
		this.shr = sh_registry;
		this.shf = sh_factory;
	}

	@Override
	public void updateMarkets(List<Market> markets) {
		dir.updateMarkets(markets);
	}

	@Override
	public void updateBoards(List<Board> boards) {
		dir.updateBoards(boards);
	}

	@Override
	public void updateCandleKinds(List<CandleKind> candle_kinds) {
		dir.updateCandleKinds(candle_kinds);
	}

	@Override
	public void updateSecurity(TQSecurityUpdate1 update) {
		shr.getHandler(update.getSecID()).update(update.getUpdate());
	}

	@Override
	public void updateSecurity(TQSecurityUpdate3 update) {
		TQSecID3 sec_id = update.getSecID();
		TQSecurityHandler x = shr.getHandlerOrNull(sec_id);
		if ( x == null ) {
			x = shf.createHandler(sec_id);
			x.initialUpdate(update.getUpdate());
			shr.registerHandler(x);
		} else {
			x.update(update.getUpdate());
		}
	}

}

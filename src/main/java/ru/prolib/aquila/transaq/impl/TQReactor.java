package ru.prolib.aquila.transaq.impl;

import ru.prolib.aquila.transaq.remote.TQSecIDT;
import ru.prolib.aquila.transaq.remote.TQSecIDF;
import ru.prolib.aquila.transaq.remote.TQSecIDG;

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

	public void updateMarket(TQStateUpdate<Integer> update) {
		dir.updateMarket(update);
	}

	public void updateBoard(TQStateUpdate<String> update) {
		dir.updateBoard(update);
	}

	public void updateCandleKind(TQStateUpdate<Integer> update) {
		dir.updateCKind(update);
	}

	public void updateSecurity1(TQStateUpdate<TQSecIDG> update) {
		dir.updateSecurityParamsP(update);
		shr.getHandler(update.getID()).update(update.getUpdate());
	}

	public void updateSecurityF(TQStateUpdate<TQSecIDF> update) {
		TQSecIDF sec_id = update.getID();
		dir.updateSecurityParamsF(update);
		TQSecurityHandler x = shr.getHandlerOrNull(sec_id);
		if ( x == null ) {
			x = shf.createHandler(sec_id);
			x.update(update.getUpdate());
			shr.registerHandler(x);
		} else {
			x.update(update.getUpdate());
		}
	}
	
	public void updateSecurityBoard(TQStateUpdate<TQSecIDT> update) {
		dir.updateSecurityBoardParams(update);
	}

}

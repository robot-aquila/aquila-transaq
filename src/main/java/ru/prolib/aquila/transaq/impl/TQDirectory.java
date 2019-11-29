package ru.prolib.aquila.transaq.impl;

import java.util.Hashtable;
import java.util.Map;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepositoryDecoratorRO;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepositoryImpl;
import ru.prolib.aquila.transaq.engine.sds.SymbolGID;
import ru.prolib.aquila.transaq.engine.sds.SymbolTID;
import ru.prolib.aquila.transaq.entity.Board;
import ru.prolib.aquila.transaq.entity.BoardFactory;
import ru.prolib.aquila.transaq.entity.CKind;
import ru.prolib.aquila.transaq.entity.CKindFactory;
import ru.prolib.aquila.transaq.entity.Market;
import ru.prolib.aquila.transaq.entity.MarketFactory;
import ru.prolib.aquila.transaq.entity.SecurityBoardParams;
import ru.prolib.aquila.transaq.entity.SecurityBoardParamsFactory;
import ru.prolib.aquila.transaq.entity.SecurityParams;
import ru.prolib.aquila.transaq.entity.SecurityParamsFactory;

public class TQDirectory {
	private final OSCRepository<Integer, CKind> ckinds;
	private final OSCRepository<Integer, Market> markets;
	private final OSCRepository<String, Board> boards;
	private final OSCRepository<SymbolGID, SecurityParams> secParams;
	private final OSCRepository<SymbolTID, SecurityBoardParams> secBoardParams;
	private final Map<TQSecID1, SymbolGID> gidMap;
	
	TQDirectory(
			OSCRepository<Integer, CKind> ckinds,
			OSCRepository<Integer, Market> markets,
			OSCRepository<String, Board> boards,
			OSCRepository<SymbolGID, SecurityParams> secParams,
			OSCRepository<SymbolTID, SecurityBoardParams> secBoardParams,
			Map<TQSecID1, SymbolGID> gid_map)
	{
		this.ckinds = ckinds;
		this.markets = markets;
		this.boards = boards;
		this.secParams = secParams;
		this.secBoardParams = secBoardParams;
		this.gidMap = gid_map;
	}
	
	public TQDirectory(EventQueue queue) {
		this(new OSCRepositoryImpl<>(new CKindFactory(queue), "CKINDS"),
			 new OSCRepositoryImpl<>(new MarketFactory(queue), "MARKETS"),
			 new OSCRepositoryImpl<>(new BoardFactory(queue), "BOARDS"),
			 new OSCRepositoryImpl<>(new SecurityParamsFactory(queue), "SEC_PARAMS"),
			 new OSCRepositoryImpl<>(new SecurityBoardParamsFactory(queue), "SEC_BRD_PARAMS"),
			 new Hashtable<>()
		);
	}
	
	public OSCRepository<Integer, CKind> getCKindRepository() {
		return new OSCRepositoryDecoratorRO<>(ckinds);
	}
	
	public OSCRepository<Integer, Market> getMarketRepository() {
		return new OSCRepositoryDecoratorRO<>(markets);
	}
	
	public OSCRepository<String, Board> getBoardRepository() {
		return new OSCRepositoryDecoratorRO<>(boards);
	}
	
	public OSCRepository<SymbolGID, SecurityParams> getSecurityParamsRepository() {
		return new OSCRepositoryDecoratorRO<>(secParams);
	}
	
	public OSCRepository<SymbolTID, SecurityBoardParams> getSecurityBoardParamsRepository() {
		return new OSCRepositoryDecoratorRO<>(secBoardParams);
	}
	
	public void updateCKind(TQStateUpdate<Integer> ckind_update) {
		ckinds.getOrCreate(ckind_update.getID()).consume(ckind_update.getUpdate());
	}
	
	public void updateMarket(TQStateUpdate<Integer> market_update) {
		markets.getOrCreate(market_update.getID()).consume(market_update.getUpdate());
	}

	public void updateBoard(TQStateUpdate<String> board_update) {
		boards.getOrCreate(board_update.getID()).consume(board_update.getUpdate());
	}
	
	private SymbolGID toSymbolGID(TQSecID_F sec_id) {
		switch ( sec_id.getType() ) {
		case FUT:
		case OPT:
			return new SymbolGID(sec_id.getShortName(), sec_id.getMarketID());
		default:
			return new SymbolGID(sec_id.getSecCode(), sec_id.getMarketID());
		}
	}

	private SymbolGID getGIDFromMap(TQSecID1 sec_id, boolean lock, boolean strict) {
		if ( lock ) {
			secParams.lock();
		}
		try {
			SymbolGID gid = gidMap.get(sec_id);
			if ( gid == null && strict ) {
				throw new IllegalStateException("Symbol GID not found: " + sec_id);
			}
			return gid;
		} finally {
			if ( lock ) {
				secParams.unlock();
			}
		}
	}

	private SymbolGID getGIDFromMap(TQSecID1 sec_id, boolean lock) {
		return getGIDFromMap(sec_id, lock, true);
	}

	private TQSecID1 toSecID1(TQSecID2 sec_id) {
		Board board = null;
		try {
			board = boards.getOrThrow(sec_id.getBoardCode());
		} catch ( IllegalArgumentException e ) {
			if ( e.getMessage() != null && e.getMessage().startsWith("Entity not exists: ") ) {
				throw new IllegalStateException("Board not found: " + sec_id.getBoardCode());
			}else {
				throw e;
			}
		}
		return new TQSecID1(sec_id.getSecCode(), board.getMarketID());
	}
	
	private SymbolTID toSymbolTID(TQSecID2 sec_id) {
		TQSecID1 sec_id1 = toSecID1(sec_id);
		SymbolGID gid = getGIDFromMap(sec_id1, true);
		return new SymbolTID(gid.getTicker(), gid.getMarketID(), sec_id.getBoardCode());
	}
	
	public void updateSecurityParamsF(TQStateUpdate<TQSecID_F> sec_params_update) {
		SymbolGID gid = toSymbolGID(sec_params_update.getID());
		secParams.lock();
		try {
			gidMap.put(new TQSecID1(sec_params_update.getID()), gid);
			secParams.getOrCreate(gid).consume(sec_params_update.getUpdate());
		} finally {
			secParams.unlock();
		}
	}
	
	public void updateSecurityParamsP(TQStateUpdate<TQSecID1> sec_params_update) {
		secParams.lock();
		try {
			SymbolGID gid = getGIDFromMap(sec_params_update.getID(), false);
			secParams.getOrCreate(gid).consume(sec_params_update.getUpdate());
		} finally {
			secParams.unlock();
		}
	}
	
	public void updateSecurityBoardParams(TQStateUpdate<TQSecID2> sbp_update) {
		secBoardParams.getOrCreate(toSymbolTID(sbp_update.getID())).consume(sbp_update.getUpdate());
	}

	public String getMarketName(int market_id) {
		return markets.getOrThrow(market_id).getName();
	}
	
	public boolean isExistsSecurityParams(TQSecID1 sec_id) {
		secParams.lock();
		try {
			return gidMap.containsKey(sec_id);
		} finally {
			secParams.unlock();
		}
	}

	public SecurityParams getSecurityParams(TQSecID1 sec_id) {
		secParams.lock();
		try {
			return secParams.getOrThrow(getGIDFromMap(sec_id, false));
		} finally {
			secParams.unlock();
		}
	}
	
	public boolean isExistsSecurityBoardParams(TQSecID2 sec_id) {
		if ( ! boards.contains(sec_id.getBoardCode()) ) {
			return false;
		}
		if ( ! isExistsSecurityParams(toSecID1(sec_id)) ) {
			return false;
		}
		return secBoardParams.contains(toSymbolTID(sec_id));
	}
	
	public SecurityBoardParams getSecurityBoardParams(TQSecID2 sec_id) {
		return secBoardParams.getOrThrow(toSymbolTID(sec_id));
	}

}

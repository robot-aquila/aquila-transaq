package ru.prolib.aquila.transaq.impl;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
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
import ru.prolib.aquila.transaq.entity.SecType;
import ru.prolib.aquila.transaq.entity.SecurityBoardParams;
import ru.prolib.aquila.transaq.entity.SecurityBoardParamsFactory;
import ru.prolib.aquila.transaq.entity.SecurityParams;
import ru.prolib.aquila.transaq.entity.SecurityParamsFactory;
import ru.prolib.aquila.transaq.entity.SecurityQuotations;
import ru.prolib.aquila.transaq.entity.SecurityQuotationsFactory;
import ru.prolib.aquila.transaq.remote.ISecIDF;
import ru.prolib.aquila.transaq.remote.ISecIDG;
import ru.prolib.aquila.transaq.remote.ISecIDT;
import ru.prolib.aquila.transaq.remote.TQSecIDG;
import ru.prolib.aquila.transaq.remote.TQSecIDT;

public class TQDirectory {
	private static final Map<SecType, SymbolType> TYPE_MAP;
	
	static {
		TYPE_MAP = new HashMap<>();
		TYPE_MAP.put(SecType.BOND, SymbolType.BOND);
		TYPE_MAP.put(SecType.CURRENCY, SymbolType.CURRENCY);
		TYPE_MAP.put(SecType.ETS_CURRENCY, SymbolType.CURRENCY);
		TYPE_MAP.put(SecType.FOB, SymbolType.FUTURES);
		TYPE_MAP.put(SecType.FUT, SymbolType.FUTURES);
		TYPE_MAP.put(SecType.GKO, SymbolType.BOND);
		TYPE_MAP.put(SecType.OPT, SymbolType.OPTION);
		TYPE_MAP.put(SecType.SHARE, SymbolType.STOCK);
	}

	private final OSCRepository<Integer, CKind> ckinds;
	private final OSCRepository<Integer, Market> markets;
	private final OSCRepository<String, Board> boards;
	private final OSCRepository<SymbolGID, SecurityParams> secParams;
	private final OSCRepository<SymbolTID, SecurityBoardParams> secBoardParams;
	private final OSCRepository<SymbolTID, SecurityQuotations> secQuots;
	
	/**
	 * Map TRANSAQ/MOEX general security identifier to local symbol general identifier.
	 * Due to possible duplicate of TRANSAQ/MOEX security code across different securities
	 * there is only one mapping in the map - the last, the actual, recently update.
	 */
	private final Map<ISecIDG, SymbolGID> tq2gidMap;
	
	/**
	 * Map local symbol general identifier to TRANSAQ/MOEX security identifier.
	 * Very important map because it's only possibility to get full TRANSAQ/MOEX identifier
	 * of security associated with any known local symbol general identifier. For example
	 * if there are two identifiers known locally RTS-12.19 and RTS-12.09 both point to
	 * RIZ9@FUT. The reverse map contains mapping RIZ9@FUT to RTS-12.19 cuz it was updated
	 * recently. So it is unable to restore link back from RTS-12.09 to RIZ9@FUT without
	 * this map. Using those two maps we can answer which one local symbol is actual and
	 * what TRANSAQ/MOEX identifier was associated with outdated symbols.
	 */
	private final Map<SymbolGID, ISecIDF> gid2tqMap;
	
	TQDirectory(
			OSCRepository<Integer, CKind> ckinds,
			OSCRepository<Integer, Market> markets,
			OSCRepository<String, Board> boards,
			OSCRepository<SymbolGID, SecurityParams> secParams,
			OSCRepository<SymbolTID, SecurityBoardParams> secBoardParams,
			OSCRepository<SymbolTID, SecurityQuotations> secQuotations,
			Map<ISecIDG, SymbolGID> tq2gid_map,
			Map<SymbolGID, ISecIDF> gid2tq_map)
	{
		this.ckinds = ckinds;
		this.markets = markets;
		this.boards = boards;
		this.secParams = secParams;
		this.secBoardParams = secBoardParams;
		this.secQuots = secQuotations;
		this.tq2gidMap = tq2gid_map;
		this.gid2tqMap = gid2tq_map;
	}
	
	public TQDirectory(EventQueue queue) {
		this(new OSCRepositoryImpl<>(new CKindFactory(queue), "CKINDS"),
			 new OSCRepositoryImpl<>(new MarketFactory(queue), "MARKETS"),
			 new OSCRepositoryImpl<>(new BoardFactory(queue), "BOARDS"),
			 new OSCRepositoryImpl<>(new SecurityParamsFactory(queue), "SEC_PARAMS"),
			 new OSCRepositoryImpl<>(new SecurityBoardParamsFactory(queue), "SEC_BRD_PARAMS"),
			 new OSCRepositoryImpl<>(new SecurityQuotationsFactory(queue), "SEC_QUOTATIONS"),
			 new Hashtable<>(),
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
	
	public OSCRepository<SymbolTID, SecurityQuotations> getSecurityQuotationsRepository() {
		return new OSCRepositoryDecoratorRO<>(secQuots);
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
	
	private SymbolGID toSymbolGID(ISecIDF sec_id) {
		switch ( sec_id.getType() ) {
		case FUT:
		case OPT:
			return new SymbolGID(sec_id.getShortName(), sec_id.getMarketID());
		default:
			return new SymbolGID(sec_id.getSecCode(), sec_id.getMarketID());
		}
	}

	private SymbolGID getSymbolGIDFromMap(ISecIDG sec_id, boolean lock, boolean strict) {
		if ( lock ) {
			secParams.lock();
		}
		try {
			SymbolGID gid = tq2gidMap.get(sec_id);
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

	private SymbolGID getSymbolGIDFromMap(ISecIDG sec_id, boolean lock) {
		return getSymbolGIDFromMap(sec_id, lock, true);
	}
	
	private ISecIDF getSecIDFFromMap(SymbolGID gid, boolean lock, boolean strict) {
		if  (lock ) {
			secParams.lock();
		}
		try {
			ISecIDF sec_id = gid2tqMap.get(gid);
			if ( sec_id == null && strict ) {
				throw new IllegalStateException("Security IDG not found: " + gid);
			}
			return sec_id;
		} finally {
			if  ( lock ) {
				secParams.unlock();
			}
		}
	}
	
	//private ISecIDF getSecIDFFromMap(SymbolGID gid, boolean lock) {
	//	return getSecIDFFromMap(gid, lock, true);
	//}

	private ISecIDG toSecIDG(ISecIDT sec_id) {
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
		return new TQSecIDG(sec_id.getSecCode(), board.getMarketID());
	}
	
	private SymbolTID toSymbolTID(ISecIDT sec_id) {
		ISecIDG sec_id1 = toSecIDG(sec_id);
		SymbolGID gid = getSymbolGIDFromMap(sec_id1, true);
		return new SymbolTID(gid.getTicker(), gid.getMarketID(), sec_id.getBoardCode());
	}
	
	public void updateSecurityParamsF(TQStateUpdate<ISecIDF> sec_params_update) {
		SymbolGID gid = toSymbolGID(sec_params_update.getID());
		secParams.lock();
		try {
			tq2gidMap.put(new TQSecIDG(sec_params_update.getID()), gid);
			gid2tqMap.put(gid, sec_params_update.getID());
			secParams.getOrCreate(gid).consume(sec_params_update.getUpdate());
		} finally {
			secParams.unlock();
		}
	}
	
	public void updateSecurityParamsP(TQStateUpdate<ISecIDG> sec_params_update) {
		secParams.lock();
		try {
			SymbolGID gid = getSymbolGIDFromMap(sec_params_update.getID(), false);
			secParams.getOrCreate(gid).consume(sec_params_update.getUpdate());
		} finally {
			secParams.unlock();
		}
	}
	
	public void updateSecurityBoardParams(TQStateUpdate<ISecIDT> sbp_update) {
		secBoardParams.getOrCreate(toSymbolTID(sbp_update.getID())).consume(sbp_update.getUpdate());
	}
	
	public void updateSecurityQuotations(TQStateUpdate<ISecIDT> update) {
		secQuots.getOrCreate(toSymbolTID(update.getID())).consume(update.getUpdate());
	}

	public String getMarketName(int market_id) {
		return markets.getOrThrow(market_id).getName();
	}
	
	public boolean isExistsSecurityParams(ISecIDG sec_id) {
		secParams.lock();
		try {
			return tq2gidMap.containsKey(sec_id);
		} finally {
			secParams.unlock();
		}
	}
	
	public boolean isExistsSecurityParams(SymbolGID gid) {
		return secParams.contains(gid);
	}
	
	public boolean isExistsSecurityParams(SymbolTID tid) {
		return isExistsSecurityParams(tid.toGID());
	}

	public SecurityParams getSecurityParams(ISecIDG sec_id) {
		secParams.lock();
		try {
			return secParams.getOrThrow(getSymbolGIDFromMap(sec_id, false));
		} finally {
			secParams.unlock();
		}
	}
	
	public SecurityParams getSecurityParams(SymbolGID gid) {
		return secParams.getOrThrow(gid);
	}
	
	public SecurityParams getSecurityParams(SymbolTID tid) {
		return getSecurityParams(tid.toGID());
	}
	
	public boolean isExistsSecurityBoardParams(ISecIDT sec_id) {
		if ( ! boards.contains(sec_id.getBoardCode()) ) {
			return false;
		}
		if ( ! isExistsSecurityParams(toSecIDG(sec_id)) ) {
			return false;
		}
		return secBoardParams.contains(toSymbolTID(sec_id));
	}
	
	public boolean isExistsSecurityBoardParams(SymbolTID tid) {
		return secBoardParams.contains(tid);
	}
	
	public SecurityBoardParams getSecurityBoardParams(ISecIDT sec_id) {
		return secBoardParams.getOrThrow(toSymbolTID(sec_id));
	}
	
	public SecurityBoardParams getSecurityBoardParams(SymbolTID tid) {
		return secBoardParams.getOrThrow(tid);
	}
	
	public boolean isExistsSecurityQuotations(SymbolTID tid) {
		return secQuots.contains(tid);
	}
	
	public SecurityQuotations getSecurityQuotations(SymbolTID tid) {
		return secQuots.getOrThrow(tid);
	}
	
	private SymbolType toSymbolType(SecType sec_type) {
		SymbolType type = TYPE_MAP.get(sec_type);
		if ( type == null ) {
			type =  SymbolType.UNKNOWN;
		}
		return type;
	}
	
	public Symbol toSymbol(ISecIDF sec_id) {
		return new Symbol(
				toSymbolGID(sec_id).getTicker(),
				sec_id.getDefaultBoard(),
				CDecimalBD.RUB,
				toSymbolType(sec_id.getType())
			);
	}
	
	public Symbol toSymbol(SymbolTID tid) {
		return new Symbol(
				tid.getTicker(),
				tid.getBoard(),
				CDecimalBD.RUB,
				toSymbolType(getSecurityParams(tid.toGID()).getSecType())
			);
	}
	
	public SymbolTID toSymbolTID(Symbol symbol) {
		Board board = boards.getOrThrow(symbol.getExchangeID());
		return new SymbolTID(
				symbol.getCode(),
				board.getMarketID(),
				symbol.getExchangeID()
			);
	}
	
	/**
	 * Convert local symbol to TRANSAQ/MOEX tradeable security identifier.
	 * <p>
	 * @param symbol - local symbol
	 * @param only_if_actual - enable or disable reverse check
	 * @return if <b>only_if_actual</b> is true then it will check reverse conversion
	 * and return TRANSAQ/MOEX identifier only if reverse conversion will give the same
	 * symbol as argument. If <b>only_if_actual</b> is false then it will use cached
	 * data to restore TRANSAQ/MOEX identifier. Any case the result may be null if
	 * there is no data or reverse check gave another symbol.
	 */
	public ISecIDT toSecIDT(Symbol symbol, boolean only_if_actual) {
		SymbolTID tid = toSymbolTID(symbol);
		SymbolGID gid = tid.toGID();
		ISecIDF sec_idf = getSecIDFFromMap(gid, true, false);
		if ( sec_idf == null ) {
			return null;
		}
		ISecIDG sec_idg = new TQSecIDG(sec_idf);
		ISecIDT sec_idt = new TQSecIDT(sec_idg.getSecCode(), tid.getBoard());
		if ( only_if_actual && gid.equals(getSymbolGIDFromMap(sec_idg, true, false)) == false ) {
			return null;
		}
		return sec_idt;
	}

}

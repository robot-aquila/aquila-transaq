package ru.prolib.aquila.transaq.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepositoryDecoratorRO;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepositoryImpl;
import ru.prolib.aquila.transaq.engine.sds.GSymbol;
import ru.prolib.aquila.transaq.engine.sds.TSymbol;
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
import ru.prolib.aquila.transaq.remote.ConnectionStatus;
import ru.prolib.aquila.transaq.remote.ISecIDF;
import ru.prolib.aquila.transaq.remote.ISecIDG;
import ru.prolib.aquila.transaq.remote.ISecIDT;
import ru.prolib.aquila.transaq.remote.TQSecIDG;
import ru.prolib.aquila.transaq.remote.TQSecIDT;
import ru.prolib.aquila.transaq.remote.entity.Client;
import ru.prolib.aquila.transaq.remote.entity.ClientFactory;

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
	private final OSCRepository<GSymbol, SecurityParams> secParams;
	private final OSCRepository<TSymbol, SecurityBoardParams> secBoardParams;
	private final OSCRepository<TSymbol, SecurityQuotations> secQuots;
	private final OSCRepository<String, Client> clients;
	private final ConnectionStatus connectionStatus;
	
	/**
	 * Map TRANSAQ/MOEX general security identifier to local symbol general identifier.
	 * Due to possible duplicate of TRANSAQ/MOEX security code across different securities
	 * there is only one mapping in the map - the last, the actual, recently update.
	 */
	private final Map<ISecIDG, GSymbol> tq2gidMap;
	
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
	private final Map<GSymbol, ISecIDF> gid2tqMap;
	
	TQDirectory(
			OSCRepository<Integer, CKind> ckinds,
			OSCRepository<Integer, Market> markets,
			OSCRepository<String, Board> boards,
			OSCRepository<GSymbol, SecurityParams> secParams,
			OSCRepository<TSymbol, SecurityBoardParams> secBoardParams,
			OSCRepository<TSymbol, SecurityQuotations> secQuotations,
			OSCRepository<String, Client> clients,
			ConnectionStatus connection_status,
			Map<ISecIDG, GSymbol> tq2gid_map,
			Map<GSymbol, ISecIDF> gid2tq_map)
	{
		this.ckinds = ckinds;
		this.markets = markets;
		this.boards = boards;
		this.secParams = secParams;
		this.secBoardParams = secBoardParams;
		this.secQuots = secQuotations;
		this.clients = clients;
		this.connectionStatus = connection_status;
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
			 new OSCRepositoryImpl<>(new ClientFactory(queue), "CLIENTS"),
			 new ConnectionStatus(queue, "CONNECTION_STATUS"),
			 new Hashtable<>(),
			 new Hashtable<>()
		);
	}
	
	private SymbolType toSymbolType(SecType sec_type) {
		SymbolType type = TYPE_MAP.get(sec_type);
		if ( type == null ) {
			type =  SymbolType.UNKNOWN;
		}
		return type;
	}

	private String getMarketCode(ISecIDF sec_id) {
		return markets.getOrThrow(sec_id.getMarketID()).getName();
	}
	
	private String getCurrencyCode(ISecIDF sec_id) {
		return "RUB";
	}
	
	private String getSecCode(ISecIDF sec_id) {
		switch ( sec_id.getType() ) {
		case FUT:
		case OPT:
			return sec_id.getShortName();
		default:
			return sec_id.getSecCode();
		}
	}
	
	private SymbolType getSymbolType(ISecIDF sec_id) {
		return toSymbolType(sec_id.getType());
	}
	
	private GSymbol getSymbolGIDFromMap(ISecIDG sec_id, boolean lock, boolean strict) {
		if ( lock ) {
			secParams.lock();
		}
		try {
			GSymbol gid = tq2gidMap.get(sec_id);
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

	private GSymbol getSymbolGIDFromMap(ISecIDG sec_id, boolean lock) {
		return getSymbolGIDFromMap(sec_id, lock, true);
	}
	
	private ISecIDF getSecIDFFromMap(GSymbol gid, boolean lock, boolean strict) {
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
	
	public GSymbol toSymbolGID(ISecIDG sec_id) {
		return getSymbolGIDFromMap(sec_id, true);
	}
	
	public TSymbol toSymbolTID(ISecIDT sec_id) {
		GSymbol gid = getSymbolGIDFromMap(toSecIDG(sec_id), true);
		return new TSymbol(gid.getCode(), sec_id.getBoardCode(), gid.getCurrencyCode(), gid.getType());
	}
	
	private GSymbol toSymbolGID(TSymbol tid) {
		return new GSymbol(
				tid.getCode(),
				getMarketName(boards.getOrThrow(tid.getBoardCode()).getMarketID()),
				tid.getCurrencyCode(),
				tid.getType()
			);
	}
	
	public ConnectionStatus getConnectionStatus() {
		return connectionStatus;
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
	
	public OSCRepository<GSymbol, SecurityParams> getSecurityParamsRepository() {
		return new OSCRepositoryDecoratorRO<>(secParams);
	}
	
	public OSCRepository<TSymbol, SecurityBoardParams> getSecurityBoardParamsRepository() {
		return new OSCRepositoryDecoratorRO<>(secBoardParams);
	}
	
	public OSCRepository<TSymbol, SecurityQuotations> getSecurityQuotationsRepository() {
		return new OSCRepositoryDecoratorRO<>(secQuots);
	}
	
	public OSCRepository<String, Client> getClientRepository() {
		return new OSCRepositoryDecoratorRO<>(clients);
	}
	
	public void updateConnectionStatus(boolean connected) {
		if ( connected ) {
			connectionStatus.setConnected();
		} else {
			connectionStatus.setDisconnected();
		}
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

	public SecurityParams updateSecurityParamsF(TQStateUpdate<ISecIDF> sec_params_update) {
		GSymbol gid = toSymbolGID(sec_params_update.getID());
		secParams.lock();
		try {
			tq2gidMap.put(new TQSecIDG(sec_params_update.getID()), gid);
			gid2tqMap.put(gid, sec_params_update.getID());
			SecurityParams entity = secParams.getOrCreate(gid);
			entity.consume(sec_params_update.getUpdate());
			return entity;
		} finally {
			secParams.unlock();
		}
	}
	
	public SecurityParams updateSecurityParamsP(TQStateUpdate<ISecIDG> sec_params_update) {
		secParams.lock();
		try {
			GSymbol gid = getSymbolGIDFromMap(sec_params_update.getID(), false);
			SecurityParams entity = secParams.getOrCreate(gid);
			entity.consume(sec_params_update.getUpdate());
			return entity;
		} finally {
			secParams.unlock();
		}
	}
	
	public SecurityBoardParams updateSecurityBoardParams(TQStateUpdate<ISecIDT> sbp_update) {
		SecurityBoardParams entity = secBoardParams.getOrCreate(toSymbolTID(sbp_update.getID()));
		entity.consume(sbp_update.getUpdate());
		return entity;
	}
	
	public SecurityQuotations updateSecurityQuotations(TQStateUpdate<ISecIDT> update) {
		SecurityQuotations entity = secQuots.getOrCreate(toSymbolTID(update.getID()));
		entity.consume(update.getUpdate());
		return entity;
	}
	
	public Client updateClient(TQStateUpdate<String> update) {
		Client entity = clients.getOrCreate(update.getID());
		entity.consume(update.getUpdate());
		return entity;
	}
	
	public boolean isExistsSecurityParams(ISecIDG sec_id) {
		secParams.lock();
		try {
			return tq2gidMap.containsKey(sec_id);
		} finally {
			secParams.unlock();
		}
	}
	
	public boolean isExistsSecurityParams(GSymbol gid) {
		return secParams.contains(gid);
	}
	
	public boolean isExistsSecurityParams(TSymbol tid) {
		return isExistsSecurityParams(toSymbolGID(tid));
	}

	public SecurityParams getSecurityParams(ISecIDG sec_id) {
		secParams.lock();
		try {
			return secParams.getOrThrow(getSymbolGIDFromMap(sec_id, false));
		} finally {
			secParams.unlock();
		}
	}
	
	public SecurityParams getSecurityParams(GSymbol gid) {
		return secParams.getOrThrow(gid);
	}
	
	public SecurityParams getSecurityParams(TSymbol tid) {
		return getSecurityParams(toSymbolGID(tid));
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
	
	public boolean isExistsSecurityBoardParams(TSymbol tid) {
		return secBoardParams.contains(tid);
	}
	
	public SecurityBoardParams getSecurityBoardParams(ISecIDT sec_id) {
		return secBoardParams.getOrThrow(toSymbolTID(sec_id));
	}
	
	public SecurityBoardParams getSecurityBoardParams(TSymbol tid) {
		return secBoardParams.getOrThrow(tid);
	}
	
	public boolean isExistsSecurityQuotations(TSymbol tid) {
		return secQuots.contains(tid);
	}
	
	public SecurityQuotations getSecurityQuotations(TSymbol tid) {
		return secQuots.getOrThrow(tid);
	}

	public String getMarketName(int market_id) {
		return markets.getOrThrow(market_id).getName();
	}
	
	public String getBoardName(String board_code) {
		Board board = boards.getOrThrow(board_code);
		String board_name = board.getName();
		return (board_name == null || board_name.length() == 0) ? board_code : board_name;
	}

	public Symbol toSymbol(TSymbol tid) {
		return new Symbol(
				tid.getCode(),
				tid.getExchangeID(),
				tid.getCurrencyCode(),
				tid.getType()
			);
	}
	
	public GSymbol toSymbolGID(ISecIDF sec_id) {
		return new GSymbol(
				getSecCode(sec_id),
				getMarketCode(sec_id),
				getCurrencyCode(sec_id),
				getSymbolType(sec_id)
			);
	}

	public Symbol toSymbol(ISecIDF sec_id) {
		GSymbol gid = toSymbolGID(sec_id);
		return new Symbol(
				gid.getCode(),
				sec_id.getDefaultBoard(),
				gid.getCurrencyCode(),
				gid.getType()
			);
	}
		
	public TSymbol toSymbolTID(Symbol symbol) {
		return new TSymbol(
				symbol.getCode(),
				symbol.getExchangeID(),
				symbol.getCurrencyCode(),
				symbol.getType()
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
		TSymbol tid = toSymbolTID(symbol);
		String board_code = tid.getExchangeID();
		if ( ! boards.contains(board_code) ) {
			return null;
		}
		GSymbol gid = new GSymbol(
				tid.getCode(),
				getMarketName(boards.getOrThrow(board_code).getMarketID()),
				tid.getCurrencyCode(),
				tid.getType()
			);
		ISecIDF sec_idf = getSecIDFFromMap(gid, true, false);
		if ( sec_idf == null ) {
			return null;
		}
		ISecIDG sec_idg = new TQSecIDG(sec_idf);
		ISecIDT sec_idt = new TQSecIDT(sec_idg.getSecCode(), tid.getExchangeID());
		if ( only_if_actual && gid.equals(getSymbolGIDFromMap(sec_idg, true, false)) == false ) {
			return null;
		}
		return sec_idt;
	}
	
	public List<Symbol> getKnownSymbols(GSymbol gid) {
		List<Symbol> list = new ArrayList<>();
		ISecIDF sec_idf = getSecIDFFromMap(gid, true, false);
		if ( sec_idf != null ) {
			int expected_market_id = sec_idf.getMarketID();
			for ( Board board : boards.getEntities() ) {
				if ( board.getMarketID() == expected_market_id ) {
					TSymbol tid = new TSymbol(gid.getCode(), board.getCode(), gid.getCurrencyCode(), gid.getType());
					if ( secBoardParams.contains(tid) ) {
						list.add(new Symbol(tid.getCode(), tid.getBoardCode(), tid.getCurrencyCode(), tid.getType()));
					}
				}
			}
		}
		return list;
	}
	
	public boolean isKnownSymbol(Symbol symbol) {
		return secBoardParams.contains(toSymbolTID(symbol));
	}

}

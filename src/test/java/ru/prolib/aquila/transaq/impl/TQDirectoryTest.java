package ru.prolib.aquila.transaq.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.osc.*;
import ru.prolib.aquila.transaq.engine.sds.*;
import ru.prolib.aquila.transaq.entity.*;
import ru.prolib.aquila.transaq.remote.*;
import ru.prolib.aquila.transaq.remote.MessageFields.*;
import ru.prolib.aquila.transaq.remote.entity.*;

@SuppressWarnings("rawtypes")
public class TQDirectoryTest {
	private static EventQueue queue;
	
	static List<Integer> toList(Integer... integers) {
		List<Integer> list = new ArrayList<>();
		for ( Integer i : integers ) {
			list.add(i);
		}
		return list;
	}
	
	static Set<Integer> toSet(Integer... integers) {
		return new HashSet<>(toList(integers));
	}

	@BeforeClass
	public static void setUpBeforeClass() {
		queue = new EventQueueFactory().createDefault();
	}
	
	@Rule
	public ExpectedException eex = ExpectedException.none();
	private IMocksControl control;
	private OSCRepository<Integer, CKind> ckindsMock, ckinds;
	private OSCRepository<Integer, Market> marketsMock, markets;
	private OSCRepository<String, Board> boardsMock, boards;
	private OSCRepository<GSymbol, SecurityParams> secParamsMock, secParams;
	private OSCRepository<TSymbol, SecurityBoardParams> secBoardParamsMock, secBoardParams;
	private OSCRepository<TSymbol, SecurityQuotations> secQuotsMock, secQuots;
	private OSCRepository<String, Client> clientsMock, clients;
	private OSCRepository<ID.MP, MoneyPosition> moneyPositionsMock, moneyPositions;
	private OSCRepository<ID.SP, SecPosition> secPositionsMock, secPositions;
	private OSCRepository<ID.FM, FortsMoney> fortsMoneyMock, fortsMoney;
	private OSCRepository<ID.FP, FortsPosition> fortsPositionsMock, fortsPositions;
	private OSCRepository<ID.FC, FortsCollaterals> fortsCollateralsMock, fortsCollaterals;
	private OSCRepository<ID.SL, SpotLimits> spotLimitsMock, spotLimits;
	private OSCRepository<ID.UL, UnitedLimits> unitedLimitsMock, unitedLimits;
	private ConnectionStatus conStatMock, conStat;
	private DeltaUpdate duMock;
	private Map<ISecIDG, GSymbol> tq2gidMap;
	private Map<GSymbol, ISecIDF> gid2tqMap;
	private TQDirectory service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		boardsMock = control.createMock(OSCRepository.class);
		marketsMock = control.createMock(OSCRepository.class);
		ckindsMock = control.createMock(OSCRepository.class);
		secParamsMock = control.createMock(OSCRepository.class);
		secBoardParamsMock = control.createMock(OSCRepository.class);
		secQuotsMock = control.createMock(OSCRepository.class);
		clientsMock = control.createMock(OSCRepository.class);
		moneyPositionsMock = control.createMock(OSCRepository.class);
		secPositionsMock = control.createMock(OSCRepository.class);
		fortsMoneyMock = control.createMock(OSCRepository.class);
		fortsPositionsMock = control.createMock(OSCRepository.class);
		fortsCollateralsMock = control.createMock(OSCRepository.class);
		spotLimitsMock = control.createMock(OSCRepository.class);
		unitedLimitsMock = control.createMock(OSCRepository.class);
		conStatMock = control.createMock(ConnectionStatus.class);
		duMock = control.createMock(DeltaUpdate.class);
		ckinds = new OSCRepositoryImpl<>(new CKindFactory(queue), "XXX");
		markets = new OSCRepositoryImpl<>(new MarketFactory(queue), "XXX");
		boards = new OSCRepositoryImpl<>(new BoardFactory(queue), "XXX");
		secParams = new OSCRepositoryImpl<>(new SecurityParamsFactory(queue), "XXX");
		secBoardParams = new OSCRepositoryImpl<>(new SecurityBoardParamsFactory(queue), "XXX");
		secQuots = new OSCRepositoryImpl<>(new SecurityQuotationsFactory(queue), "XXX");
		clients = new OSCRepositoryImpl<>(new ClientFactory(queue), "XXX");
		moneyPositions = new OSCRepositoryImpl<>(new MoneyPositionFactory(queue), "XXX");
		secPositions = new OSCRepositoryImpl<>(new SecPositionFactory(queue), "XXX");
		fortsMoney = new OSCRepositoryImpl<>(new FortsMoneyFactory(queue), "XXX");
		fortsPositions = new OSCRepositoryImpl<>(new FortsPositionFactory(queue), "XXX");
		fortsCollaterals = new OSCRepositoryImpl<>(new FortsCollateralsFactory(queue), "XXX");
		spotLimits = new OSCRepositoryImpl<>(new SpotLimitsFactory(queue), "XXX");
		unitedLimits = new OSCRepositoryImpl<>(new UnitedLimitsFactory(queue), "XXX");
		conStat = new ConnectionStatus(queue, "XXX");
		tq2gidMap = new LinkedHashMap<>();
		gid2tqMap = new LinkedHashMap<>();
		service = new TQDirectory(
				ckindsMock,
				marketsMock,
				boardsMock,
				secParamsMock,
				secBoardParamsMock,
				secQuotsMock,
				clientsMock,
				moneyPositionsMock,
				secPositionsMock,
				fortsMoneyMock,
				fortsPositionsMock,
				fortsCollateralsMock,
				spotLimitsMock,
				unitedLimitsMock,
				conStatMock,
				tq2gidMap,
				gid2tqMap
			);
	}
	
	@Test
	public void testUpdateCKind() {
		TQStateUpdate<Integer> update = new TQStateUpdate<>(12, duMock);
		CKind ckindMock = control.createMock(CKind.class);
		expect(ckindsMock.getOrCreate(12)).andReturn(ckindMock);
		ckindMock.consume(duMock);
		control.replay();
		
		service.updateCKind(update);
		
		control.verify();
	}
	
	@Test
	public void testUpdateMarket() {
		TQStateUpdate<Integer> update = new TQStateUpdate<>(56, duMock);
		Market marketMock = control.createMock(Market.class);
		expect(marketsMock.getOrCreate(56)).andReturn(marketMock);
		marketMock.consume(duMock);
		control.replay();
		
		service.updateMarket(update);
		
		control.verify();
	}
	
	@Test
	public void testUpdateBoard() {
		TQStateUpdate<String> update = new TQStateUpdate<>("foo", duMock);
		Board boardMock = control.createMock(Board.class);
		expect(boardsMock.getOrCreate("foo")).andReturn(boardMock);
		boardMock.consume(duMock);
		control.replay();
		
		service.updateBoard(update);
		
		control.verify();
	}
	
	@Test
	public void testUpdateSecurityParamsF_CommonType() {
		markets.getOrCreate(1).consume(new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 1)
				.withToken(FMarket.NAME, "MICEX")
				.buildUpdate()
			);
		GSymbol gid = new GSymbol("GAZP", "MICEX", "RUB", SymbolType.STOCK);
		TQSecIDF sec_idf = new TQSecIDF("GAZP", 1, "EQTB", "Gazprom AO", SecType.SHARE);
		service = new TQDirectory(
				ckinds,
				markets,
				boards,
				secParamsMock,
				secBoardParams,
				secQuots,
				clients,
				moneyPositions,
				secPositions,
				fortsMoney,
				fortsPositions,
				fortsCollaterals,
				spotLimits,
				unitedLimits,
				conStat,
				tq2gidMap,
				gid2tqMap);
		SecurityParams entityMock = control.createMock(SecurityParams.class);
		secParamsMock.lock();
		expect(secParamsMock.getOrCreate(gid)).andReturn(entityMock);
		entityMock.consume(duMock);
		secParamsMock.unlock();
		control.replay();
		
		SecurityParams actual = service.updateSecurityParamsF(new TQStateUpdate<>(sec_idf, duMock));

		control.verify();
		assertSame(entityMock, actual);
		assertEquals(gid, tq2gidMap.get(new TQSecIDG("GAZP", 1)));
		assertEquals(sec_idf, gid2tqMap.get(gid));
	}
	
	@Test
	public void testUpdateSecurityParamsF_Fut() {
		markets.getOrCreate(4).consume(new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 4)
				.withToken(FMarket.NAME, "FORTS")
				.buildUpdate()
			);
		GSymbol gid = new GSymbol("RTS-12.19", "FORTS", "RUB", SymbolType.FUTURES);
		TQSecIDF sec_idf = new TQSecIDF("RIZ9", 4, "FUT", "RTS-12.19", SecType.FUT);
		service = new TQDirectory(
				ckinds,
				markets,
				boards,
				secParamsMock,
				secBoardParams,
				secQuots,
				clients,
				moneyPositions,
				secPositions,
				fortsMoney,
				fortsPositions,
				fortsCollaterals,
				spotLimits,
				unitedLimits,
				conStat,
				tq2gidMap,
				gid2tqMap);
		SecurityParams entityMock = control.createMock(SecurityParams.class);
		secParamsMock.lock();
		expect(secParamsMock.getOrCreate(gid)).andReturn(entityMock);
		entityMock.consume(duMock);
		secParamsMock.unlock();
		control.replay();

		SecurityParams actual = service.updateSecurityParamsF(new TQStateUpdate<>(sec_idf, duMock));
		
		control.verify();
		assertSame(entityMock, actual);
		assertEquals(gid, tq2gidMap.get(new TQSecIDG("RIZ9", 4)));
		assertEquals(sec_idf, gid2tqMap.get(gid));
	}

	@Test
	public void testUpdateSecurityParamsF_Opt() {
		markets.getOrCreate(4).consume(new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 4)
				.withToken(FMarket.NAME, "FORTS")
				.buildUpdate()
			);
		GSymbol gid = new GSymbol("Eu-6.19M200619CA63500", "FORTS", "RUB", SymbolType.OPTION);
		TQSecIDF sec_idf = new TQSecIDF("Eu63500BF9", 4, "OPT", "Eu-6.19M200619CA63500", SecType.OPT);
		service = new TQDirectory(
				ckinds,
				markets,
				boards,
				secParamsMock,
				secBoardParams,
				secQuots,
				clients,
				moneyPositions,
				secPositions,
				fortsMoney,
				fortsPositions,
				fortsCollaterals,
				spotLimits,
				unitedLimits,
				conStat,
				tq2gidMap,
				gid2tqMap);
		SecurityParams entityMock = control.createMock(SecurityParams.class);
		secParamsMock.lock();
		expect(secParamsMock.getOrCreate(gid)).andReturn(entityMock);
		entityMock.consume(duMock);
		secParamsMock.unlock();
		control.replay();

		SecurityParams actual = service.updateSecurityParamsF(new TQStateUpdate<>(sec_idf, duMock));
		
		control.verify();
		assertSame(entityMock, actual);
		assertEquals(gid, tq2gidMap.get(new TQSecIDG("Eu63500BF9", 4)));
		assertEquals(sec_idf, gid2tqMap.get(gid));
	}
	
	@Test
	public void testUpdateSecurityParamsP_ThrowsIfNoSymbolGID() {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Symbol GID not found: TQSecIDG[secCode=GAZP,marketID=0]");
		secParamsMock.lock();
		secParamsMock.unlock();
		control.replay();
		
		service.updateSecurityParamsP(new TQStateUpdate<>(new TQSecIDG("GAZP", 0), duMock));
	}

	@Test
	public void testUpdateSecurityParamsP() {
		GSymbol gid = new GSymbol("RTS-12.19", "FORTS", "RUB", SymbolType.FUTURES);
		tq2gidMap.put(new TQSecIDG("RIZ9", 4), gid);
		SecurityParams entityMock = control.createMock(SecurityParams.class);
		secParamsMock.lock();
		expect(secParamsMock.getOrCreate(gid)).andReturn(entityMock);
		entityMock.consume(duMock);
		secParamsMock.unlock();
		control.replay();

		SecurityParams actual = service.updateSecurityParamsP(new TQStateUpdate<>(new TQSecIDG("RIZ9", 4), duMock));
		
		control.verify();
		assertSame(entityMock, actual);
	}
	
	@Test
	public void testUpdateSecurityBoardParams_ThrowsIfNoBoard() {
		service = new TQDirectory(
				ckinds,
				markets,
				boards,
				secParams,
				secBoardParamsMock,
				secQuots,
				clients,
				moneyPositions,
				secPositions,
				fortsMoney,
				fortsPositions,
				fortsCollaterals,
				spotLimits,
				unitedLimits,
				conStat,
				tq2gidMap,
				gid2tqMap);
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Board not found: FUT");
		control.replay();
		
		service.updateSecurityBoardParams(new TQStateUpdate<>(new TQSecIDT("RIZ9", "FUT"), duMock));
	}
	
	@Test
	public void testUpdateSecurityBoardParams_ThrowsIfNoSymbolGID() {
		service = new TQDirectory(
				ckinds,
				markets,
				boards,
				secParams,
				secBoardParamsMock,
				secQuots,
				clients,
				moneyPositions,
				secPositions,
				fortsMoney,
				fortsPositions,
				fortsCollaterals,
				spotLimits,
				unitedLimits,
				conStat,
				tq2gidMap,
				gid2tqMap);
		boards.getOrCreate("FUT").consume(new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "FUT")
				.withToken(FBoard.MARKET_ID, 4)
				.withToken(FBoard.NAME, "FORTS-Futures")
				.withToken(FBoard.TYPE, 0)
				.buildUpdate()
			);
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Symbol GID not found: TQSecIDG[secCode=RIZ9,marketID=4]");
		control.replay();
		
		service.updateSecurityBoardParams(new TQStateUpdate<>(new TQSecIDT("RIZ9", "FUT"), duMock));
	}
	
	@Test
	public void testUpdateSecurityBoardParams() {
		service = new TQDirectory(
				ckinds,
				markets,
				boards,
				secParams,
				secBoardParamsMock,
				secQuots,
				clients,
				moneyPositions,
				secPositions,
				fortsMoney,
				fortsPositions,
				fortsCollaterals,
				spotLimits,
				unitedLimits,
				conStat,
				tq2gidMap,
				gid2tqMap);
		boards.getOrCreate("FUT").consume(new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "FUT")
				.withToken(FBoard.MARKET_ID, 4)
				.withToken(FBoard.NAME, "FORTS-Futures")
				.withToken(FBoard.TYPE, 0)
				.buildUpdate()
			);
		
		GSymbol gid = new GSymbol("RTS-12.19", "FORTS", "RUB", SymbolType.FUTURES);
		TSymbol tid = new TSymbol("RTS-12.19", "FUT", "RUB", SymbolType.FUTURES);
		tq2gidMap.put(new TQSecIDG("RIZ9", 4), gid);
		SecurityBoardParams entityMock = control.createMock(SecurityBoardParams.class);
		expect(secBoardParamsMock.getOrCreate(tid)).andReturn(entityMock);
		entityMock.consume(duMock);
		control.replay();
		
		SecurityBoardParams actual = service.updateSecurityBoardParams(new TQStateUpdate<>(new TQSecIDT("RIZ9", "FUT"), duMock));
		
		control.verify();
		assertSame(entityMock, actual);
	}
	
	@Test
	public void testUpdateSecurityQuotations_ThrowsIfNoBoard() {
		service = new TQDirectory(queue);
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Board not found: FUT");
		control.replay();
		
		service.updateSecurityQuotations(new TQStateUpdate<>(new TQSecIDT("RIZ9", "FUT"), duMock));
	}
	
	@Test
	public void testUpdateSecurityQuotations_ThrowsIfNoSymbolGID() {
		service = new TQDirectory(queue);
		service.updateBoard(new TQStateUpdate<>("FUT", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "FUT")
				.withToken(FBoard.MARKET_ID, 4)
				.withToken(FBoard.NAME, "FORTS-Futures")
				.withToken(FBoard.TYPE, 0)
				.buildUpdate()
			));
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Symbol GID not found: TQSecIDG[secCode=RIZ9,marketID=4]");
		control.replay();
		
		service.updateSecurityQuotations(new TQStateUpdate<>(new TQSecIDT("RIZ9", "FUT"), duMock));
	}
	
	@Test
	public void testUpdateSecurityQuotations() {
		service = new TQDirectory(
				ckinds,
				markets,
				boards,
				secParams,
				secBoardParams,
				secQuotsMock,
				clients,
				moneyPositions,
				secPositions,
				fortsMoney,
				fortsPositions,
				fortsCollaterals,
				spotLimits,
				unitedLimits,
				conStat,
				tq2gidMap,
				gid2tqMap);
		boards.getOrCreate("FUT").consume(new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "FUT")
				.withToken(FBoard.MARKET_ID, 4)
				.withToken(FBoard.NAME, "FORTS-Futures")
				.withToken(FBoard.TYPE, 0)
				.buildUpdate()
			);
		GSymbol gid = new GSymbol("RTS-12.19", "FORTS", "RUB", SymbolType.FUTURES);
		TSymbol tid = new TSymbol("RTS-12.19", "FUT", "RUB", SymbolType.FUTURES);
		tq2gidMap.put(new TQSecIDG("RIZ9", 4), gid);
		SecurityQuotations entityMock = control.createMock(SecurityQuotations.class);
		expect(secQuotsMock.getOrCreate(tid)).andReturn(entityMock);
		entityMock.consume(duMock);
		control.replay();
		
		SecurityQuotations actual = service.updateSecurityQuotations(new TQStateUpdate<>(new TQSecIDT("RIZ9", "FUT"), duMock));
		
		control.verify();
		assertSame(entityMock, actual);
	}
	
	@Test
	public void testUpdateClient() {
		service = new TQDirectory(
				ckinds,
				markets,
				boards,
				secParams,
				secBoardParams,
				secQuots,
				clientsMock,
				moneyPositions,
				secPositions,
				fortsMoney,
				fortsPositions,
				fortsCollaterals,
				spotLimits,
				unitedLimits,
				conStat,
				tq2gidMap,
				gid2tqMap);
		Client entityMock = control.createMock(Client.class);
		expect(clientsMock.getOrCreate("XXX-2495")).andReturn(entityMock);
		entityMock.consume(duMock);
		control.replay();
		
		Client actual = service.updateClient(new TQStateUpdate<>("XXX-2495", duMock));
		
		control.verify();
		assertSame(entityMock, actual);
	}
	
	@Test
	public void testUpdateMoneyPosition() {
		MoneyPosition entityMock = control.createMock(MoneyPosition.class);
		expect(moneyPositionsMock.getOrCreate(new ID.MP("suse", "FUND", "TX"))).andReturn(entityMock);
		entityMock.consume(duMock);
		control.replay();
		
		MoneyPosition actual = service.updateMoneyPosition(new TQStateUpdate<>(new ID.MP("suse", "FUND", "TX"),duMock));
		
		control.verify();
		assertSame(entityMock, actual);
	}
	
	@Test
	public void testUpdateSecPosition() {
		SecPosition entityMock = control.createMock(SecPosition.class);
		expect(secPositionsMock.getOrCreate(new ID.SP("foo", "RIH0", 4, null))).andReturn(entityMock);
		entityMock.consume(duMock);
		control.replay();
		
		SecPosition actual = service.updateSecPosition(new TQStateUpdate<>(new ID.SP("foo", "RIH0", 4, null), duMock));
		
		control.verify();
		assertSame(entityMock, actual);
	}
	
	@Test
	public void testUpdateFortsMoney() {
		FortsMoney entityMock = control.createMock(FortsMoney.class);
		expect(fortsMoneyMock.getOrCreate(new ID.FM("zuzu25"))).andReturn(entityMock);
		entityMock.consume(duMock);
		control.replay();
		
		FortsMoney actual = service.updateFortsMoney(new TQStateUpdate<>(new ID.FM("zuzu25"), duMock));
		
		control.verify();
		assertSame(entityMock, actual);
	}
	
	@Test
	public void testUpdateFortsPosition() {
		FortsPosition entityMock = control.createMock(FortsPosition.class);
		expect(fortsPositionsMock.getOrCreate(new ID.FP("zoo", "RIH0", toSet(4, 7)))).andReturn(entityMock);
		entityMock.consume(duMock);
		control.replay();
		
		FortsPosition actual = service.updateFortsPosition(new TQStateUpdate<>(new ID.FP("zoo", "RIH0", toSet(4, 7)), duMock));
		
		control.verify();
		assertSame(entityMock, actual);
	}
	
	@Test
	public void testUpdateFortsCollaterals() {
		FortsCollaterals entityMock = control.createMock(FortsCollaterals.class);
		expect(fortsCollateralsMock.getOrCreate(new ID.FC("cookie", toSet(4)))).andReturn(entityMock);
		entityMock.consume(duMock);
		control.replay();
		
		FortsCollaterals actual = service.updateFortsCollaterals(new TQStateUpdate<>(new ID.FC("cookie", toSet(4)), duMock));
		
		control.verify();
		assertSame(entityMock, actual);
	}
	
	@Test
	public void testUpdateSpotLimits() {
		SpotLimits entityMock = control.createMock(SpotLimits.class);
		expect(spotLimitsMock.getOrCreate(new ID.SL("foo", toSet(14, 15)))).andReturn(entityMock);
		entityMock.consume(duMock);
		control.replay();
		
		SpotLimits actual = service.updateSpotLimits(new TQStateUpdate<>(new ID.SL("foo", toSet(14, 15)), duMock));
		
		control.verify();
		assertSame(entityMock, actual);
	}
	
	@Test
	public void testUpdateUnitedLimits() {
		UnitedLimits entityMock = control.createMock(UnitedLimits.class);
		expect(unitedLimitsMock.getOrCreate(new ID.UL("gabba"))).andReturn(entityMock);
		entityMock.consume(duMock);
		control.replay();
		
		UnitedLimits actual = service.updateUnitedLimits(new TQStateUpdate<>(new ID.UL("gabba"), duMock));
		
		control.verify();
		assertSame(entityMock, actual);
	}
	
	@Test
	public void testGetMarketName() {
		Market marketMock = control.createMock(Market.class);
		expect(marketsMock.getOrThrow(71)).andReturn(marketMock);
		expect(marketMock.getName()).andReturn("zulu24");
		control.replay();
		
		assertEquals("zulu24", service.getMarketName(71));
		
		control.verify();
	}
	
	@Test
	public void testGetCKindRepository() {
		OSCRepository<Integer, CKind> actual = service.getCKindRepository();
		
		assertNotNull(actual);
		assertNotSame(ckindsMock, actual);
		assertEquals(OSCRepositoryDecoratorRO.class, actual.getClass());
		assertSame(ckindsMock, ((OSCRepositoryDecoratorRO) actual).getDecoratedRepository());
	}
	
	@Test
	public void testGetMarketRepository() {
		OSCRepository<Integer, Market> actual = service.getMarketRepository();
		
		assertNotNull(actual);
		assertNotSame(marketsMock, actual);
		assertEquals(OSCRepositoryDecoratorRO.class, actual.getClass());
		assertSame(marketsMock, ((OSCRepositoryDecoratorRO) actual).getDecoratedRepository());
	}
	
	@Test
	public void testGetBoardRepository() {
		OSCRepository<String, Board> actual = service.getBoardRepository();
		
		assertNotNull(actual);
		assertNotSame(boardsMock, actual);
		assertEquals(OSCRepositoryDecoratorRO.class, actual.getClass());
		assertSame(boardsMock, ((OSCRepositoryDecoratorRO) actual).getDecoratedRepository());
	}
	
	@Test
	public void testGetSecurityParamsRepository() {
		OSCRepository<GSymbol, SecurityParams> actual = service.getSecurityParamsRepository();
		
		assertNotNull(actual);
		assertNotSame(secParamsMock, actual);
		assertEquals(OSCRepositoryDecoratorRO.class, actual.getClass());
		assertSame(secParamsMock, ((OSCRepositoryDecoratorRO) actual).getDecoratedRepository());
	}
	
	@Test
	public void testGetSecurityBoardParamsRepository() {
		OSCRepository<TSymbol, SecurityBoardParams> actual = service.getSecurityBoardParamsRepository();
		
		assertNotNull(actual);
		assertNotSame(secBoardParamsMock, actual);
		assertEquals(OSCRepositoryDecoratorRO.class, actual.getClass());
		assertSame(secBoardParamsMock, ((OSCRepositoryDecoratorRO) actual).getDecoratedRepository());
	}
	
	@Test
	public void testGetSecurityQuotationsRepository() {
		OSCRepository<TSymbol, SecurityQuotations> actual = service.getSecurityQuotationsRepository();
		
		assertNotNull(actual);
		assertNotSame(secQuotsMock, actual);
		assertEquals(OSCRepositoryDecoratorRO.class, actual.getClass());
		assertSame(secQuotsMock, ((OSCRepositoryDecoratorRO) actual).getDecoratedRepository());
	}
	
	@Test
	public void testGetClientRepository() {
		OSCRepository<String, Client> actual = service.getClientRepository();
		
		assertNotNull(actual);
		assertNotSame(clientsMock, actual);
		assertEquals(OSCRepositoryDecoratorRO.class, actual.getClass());
		assertSame(clientsMock, ((OSCRepositoryDecoratorRO) actual).getDecoratedRepository());
	}
	
	@Test
	public void testGetMoneyPositionRepository() {
		OSCRepository<ID.MP, MoneyPosition> actual = service.getMoneyPositionRepository();
		
		assertNotNull(actual);
		assertNotSame(moneyPositionsMock, actual);
		assertEquals(OSCRepositoryDecoratorRO.class, actual.getClass());
		assertSame(moneyPositionsMock, ((OSCRepositoryDecoratorRO) actual).getDecoratedRepository());
	}
	
	@Test
	public void testGetSecPositionRepository() {
		OSCRepository<ID.SP, SecPosition> actual = service.getSecPositionRepository();
		
		assertNotNull(actual);
		assertNotSame(secPositionsMock, actual);
		assertEquals(OSCRepositoryDecoratorRO.class, actual.getClass());
		assertSame(secPositionsMock, ((OSCRepositoryDecoratorRO) actual).getDecoratedRepository());
	}
	
	@Test
	public void testGetFortsMoneyRepository() {
		OSCRepository<ID.FM, FortsMoney> actual = service.getFortsMoneyRepository();
		
		assertNotNull(actual);
		assertNotSame(fortsMoneyMock, actual);
		assertEquals(OSCRepositoryDecoratorRO.class, actual.getClass());
		assertSame(fortsMoneyMock, ((OSCRepositoryDecoratorRO) actual).getDecoratedRepository());
	}
	
	@Test
	public void testGetFortsPositionRepository() {
		OSCRepository<ID.FP, FortsPosition> actual = service.getFortsPositionRepository();
		
		assertNotNull(actual);
		assertNotSame(fortsPositionsMock, actual);
		assertEquals(OSCRepositoryDecoratorRO.class, actual.getClass());
		assertSame(fortsPositionsMock, ((OSCRepositoryDecoratorRO) actual).getDecoratedRepository());
	}
	
	@Test
	public void testGetFortsCollateralsRepository() {
		OSCRepository<ID.FC, FortsCollaterals> actual = service.getFortsCollateralsRepository();
		
		assertNotNull(actual);
		assertNotSame(fortsCollateralsMock, actual);
		assertEquals(OSCRepositoryDecoratorRO.class, actual.getClass());
		assertSame(fortsCollateralsMock, ((OSCRepositoryDecoratorRO) actual).getDecoratedRepository());
	}
	
	@Test
	public void testGetSpotLimitsRepository() {
		OSCRepository<ID.SL, SpotLimits> actual = service.getSpotLimitsRepository();
		
		assertNotNull(actual);
		assertNotSame(spotLimitsMock, actual);
		assertEquals(OSCRepositoryDecoratorRO.class, actual.getClass());
		assertSame(spotLimitsMock, ((OSCRepositoryDecoratorRO) actual).getDecoratedRepository());
	}
	
	@Test
	public void testGetUnitedLimitsRepository() {
		OSCRepository<ID.UL, UnitedLimits> actual = service.getUnitedLimitsRepository();
		
		assertNotNull(actual);
		assertNotSame(unitedLimitsMock, actual);
		assertEquals(OSCRepositoryDecoratorRO.class, actual.getClass());
		assertSame(unitedLimitsMock, ((OSCRepositoryDecoratorRO) actual).getDecoratedRepository());
	}
	
	@Test
	public void testIsExistsSecurityParams_SecIDG() {
		service = new TQDirectory(
				ckinds,
				markets,
				boards,
				secParams,
				secBoardParams,
				secQuots,
				clients,
				moneyPositions,
				secPositions,
				fortsMoney,
				fortsPositions,
				fortsCollaterals,
				spotLimits,
				unitedLimits,
				conStat,
				tq2gidMap,
				gid2tqMap);
		tq2gidMap.put(new TQSecIDG("RIZ9", 4),  new GSymbol("RTS-12.19", "FORTS", "RUB", SymbolType.FUTURES));
		control.replay();
		
		assertTrue(service.isExistsSecurityParams(new TQSecIDG("RIZ9", 4)));
		assertFalse(service.isExistsSecurityParams(new TQSecIDG("SiZ9", 4)));
		
		control.verify();
	}
	
	@Test
	public void testIsExistsSecurityParams_SymbolGID() {
		expect(secParamsMock.contains(new GSymbol("GAZP", "MICEX", "RUB", SymbolType.STOCK))).andReturn(true);
		expect(secParamsMock.contains(new GSymbol("RIZ9", "FORTS", "RUB", SymbolType.FUTURES))).andReturn(false);
		control.replay();
		
		assertTrue(service.isExistsSecurityParams(new GSymbol("GAZP", "MICEX", "RUB", SymbolType.STOCK)));
		assertFalse(service.isExistsSecurityParams(new GSymbol("RIZ9", "FORTS", "RUB", SymbolType.FUTURES)));
		
		control.verify();
	}
	
	@Test
	public void testIsExistsSecurityParams_SymbolTID() {
		service = new TQDirectory(
				ckinds,
				markets,
				boards,
				secParamsMock,
				secBoardParams,
				secQuots,
				clients,
				moneyPositions,
				secPositions,
				fortsMoney,
				fortsPositions,
				fortsCollaterals,
				spotLimits,
				unitedLimits,
				conStat,
				tq2gidMap,
				gid2tqMap);
		markets.getOrCreate(1).consume(new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 1)
				.withToken(FMarket.NAME, "MICEX")
				.buildUpdate()
			);
		markets.getOrCreate(4).consume(new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 4)
				.withToken(FMarket.NAME, "FORTS")
				.buildUpdate()
			);
		boards.getOrCreate("EQTB").consume(new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "EQTB")
				.withToken(FBoard.MARKET_ID, 1)
				.withToken(FBoard.NAME, "EQTB XXX")
				.withToken(FBoard.TYPE, 0)
				.buildUpdate()
			);
		boards.getOrCreate("FUT").consume(new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "FUT")
				.withToken(FBoard.MARKET_ID, 4)
				.withToken(FBoard.NAME, "FUT XXX")
				.withToken(FBoard.TYPE, 0)
				.buildUpdate()
			);
		expect(secParamsMock.contains(new GSymbol("GAZP", "MICEX", "RUB", SymbolType.STOCK))).andReturn(true);
		expect(secParamsMock.contains(new GSymbol("RIZ9", "FORTS", "RUB", SymbolType.FUTURES))).andReturn(false);
		control.replay();
		
		assertTrue(service.isExistsSecurityParams(new TSymbol("GAZP", "EQTB", "RUB", SymbolType.STOCK)));
		assertFalse(service.isExistsSecurityParams(new TSymbol("RIZ9", "FUT", "RUB", SymbolType.FUTURES)));
		
		control.verify();
	}
	
	@Test
	public void testGetSecurityParams_SecIDG() {
		service = new TQDirectory(queue);
		service.updateMarket(new TQStateUpdate<>(4, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 4)
				.withToken(FMarket.NAME, "FORTS")
				.buildUpdate()
			));
		service.updateSecurityParamsF(new TQStateUpdate<>(
				new TQSecIDF("RIZ9", 4, "FUT", "RTS-12.19", SecType.FUT),
				new DeltaUpdateBuilder()
					.withToken(FSecurity.SECCODE, "RIZ9")
					.withToken(FSecurity.DEFAULT_BOARDCODE, "FUT")
					.withToken(FSecurity.SHORT_NAME, "RTS-12.19")
					.buildUpdate()
			));
		control.replay();
		
		SecurityParams actual = service.getSecurityParams(new TQSecIDG("RIZ9", 4));
		
		control.verify();
		assertNotNull(actual);
		assertEquals("RIZ9", actual.getSecCode());
		assertEquals("FUT", actual.getDefaultBoard());
		assertEquals("RTS-12.19", actual.getShortName());
	}
	
	@Test
	public void testGetSecurityParams_SymbolGID() {
		service = new TQDirectory(queue);
		service.updateMarket(new TQStateUpdate<>(4, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 4)
				.withToken(FMarket.NAME, "FORTS")
				.buildUpdate()
			));
		service.updateSecurityParamsF(new TQStateUpdate<>(
				new TQSecIDF("RIZ9", 4, "FUT", "RTS-12.19", SecType.FUT),
				new DeltaUpdateBuilder()
					.withToken(FSecurity.SECCODE, "RIZ9")
					.withToken(FSecurity.DEFAULT_BOARDCODE, "FUT")
					.withToken(FSecurity.SHORT_NAME, "RTS-12.19")
					.buildUpdate())
			);
		control.replay();

		SecurityParams actual = service.getSecurityParams(new GSymbol("RTS-12.19", "FORTS", "RUB", SymbolType.FUTURES));
		
		control.verify();
		assertNotNull(actual);
		assertEquals("RIZ9", actual.getSecCode());
		assertEquals("FUT", actual.getDefaultBoard());
		assertEquals("RTS-12.19", actual.getShortName());
	}
	
	@Test
	public void testGetSecurityParams_SymbolTID() {
		service = new TQDirectory(queue);
		service.updateMarket(new TQStateUpdate<>(4, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 4)
				.withToken(FMarket.NAME, "FORTS")
				.buildUpdate()
			));
		service.updateBoard(new TQStateUpdate<>("FUT", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "FUT")
				.withToken(FBoard.MARKET_ID, 4)
				.withToken(FBoard.NAME, "FUT XXX")
				.withToken(FBoard.TYPE, 0)
				.buildUpdate()
			));
		service.updateSecurityParamsF(new TQStateUpdate<>(
				new TQSecIDF("RIZ9", 4, "FUT", "RTS-12.19", SecType.FUT),
				new DeltaUpdateBuilder()
					.withToken(FSecurity.SECCODE, "RIZ9")
					.withToken(FSecurity.DEFAULT_BOARDCODE, "FUT")
					.withToken(FSecurity.SHORT_NAME, "RTS-12.19")
					.buildUpdate())
			);
		control.replay();

		SecurityParams actual = service.getSecurityParams(new TSymbol("RTS-12.19", "FUT", "RUB", SymbolType.FUTURES));
		
		control.verify();
		assertNotNull(actual);
		assertEquals("RIZ9", actual.getSecCode());
		assertEquals("FUT", actual.getDefaultBoard());
		assertEquals("RTS-12.19", actual.getShortName());
	}
	
	@Test
	public void testIsExistsSecurityBoardParams_SecIDT() {
		service = new TQDirectory(queue);
		service.updateMarket(new TQStateUpdate<>(4, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 4)
				.withToken(FMarket.NAME, "FORTS")
				.buildUpdate()
			));
		service.updateBoard(new TQStateUpdate<>("FUT", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "FUT")
				.withToken(FBoard.MARKET_ID, 4)
				.withToken(FBoard.NAME, "FORTS-Futures")
				.withToken(FBoard.TYPE, 0)
				.buildUpdate()
			));
		service.updateBoard(new TQStateUpdate<>("OPT", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "OPT")
				.withToken(FBoard.MARKET_ID, 4)
				.withToken(FBoard.NAME, "FORTS-Options")
				.withToken(FBoard.TYPE, 0)
				.buildUpdate()
			));
		service.updateSecurityParamsF(new TQStateUpdate<>(
				new TQSecIDF("RIZ9", 4, "FUT", "RTS-12.19", SecType.FUT),
				new DeltaUpdateBuilder()
					.withToken(FSecurity.SECCODE, "RIZ9")
					.withToken(FSecurity.DEFAULT_BOARDCODE, "FUT")
					.withToken(FSecurity.SHORT_NAME, "RTS-12.19")
					.buildUpdate())
			);
		service.updateSecurityBoardParams(new TQStateUpdate<>(new TQSecIDT("RIZ9", "FUT"), new DeltaUpdateBuilder()
				.withToken(FSecurityBoard.SECCODE, "marker value")
				.buildUpdate()
			));
		control.replay();
		
		assertTrue(service.isExistsSecurityBoardParams(new TQSecIDT("RIZ9", "FUT")));
		assertFalse(service.isExistsSecurityBoardParams(new TQSecIDT("Eu63500BF9", "OPT")));
		assertFalse(service.isExistsSecurityBoardParams(new TQSecIDT("GAZP", "EQTB")));
		
		control.verify();
	}
	
	@Test
	public void testIsExistsSecurityBoardParams_SymbolTID() {
		service = new TQDirectory(
				ckinds,
				markets,
				boards,
				secParams,
				secBoardParamsMock,
				secQuots,
				clients,
				moneyPositions,
				secPositions,
				fortsMoney,
				fortsPositions,
				fortsCollaterals,
				spotLimits,
				unitedLimits,
				conStat,
				tq2gidMap,
				gid2tqMap);
		TSymbol tid1 = new TSymbol("RTS-12.19", "FUT", "RUB", SymbolType.FUTURES);
		TSymbol tid2 = new TSymbol("GAZP", "EQTB", "RUB", SymbolType.STOCK);
		expect(secBoardParamsMock.contains(tid1)).andReturn(true);
		expect(secBoardParamsMock.contains(tid2)).andReturn(false);
		control.replay();
		
		assertTrue(service.isExistsSecurityBoardParams(tid1));
		assertFalse(service.isExistsSecurityBoardParams(tid2));
		
		control.verify();
	}
	
	@Test
	public void testIsExistsSecurityQuotations_SymbolTID() {
		service = new TQDirectory(
				ckinds,
				markets,
				boards,
				secParams,
				secBoardParams,
				secQuotsMock,
				clients,
				moneyPositions,
				secPositions,
				fortsMoney,
				fortsPositions,
				fortsCollaterals,
				spotLimits,
				unitedLimits,
				conStat,
				tq2gidMap,
				gid2tqMap);
		TSymbol tid1 = new TSymbol("SBERP", "EQTB", "RUB", SymbolType.STOCK);
		TSymbol tid2 = new TSymbol("BOOM", "KAPPA", "RUB", SymbolType.STOCK);
		expect(secQuotsMock.contains(tid1)).andReturn(false);
		expect(secQuotsMock.contains(tid2)).andReturn(true);
		control.replay();
		
		assertFalse(service.isExistsSecurityQuotations(tid1));
		assertTrue(service.isExistsSecurityQuotations(tid2));
		
		control.verify();
	}
	
	@Test
	public void testGetSecurityBoardParams_SecIDT() {
		service = new TQDirectory(
				ckinds,
				markets,
				boards,
				secParams,
				secBoardParams,
				secQuots,
				clients,
				moneyPositions,
				secPositions,
				fortsMoney,
				fortsPositions,
				fortsCollaterals,
				spotLimits,
				unitedLimits,
				conStat,
				tq2gidMap,
				gid2tqMap);
		service.updateBoard(new TQStateUpdate<>("FUT", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "FUT")
				.withToken(FBoard.MARKET_ID, 4)
				.withToken(FBoard.NAME, "FORTS-Futures")
				.withToken(FBoard.TYPE, 0)
				.buildUpdate()
			));
		TSymbol tid = new TSymbol("RTS-12.19", "FUT", "RUB", SymbolType.FUTURES);
		tq2gidMap.put(new TQSecIDG("RIZ9", 4), new GSymbol("RTS-12.19", "FORTS", "RUB", SymbolType.FUTURES));
		SecurityBoardParams x = secBoardParams.getOrCreate(tid);
		control.replay();
		
		SecurityBoardParams actual = service.getSecurityBoardParams(new TQSecIDT("RIZ9", "FUT"));
		
		control.verify();
		assertSame(x, actual);
	}
	
	@Test
	public void testGetSecurityBoardParams_SymbolTID() {
		TSymbol tid = new TSymbol("Si-12.19", "FUT", "RUB", SymbolType.FUTURES);
		SecurityBoardParams sbpMock = control.createMock(SecurityBoardParams.class);
		expect(secBoardParamsMock.getOrThrow(tid)).andReturn(sbpMock);
		control.replay();
		
		assertSame(sbpMock, service.getSecurityBoardParams(tid));
		
		control.verify();
	}
	
	@Test
	public void testGetSecurityQuotations_SymbolTID() {
		TSymbol tid = new TSymbol("GAZP", "EQTB", "RUB", SymbolType.STOCK);
		SecurityQuotations entityMock = control.createMock(SecurityQuotations.class);
		expect(secQuotsMock.getOrThrow(tid)).andReturn(entityMock);
		control.replay();
		
		assertSame(entityMock, service.getSecurityQuotations(tid));
		
		control.verify();
	}
	
	@Test
	public void testToSymbol_SecIDF() {
		service = new TQDirectory(queue);
		service.updateMarket(new TQStateUpdate<>(0, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 1)
				.withToken(FMarket.NAME, "XXX 0")
				.buildUpdate()
			));
		service.updateMarket(new TQStateUpdate<>(1, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 1)
				.withToken(FMarket.NAME, "XXX 1")
				.buildUpdate()
			));
		service.updateMarket(new TQStateUpdate<>(2, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 2)
				.withToken(FMarket.NAME, "XXX 2")
				.buildUpdate()
			));
		
		assertEquals(new Symbol("U:boo@GAP:RUB"), service.toSymbol(new TQSecIDF("boo", 1, "GAP", "BAA", SecType.ADR)));
		assertEquals(new Symbol("B:lol@ZOB:RUB"), service.toSymbol(new TQSecIDF("lol", 2, "ZOB", "KAA", SecType.BOND)));
		assertEquals(new Symbol("C:JPY@FX:RUB"), service.toSymbol(new TQSecIDF("JPY", 0, "FX", "JAPAN", SecType.CURRENCY)));
		assertEquals(new Symbol("U:gaz@MAP:RUB"), service.toSymbol(new TQSecIDF("gaz", 2,"MAP", "GGG", SecType.ERROR))); 
		assertEquals(new Symbol("C:CAD@FX:RUB"), service.toSymbol(new TQSecIDF("CAD", 0, "FX", "CANADA", SecType.ETS_CURRENCY)));
		assertEquals(new Symbol("U:GGR@MAP:RUB"), service.toSymbol(new TQSecIDF("GGR", 2, "MAP", "GAGR", SecType.ETS_SWAP)));
		assertEquals(new Symbol("F:GAZ@FUT:RUB"), service.toSymbol(new TQSecIDF("GAZ", 0, "FUT", "<G>", SecType.FOB)));
		assertEquals(new Symbol("F:RTS-9.19@FUT:RUB"), service.toSymbol(new TQSecIDF("RIZ", 1, "FUT", "RTS-9.19", SecType.FUT)));
		assertEquals(new Symbol("B:ZAP@ZOB:RUB"), service.toSymbol(new TQSecIDF("ZAP", 2, "ZOB", "Zorg", SecType.GKO)));
		assertEquals(new Symbol("U:zzz@RAB:RUB"), service.toSymbol(new TQSecIDF("zzz", 1, "RAB", "aaa", SecType.IDX)));
		assertEquals(new Symbol("U:bak@foo:RUB"), service.toSymbol(new TQSecIDF("bak", 0, "foo", "Barter", SecType.MCT)));
		assertEquals(new Symbol("U:GOLD@LOL:RUB"), service.toSymbol(new TQSecIDF("GOLD", 2, "LOL", "Gold", SecType.METAL)));
		assertEquals(new Symbol("U:AAPL@RTX:RUB"), service.toSymbol(new TQSecIDF("AAPL", 1, "RTX", "Apple", SecType.NYSE)));
		assertEquals(new Symbol("U:BR@COM:RUB"), service.toSymbol(new TQSecIDF("BR", 2, "COM", "Brent", SecType.OIL)));
		assertEquals(new Symbol("O:Griz-3.19@OPDESK:RUB"), service.toSymbol(new TQSecIDF("GZZ", 1, "OPDESK", "Griz-3.19", SecType.OPT)));
		assertEquals(new Symbol("U:bubble@foo:RUB"), service.toSymbol(new TQSecIDF("bubble", 0, "foo", "Bx", SecType.QUOTES)));
		assertEquals(new Symbol("S:SBER@TQBR:RUB"), service.toSymbol(new TQSecIDF("SBER", 2, "TQBR", "Sberbank", SecType.SHARE)));
	}
	
	@Test
	public void testToSymbolGID_SecIDF() {
		service = new TQDirectory(queue);
		service.updateMarket(new TQStateUpdate<>(1, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 1)
				.withToken(FMarket.NAME, "MICEX")
				.buildUpdate()
			));
		service.updateMarket(new TQStateUpdate<>(4, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 4)
				.withToken(FMarket.NAME, "FORTS")
				.buildUpdate()
			));
		
		GSymbol
		actual = service.toSymbolGID(new TQSecIDF("GAZP", 1, "EQTB", "Gazprom AO", SecType.SHARE));
		assertEquals(new GSymbol("GAZP", "MICEX", "RUB", SymbolType.STOCK), actual);
		
		actual = service.toSymbolGID(new TQSecIDF("RIZ", 4, "FUT", "RTS-12.19", SecType.FUT));
		assertEquals(new GSymbol("RTS-12.19", "FORTS", "RUB", SymbolType.FUTURES), actual);
	}
	
	@Test
	public void testToSymbolGID_SecIDG() {
		service = new TQDirectory(queue);
		service.updateMarket(new TQStateUpdate<>(1, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 1)
				.withToken(FMarket.NAME, "MICEX")
				.buildUpdate()
			));
		service.updateMarket(new TQStateUpdate<>(4, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 4)
				.withToken(FMarket.NAME, "FORTS")
				.buildUpdate()
			));
		service.updateSecurityParamsF(new TQStateUpdate<>(
				new TQSecIDF("GAZP", 1, "EQTB", "Gazprom AO", SecType.SHARE),
				new DeltaUpdateBuilder().buildUpdate()
			));
		service.updateSecurityParamsF(new TQStateUpdate<>(
				new TQSecIDF("RIZ9", 4, "FUT", "RTS-12.19", SecType.FUT),
				new DeltaUpdateBuilder().buildUpdate()
			));
		
		GSymbol
		actual = service.toSymbolGID(new TQSecIDG("GAZP", 1));
		assertEquals(new GSymbol("GAZP", "MICEX", "RUB", SymbolType.STOCK), actual);
		
		actual = service.toSymbolGID(new TQSecIDG("RIZ9", 4));
		assertEquals(new GSymbol("RTS-12.19", "FORTS", "RUB", SymbolType.FUTURES), actual);
	}
	
	@Test
	public void testToSymbolGID_SecIDG_ThrowsIfNotMapped() {
		service = new TQDirectory(queue);
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Symbol GID not found: TQSecIDG[secCode=GAZP,marketID=1]");
		
		service.toSymbolGID(new TQSecIDG("GAZP", 1));
	}
	
	@Test
	public void testToSymbolTID_SecIDT() {
		service = new TQDirectory(queue);
		service.updateMarket(new TQStateUpdate<>(1, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 1)
				.withToken(FMarket.NAME, "MICEX")
				.buildUpdate()
			));
		service.updateMarket(new TQStateUpdate<>(4, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 4)
				.withToken(FMarket.NAME, "FORTS")
				.buildUpdate()
			));
		service.updateBoard(new TQStateUpdate<>("EQTB", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "EQTB")
				.withToken(FBoard.MARKET_ID, 1)
				.withToken(FBoard.NAME, "XXX EQTB")
				.buildUpdate()
			));
		service.updateBoard(new TQStateUpdate<>("FUT", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "FUT")
				.withToken(FBoard.MARKET_ID, 4)
				.withToken(FBoard.NAME, "XXX FUT")
				.buildUpdate()
			));
		service.updateSecurityParamsF(new TQStateUpdate<>(
				new TQSecIDF("GAZP", 1, "EQTB", "Gazprom AO", SecType.SHARE),
				new DeltaUpdateBuilder().buildUpdate()
			));
		service.updateSecurityParamsF(new TQStateUpdate<>(
				new TQSecIDF("RIZ9", 4, "FUT", "RTS-12.19", SecType.FUT),
				new DeltaUpdateBuilder().buildUpdate()
			));
		
		TSymbol
		actual = service.toSymbolTID(new TQSecIDT("GAZP", "EQTB"));
		assertEquals(new TSymbol("GAZP", "EQTB", "RUB", SymbolType.STOCK), actual);
		
		actual = service.toSymbolTID(new TQSecIDT("RIZ9", "FUT"));
		assertEquals(new TSymbol("RTS-12.19", "FUT", "RUB", SymbolType.FUTURES), actual);
	}
	
	@Test
	public void testToSymbol_SymbolTID() {
		service = new TQDirectory(queue);
		service.updateMarket(new TQStateUpdate<>(4, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 4)
				.withToken(FMarket.NAME, "FORTS")
				.buildUpdate()
			));
		service.updateMarket(new TQStateUpdate<>(1, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 1)
				.withToken(FMarket.NAME, "MICEX")
				.buildUpdate()
			));
		service.updateSecurityParamsF(new TQStateUpdate<>(new TQSecIDF("RIZ9", 4, "FUT", "RTS-12.19", SecType.FUT),
				new DeltaUpdateBuilder()
					.withToken(FSecurity.SECTYPE, SecType.FUT)
					.buildUpdate()
			));
		service.updateSecurityParamsF(new TQStateUpdate<>(new TQSecIDF("GAZP", 1, "EQTB", "GAZP", SecType.SHARE),
				new DeltaUpdateBuilder()
					.withToken(FSecurity.SECTYPE, SecType.SHARE)
					.buildUpdate()
			));
		control.replay();
		
		assertEquals(new Symbol("F:RTS-12.19@FUT:RUB"), service.toSymbol(new TSymbol("RTS-12.19", "FUT", "RUB", SymbolType.FUTURES)));
		assertEquals(new Symbol("S:GAZP@EQTB:RUB"), service.toSymbol(new TSymbol("GAZP", "EQTB", "RUB", SymbolType.STOCK)));
		
		control.verify();
	}
	
	@Test
	public void testToSymbolTID_Symbol() {
		service = new TQDirectory(queue);
		control.replay();
		
		TSymbol expected = new TSymbol("RTS-12.19", "FUT", "RUB", SymbolType.FUTURES);
		assertEquals(expected, service.toSymbolTID(new Symbol("F:RTS-12.19@FUT:RUB")));
	}
	
	@Test
	public void testToSecIDT2_Symbol() {
		service = new TQDirectory(queue);
		service.updateMarket(new TQStateUpdate<>(4, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 4)
				.withToken(FMarket.NAME, "FORTS")
				.buildUpdate()
			));
		service.updateBoard(new TQStateUpdate<>("FUT", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "FUT")
				.withToken(FBoard.MARKET_ID, 4)
				.withToken(FBoard.NAME, "Futures")
				.withToken(FBoard.TYPE, 0)
				.buildUpdate()
			));
		service.updateSecurityParamsF(new TQStateUpdate<>(
				new TQSecIDF("RIZ9", 4, "FUT", "RTS-12.09", SecType.FUT),
				new DeltaUpdateBuilder().withToken(FSecurity.SECCODE, "N/A").buildUpdate()));
		service.updateSecurityParamsF(new TQStateUpdate<>(
				new TQSecIDF("RIZ9", 4, "FUT", "RTS-12.19", SecType.FUT),
				new DeltaUpdateBuilder().withToken(FSecurity.SECCODE, "N/A").buildUpdate()));
		
		assertEquals(new TQSecIDT("RIZ9", "FUT"), service.toSecIDT(new Symbol("F:RTS-12.09@FUT:RUB"), false));
		assertNull(service.toSecIDT(new Symbol("F:RTS-12.09@FUT:RUB"), true));
		assertEquals(new TQSecIDT("RIZ9", "FUT"), service.toSecIDT(new Symbol("F:RTS-12.19@FUT:RUB"), false));
		assertEquals(new TQSecIDT("RIZ9", "FUT"), service.toSecIDT(new Symbol("F:RTS-12.19@FUT:RUB"), true));
		assertNull(service.toSecIDT(new Symbol("F:Si-12.19@FUT:RUB"), false));
		assertNull(service.toSecIDT(new Symbol("F:Si-12.19@FUT:RUB"), true));
	}
	
	@Test
	public void testToSecIDT2_Symbol_IfBoardNotExists() throws Exception {
		service = new TQDirectory(queue);
		service.updateMarket(new TQStateUpdate<>(1, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 1)
				.withToken(FMarket.NAME, "MICEX")
				.buildUpdate()
			));
		service.updateBoard(new TQStateUpdate<>("TQBR", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "TQBR")
				.withToken(FBoard.MARKET_ID, 1)
				.withToken(FBoard.NAME, "T+: stocks  DRs")
				.withToken(FBoard.TYPE, 0)
				.buildUpdate()
			));
		service.updateSecurityParamsF(new TQStateUpdate<>(
				new TQSecIDF("GAZP", 1, "TQBR", "Gazprom", SecType.SHARE),
				new DeltaUpdateBuilder().withToken(FSecurity.SECCODE, "N/A").buildUpdate()));
		
		assertEquals(new TQSecIDT("GAZP", "TQBR"), service.toSecIDT(new Symbol("S:GAZP@TQBR:RUB"), true));
		assertEquals(new TQSecIDT("GAZP", "TQBR"), service.toSecIDT(new Symbol("S:GAZP@TQBR:RUB"), false));
		assertNull(service.toSecIDT(new Symbol("S:GAZP@EQTB:RUB"), true));
		assertNull(service.toSecIDT(new Symbol("S:GAZP@EQTB:RUB"), false));
		// Should be no errors
	}
	
	@Test
	public void testGetKnownSymbols() {
		service = new TQDirectory(queue);
		service.updateMarket(new TQStateUpdate<>(1, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 1)
				.withToken(FMarket.NAME, "XXL")
				.buildUpdate()));
		service.updateBoard(new TQStateUpdate<>("FOO", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "FOO")
				.withToken(FBoard.MARKET_ID, 1)
				.buildUpdate()));
		service.updateBoard(new TQStateUpdate<>("BAR", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "BAR")
				.withToken(FBoard.MARKET_ID, 1)
				.buildUpdate()));
		service.updateBoard(new TQStateUpdate<>("BUZ", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "BUZ")
				.withToken(FBoard.MARKET_ID, 1)
				.buildUpdate()));
		service.updateBoard(new TQStateUpdate<>("BOO", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "BOO")
				.withToken(FBoard.MARKET_ID, 1)
				.buildUpdate()));
		service.updateSecurityParamsF(new TQStateUpdate<>(
				new TQSecIDF("CODE", 1, "BAR", "Code Ticker", SecType.SHARE),
				new DeltaUpdateBuilder().buildUpdate()));
		service.updateSecurityBoardParams(new TQStateUpdate<>(
				new TQSecIDT("CODE", "FOO"),
				new DeltaUpdateBuilder().withToken(FSecurityBoard.BOARD, "FOO").buildUpdate()));
		service.updateSecurityBoardParams(new TQStateUpdate<>(
				new TQSecIDT("CODE", "BAR"),
				new DeltaUpdateBuilder().withToken(FSecurityBoard.BOARD, "BAR").buildUpdate()));
		service.updateSecurityBoardParams(new TQStateUpdate<>(
				new TQSecIDT("CODE", "BUZ"),
				new DeltaUpdateBuilder().withToken(FSecurityBoard.BOARD, "BUZ").buildUpdate()));
		control.replay();
		
		List<Symbol> actual = service.getKnownSymbols(new GSymbol("CODE", "XXL", "RUB", SymbolType.STOCK));
		
		control.verify();
		assertTrue(actual.contains(new Symbol("CODE", "FOO", "RUB", SymbolType.STOCK)));
		assertTrue(actual.contains(new Symbol("CODE", "BAR", "RUB", SymbolType.STOCK)));
		assertTrue(actual.contains(new Symbol("CODE", "BUZ", "RUB", SymbolType.STOCK)));
	}
	
	@Test
	public void testIsKnownSymbol() {
		service = new TQDirectory(queue);
		service.updateMarket(new TQStateUpdate<>(1, new DeltaUpdateBuilder()
				.withToken(FMarket.ID, 1)
				.withToken(FMarket.NAME, "XXL")
				.buildUpdate()));
		service.updateBoard(new TQStateUpdate<>("BAR", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "BAR")
				.withToken(FBoard.MARKET_ID, 1)
				.buildUpdate()));
		service.updateSecurityParamsF(new TQStateUpdate<>(
				new TQSecIDF("FOO", 1, "BAR", "FOO ticker", SecType.SHARE),
				new DeltaUpdateBuilder().buildUpdate()));
		service.updateSecurityBoardParams(new TQStateUpdate<>(
				new TQSecIDT("FOO", "BAR"),
				new DeltaUpdateBuilder().buildUpdate()
			));
		
		assertTrue(service.isKnownSymbol(new Symbol("FOO", "BAR", "RUB", SymbolType.STOCK)));
		assertFalse(service.isKnownSymbol(new Symbol("GOO", "BOO", "RUB", SymbolType.STOCK)));
	}
	
	@Test
	public void testGetBoardName() {
		service = new TQDirectory(queue);
		service.updateBoard(new TQStateUpdate<>("EQDB", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "EQBD")
				.withToken(FBoard.NAME, "Main market: D bonds")
				.withToken(FBoard.MARKET_ID, 1)
				.buildUpdate()));
		service.updateBoard(new TQStateUpdate<>("EQNL", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "EQNL")
				.withToken(FBoard.NAME, "")
				.withToken(FBoard.MARKET_ID, 2)
				.buildUpdate()));
		service.updateBoard(new TQStateUpdate<>("AETS", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "AETS")
				.withToken(FBoard.MARKET_ID, 2)
				.buildUpdate()));
		
		assertEquals("Main market: D bonds", service.getBoardName("EQDB"));
		assertEquals("EQNL", service.getBoardName("EQNL"));
		assertEquals("AETS", service.getBoardName("AETS"));
	}
	
	@Test
	public void testGetConnectionStatus() {
		assertSame(conStatMock, service.getConnectionStatus());
	}
	
	@Test
	public void testUpdateConnectionStatus() {
		conStatMock.setConnected();
		conStatMock.setDisconnected();
		control.replay();
		
		service.updateConnectionStatus(true);
		service.updateConnectionStatus(false);
		
		control.verify();
	}

}

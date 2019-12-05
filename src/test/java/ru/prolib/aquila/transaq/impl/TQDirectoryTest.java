package ru.prolib.aquila.transaq.impl;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.Map;

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

@SuppressWarnings("rawtypes")
public class TQDirectoryTest {
	private static EventQueue queue;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		queue = new EventQueueImpl();
	}
	
	@Rule
	public ExpectedException eex = ExpectedException.none();
	private IMocksControl control;
	private OSCRepository<Integer, CKind> ckindsMock, ckinds;
	private OSCRepository<Integer, Market> marketsMock, markets;
	private OSCRepository<String, Board> boardsMock, boards;
	private OSCRepository<SymbolGID, SecurityParams> secParamsMock, secParams;
	private OSCRepository<SymbolTID, SecurityBoardParams> secBoardParamsMock, secBoardParams;
	private OSCRepository<SymbolTID, SecurityQuotations> secQuotsMock, secQuots;
	private DeltaUpdate duMock;
	private Map<ISecIDG, SymbolGID> gidMap;
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
		duMock = control.createMock(DeltaUpdate.class);
		ckinds = new OSCRepositoryImpl<>(new CKindFactory(queue), "XXX");
		markets = new OSCRepositoryImpl<>(new MarketFactory(queue), "XXX");
		boards = new OSCRepositoryImpl<>(new BoardFactory(queue), "XXX");
		secParams = new OSCRepositoryImpl<>(new SecurityParamsFactory(queue), "XXX");
		secBoardParams = new OSCRepositoryImpl<>(new SecurityBoardParamsFactory(queue), "XXX");
		secQuots = new OSCRepositoryImpl<>(new SecurityQuotationsFactory(queue), "XXX");
		gidMap = new LinkedHashMap<>();
		service = new TQDirectory(
				ckindsMock,
				marketsMock,
				boardsMock,
				secParamsMock,
				secBoardParamsMock,
				secQuotsMock,
				gidMap
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
		service = new TQDirectory(ckinds, markets, boards, secParamsMock, secBoardParams, secQuots, gidMap);
		SecurityParams entityMock = control.createMock(SecurityParams.class);
		secParamsMock.lock();
		expect(secParamsMock.getOrCreate(new SymbolGID("GAZP", 0))).andReturn(entityMock);
		entityMock.consume(duMock);
		secParamsMock.unlock();
		control.replay();
		
		service.updateSecurityParamsF(new TQStateUpdate<>(
				new TQSecIDF("GAZP", 0, "EQTB", "Gazprom AO", SecType.SHARE), duMock)
			);

		control.verify();
		assertEquals(new SymbolGID("GAZP", 0), gidMap.get(new TQSecIDG("GAZP", 0)));
	}
	
	@Test
	public void testUpdateSecurityParamsF_Fut() {
		service = new TQDirectory(ckinds, markets, boards, secParamsMock, secBoardParams, secQuots, gidMap);
		SecurityParams entityMock = control.createMock(SecurityParams.class);
		secParamsMock.lock();
		expect(secParamsMock.getOrCreate(new SymbolGID("RTS-12.19", 4))).andReturn(entityMock);
		entityMock.consume(duMock);
		secParamsMock.unlock();
		control.replay();

		service.updateSecurityParamsF(new TQStateUpdate<>(
				new TQSecIDF("RIZ9", 4, "FUT", "RTS-12.19", SecType.FUT), duMock)
			);
		
		control.verify();
		assertEquals(new SymbolGID("RTS-12.19", 4), gidMap.get(new TQSecIDG("RIZ9", 4)));
	}

	@Test
	public void testUpdateSecurityParamsF_Opt() {
		service = new TQDirectory(ckinds, markets, boards, secParamsMock, secBoardParams, secQuots, gidMap);
		SecurityParams entityMock = control.createMock(SecurityParams.class);
		secParamsMock.lock();
		expect(secParamsMock.getOrCreate(new SymbolGID("Eu-6.19M200619CA63500", 4))).andReturn(entityMock);
		entityMock.consume(duMock);
		secParamsMock.unlock();
		control.replay();

		service.updateSecurityParamsF(new TQStateUpdate<>(
				new TQSecIDF("Eu63500BF9", 4, "OPT", "Eu-6.19M200619CA63500", SecType.OPT), duMock)
			);
		
		control.verify();
		assertEquals(new SymbolGID("Eu-6.19M200619CA63500", 4), gidMap.get(new TQSecIDG("Eu63500BF9", 4)));
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
		gidMap.put(new TQSecIDG("RIZ9", 4), new SymbolGID("RTS-12.19", 4));
		SecurityParams entityMock = control.createMock(SecurityParams.class);
		secParamsMock.lock();
		expect(secParamsMock.getOrCreate(new SymbolGID("RTS-12.19", 4))).andReturn(entityMock);
		entityMock.consume(duMock);
		secParamsMock.unlock();
		control.replay();

		service.updateSecurityParamsP(new TQStateUpdate<>(new TQSecIDG("RIZ9", 4), duMock));
		
		control.verify();
	}
	
	@Test
	public void testUpdateSecurityBoardParams_ThrowsIfNoBoard() {
		service = new TQDirectory(ckinds, markets, boards, secParams, secBoardParamsMock, secQuots, gidMap);
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Board not found: FUT");
		control.replay();
		
		service.updateSecurityBoardParams(new TQStateUpdate<>(new TQSecIDT("RIZ9", "FUT"), duMock));
	}
	
	@Test
	public void testUpdateSecurityBoardParams_ThrowsIfNoSymbolGID() {
		service = new TQDirectory(ckinds, markets, boards, secParams, secBoardParamsMock, secQuots, gidMap);
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
		service = new TQDirectory(ckinds, markets, boards, secParams, secBoardParamsMock, secQuots, gidMap);
		boards.getOrCreate("FUT").consume(new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "FUT")
				.withToken(FBoard.MARKET_ID, 4)
				.withToken(FBoard.NAME, "FORTS-Futures")
				.withToken(FBoard.TYPE, 0)
				.buildUpdate()
			);
		gidMap.put(new TQSecIDG("RIZ9", 4), new SymbolGID("RTS-12.19", 4));
		SecurityBoardParams entityMock = control.createMock(SecurityBoardParams.class);
		expect(secBoardParamsMock.getOrCreate(new SymbolTID("RTS-12.19", 4, "FUT"))).andReturn(entityMock);
		entityMock.consume(duMock);
		control.replay();
		
		service.updateSecurityBoardParams(new TQStateUpdate<>(new TQSecIDT("RIZ9", "FUT"), duMock));
		
		control.verify();
	}
	
	@Test
	public void testUpdateSecurityQuotations_ThrowsIfNoBoard() {
		service = new TQDirectory(ckinds, markets, boards, secParams, secBoardParamsMock, secQuots, gidMap);
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Board not found: FUT");
		control.replay();
		
		service.updateSecurityQuotations(new TQStateUpdate<>(new TQSecIDT("RIZ9", "FUT"), duMock));
	}
	
	@Test
	public void testUpdateSecurityQuotations_ThrowsIfNoSymbolGID() {
		service = new TQDirectory(ckinds, markets, boards, secParams, secBoardParams, secQuots, gidMap);
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
		
		service.updateSecurityQuotations(new TQStateUpdate<>(new TQSecIDT("RIZ9", "FUT"), duMock));
	}
	
	@Test
	public void testUpdateSecurityQuotations() {
		service = new TQDirectory(ckinds, markets, boards, secParams, secBoardParams, secQuotsMock, gidMap);
		boards.getOrCreate("FUT").consume(new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "FUT")
				.withToken(FBoard.MARKET_ID, 4)
				.withToken(FBoard.NAME, "FORTS-Futures")
				.withToken(FBoard.TYPE, 0)
				.buildUpdate()
			);
		gidMap.put(new TQSecIDG("RIZ9", 4), new SymbolGID("RTS-12.19", 4));
		SecurityQuotations entityMock = control.createMock(SecurityQuotations.class);
		expect(secQuotsMock.getOrCreate(new SymbolTID("RTS-12.19", 4, "FUT"))).andReturn(entityMock);
		entityMock.consume(duMock);
		control.replay();
		
		service.updateSecurityQuotations(new TQStateUpdate<>(new TQSecIDT("RIZ9", "FUT"), duMock));
		
		control.verify();
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
		OSCRepository<SymbolGID, SecurityParams> actual = service.getSecurityParamsRepository();
		
		assertNotNull(actual);
		assertNotSame(secParamsMock, actual);
		assertEquals(OSCRepositoryDecoratorRO.class, actual.getClass());
		assertSame(secParamsMock, ((OSCRepositoryDecoratorRO) actual).getDecoratedRepository());
	}
	
	@Test
	public void testGetSecurityBoardParamsRepository() {
		OSCRepository<SymbolTID, SecurityBoardParams> actual = service.getSecurityBoardParamsRepository();
		
		assertNotNull(actual);
		assertNotSame(secBoardParamsMock, actual);
		assertEquals(OSCRepositoryDecoratorRO.class, actual.getClass());
		assertSame(secBoardParamsMock, ((OSCRepositoryDecoratorRO) actual).getDecoratedRepository());
	}
	
	@Test
	public void testGetSecurityQuotationsRepository() {
		OSCRepository<SymbolTID, SecurityQuotations> actual = service.getSecurityQuotationsRepository();
		
		assertNotNull(actual);
		assertNotSame(secQuotsMock, actual);
		assertEquals(OSCRepositoryDecoratorRO.class, actual.getClass());
		assertSame(secQuotsMock, ((OSCRepositoryDecoratorRO) actual).getDecoratedRepository());
	}
	
	@Test
	public void testIsExistsSecurityParams_SecIDG() {
		service = new TQDirectory(ckinds, markets, boards, secParams, secBoardParams, secQuots, gidMap);
		gidMap.put(new TQSecIDG("RIZ9", 4),  new SymbolGID("RTS-12.19", 4));
		control.replay();
		
		assertTrue(service.isExistsSecurityParams(new TQSecIDG("RIZ9", 4)));
		assertFalse(service.isExistsSecurityParams(new TQSecIDG("SiZ9", 4)));
		
		control.verify();
	}
	
	@Test
	public void testIsExistsSecurityParams_SymbolGID() {
		expect(secParamsMock.contains(new SymbolGID("GAZP", 1))).andReturn(true);
		expect(secParamsMock.contains(new SymbolGID("RIZ9", 4))).andReturn(false);
		control.replay();
		
		assertTrue(service.isExistsSecurityParams(new SymbolGID("GAZP", 1)));
		assertFalse(service.isExistsSecurityParams(new SymbolGID("RIZ9", 4)));
		
		control.verify();
	}
	
	@Test
	public void testIsExistsSecurityParams_SymbolTID() {
		expect(secParamsMock.contains(new SymbolGID("GAZP", 1))).andReturn(true);
		expect(secParamsMock.contains(new SymbolGID("RIZ9", 4))).andReturn(false);
		control.replay();
		
		assertTrue(service.isExistsSecurityParams(new SymbolTID("GAZP", 1, "EQTB")));
		assertFalse(service.isExistsSecurityParams(new SymbolTID("RIZ9", 4, "FUT")));
		
		control.verify();
	}
	
	@Test
	public void testGetSecurityParams_SecIDG() {
		service = new TQDirectory(ckinds, markets, boards, secParams, secBoardParams, secQuots, gidMap);
		service.updateSecurityParamsF(new TQStateUpdate<>(
				new TQSecIDF("RIZ9", 4, "FUT", "RTS-12.19", SecType.FUT),
				new DeltaUpdateBuilder()
					.withToken(FSecurity.SECCODE, "RIZ9")
					.withToken(FSecurity.DEFAULT_BOARDCODE, "FUT")
					.withToken(FSecurity.SHORT_NAME, "RTS-12.19")
					.buildUpdate())
			);
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
		service = new TQDirectory(ckinds, markets, boards, secParams, secBoardParams, secQuots, gidMap);
		service.updateSecurityParamsF(new TQStateUpdate<>(
				new TQSecIDF("RIZ9", 4, "FUT", "RTS-12.19", SecType.FUT),
				new DeltaUpdateBuilder()
					.withToken(FSecurity.SECCODE, "RIZ9")
					.withToken(FSecurity.DEFAULT_BOARDCODE, "FUT")
					.withToken(FSecurity.SHORT_NAME, "RTS-12.19")
					.buildUpdate())
			);
		control.replay();

		SecurityParams actual = service.getSecurityParams(new SymbolGID("RTS-12.19", 4));
		
		control.verify();
		assertNotNull(actual);
		assertEquals("RIZ9", actual.getSecCode());
		assertEquals("FUT", actual.getDefaultBoard());
		assertEquals("RTS-12.19", actual.getShortName());
	}
	
	@Test
	public void testGetSecurityParams_SymbolTID() {
		service = new TQDirectory(ckinds, markets, boards, secParams, secBoardParams, secQuots, gidMap);
		service.updateSecurityParamsF(new TQStateUpdate<>(
				new TQSecIDF("RIZ9", 4, "FUT", "RTS-12.19", SecType.FUT),
				new DeltaUpdateBuilder()
					.withToken(FSecurity.SECCODE, "RIZ9")
					.withToken(FSecurity.DEFAULT_BOARDCODE, "FUT")
					.withToken(FSecurity.SHORT_NAME, "RTS-12.19")
					.buildUpdate())
			);
		control.replay();

		SecurityParams actual = service.getSecurityParams(new SymbolTID("RTS-12.19", 4, "FUT"));
		
		control.verify();
		assertNotNull(actual);
		assertEquals("RIZ9", actual.getSecCode());
		assertEquals("FUT", actual.getDefaultBoard());
		assertEquals("RTS-12.19", actual.getShortName());
	}
	
	@Test
	public void testIsExistsSecurityBoardParams_SecIDT() {
		service = new TQDirectory(ckinds, markets, boards, secParams, secBoardParams, secQuots, gidMap);
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
		expect(secBoardParamsMock.contains(new SymbolTID("RIZ9", 4, "FUT"))).andReturn(true);
		expect(secBoardParamsMock.contains(new SymbolTID("GAZP", 1, "EQTB"))).andReturn(false);
		control.replay();
		
		assertTrue(service.isExistsSecurityBoardParams(new SymbolTID("RIZ9", 4, "FUT")));
		assertFalse(service.isExistsSecurityBoardParams(new SymbolTID("GAZP", 1, "EQTB")));
		
		control.verify();
	}
	
	@Test
	public void testIsExistsSecurityQuotations_SymbolTID() {
		expect(secQuotsMock.contains(new SymbolTID("SBERP", 1, "EQTB"))).andReturn(false);
		expect(secQuotsMock.contains(new SymbolTID("BOOM", 5, "KAPPA"))).andReturn(true);
		control.replay();
		
		assertFalse(service.isExistsSecurityQuotations(new SymbolTID("SBERP", 1, "EQTB")));
		assertTrue(service.isExistsSecurityQuotations(new SymbolTID("BOOM", 5, "KAPPA")));
		
		control.verify();
	}
	
	@Test
	public void testGetSecurityBoardParams_SecIDT() {
		service = new TQDirectory(ckinds, markets, boards, secParams, secBoardParams, secQuots, gidMap);
		service.updateBoard(new TQStateUpdate<>("FUT", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "FUT")
				.withToken(FBoard.MARKET_ID, 4)
				.withToken(FBoard.NAME, "FORTS-Futures")
				.withToken(FBoard.TYPE, 0)
				.buildUpdate()
			));
		gidMap.put(new TQSecIDG("RIZ9", 4), new SymbolGID("RTS-12.19", 4));
		SecurityBoardParams x = secBoardParams.getOrCreate(new SymbolTID("RTS-12.19", 4, "FUT"));
		control.replay();
		
		SecurityBoardParams actual = service.getSecurityBoardParams(new TQSecIDT("RIZ9", "FUT"));
		
		control.verify();
		assertSame(x, actual);
	}
	
	@Test
	public void testGetSecurityBoardParams_SymbolTID() {
		SecurityBoardParams sbpMock = control.createMock(SecurityBoardParams.class);
		expect(secBoardParamsMock.getOrThrow(new SymbolTID("Si-12.19", 4, "FUT"))).andReturn(sbpMock);
		control.replay();
		
		assertSame(sbpMock, service.getSecurityBoardParams(new SymbolTID("Si-12.19", 4, "FUT")));
		
		control.verify();
	}
	
	@Test
	public void testGetSecurityQuotations_SymbolTID() {
		SecurityQuotations entityMock = control.createMock(SecurityQuotations.class);
		expect(secQuotsMock.getOrThrow(new SymbolTID("GAZP", 1, "EQTB"))).andReturn(entityMock);
		control.replay();
		
		assertSame(entityMock, service.getSecurityQuotations(new SymbolTID("GAZP", 1, "EQTB")));
		
		control.verify();
	}
	
	@Test
	public void testToSymbol_SecIDF() {
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
	public void testToSymbol_SymbolTID() {
		service = new TQDirectory(ckinds, markets, boards, secParams, secBoardParams, secQuots, gidMap);
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
		
		assertEquals(new Symbol("F:RTS-12.19@FUT:RUB"), service.toSymbol(new SymbolTID("RTS-12.19", 4, "FUT")));
		assertEquals(new Symbol("S:GAZP@EQTB:RUB"), service.toSymbol(new SymbolTID("GAZP", 1, "EQTB")));
		
		control.verify();
	}

}

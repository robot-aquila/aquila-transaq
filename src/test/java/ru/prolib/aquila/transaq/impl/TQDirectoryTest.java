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

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
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
import ru.prolib.aquila.transaq.impl.TQField.FBoard;
import ru.prolib.aquila.transaq.impl.TQField.FSecurity;
import ru.prolib.aquila.transaq.impl.TQField.FSecurityBoard;

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
	private DeltaUpdate duMock;
	private Map<TQSecID1, SymbolGID> gidMap;
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
		duMock = control.createMock(DeltaUpdate.class);
		ckinds = new OSCRepositoryImpl<>(new CKindFactory(queue), "XXX");
		markets = new OSCRepositoryImpl<>(new MarketFactory(queue), "XXX");
		boards = new OSCRepositoryImpl<>(new BoardFactory(queue), "XXX");
		secParams = new OSCRepositoryImpl<>(new SecurityParamsFactory(queue), "XXX");
		secBoardParams = new OSCRepositoryImpl<>(new SecurityBoardParamsFactory(queue), "XXX");
		gidMap = new LinkedHashMap<>();
		service = new TQDirectory(
				ckindsMock,
				marketsMock,
				boardsMock,
				secParamsMock,
				secBoardParamsMock,
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
	public void testUpdateSecurityParamsF_NotSpecial() {
		service = new TQDirectory(ckinds, markets, boards, secParamsMock, secBoardParams, gidMap);
		SecurityParams entityMock = control.createMock(SecurityParams.class);
		secParamsMock.lock();
		expect(secParamsMock.getOrCreate(new SymbolGID("GAZP", 0))).andReturn(entityMock);
		entityMock.consume(duMock);
		secParamsMock.unlock();
		control.replay();
		
		service.updateSecurityParamsF(new TQStateUpdate<>(
				new TQSecID_F("GAZP", 0, "EQTB", "Gazprom AO", SecType.SHARE), duMock)
			);

		control.verify();
		assertEquals(new SymbolGID("GAZP", 0), gidMap.get(new TQSecID1("GAZP", 0)));
	}
	
	@Test
	public void testUpdateSecurityParamsF_Fut() {
		service = new TQDirectory(ckinds, markets, boards, secParamsMock, secBoardParams, gidMap);
		SecurityParams entityMock = control.createMock(SecurityParams.class);
		secParamsMock.lock();
		expect(secParamsMock.getOrCreate(new SymbolGID("RTS-12.19", 4))).andReturn(entityMock);
		entityMock.consume(duMock);
		secParamsMock.unlock();
		control.replay();

		service.updateSecurityParamsF(new TQStateUpdate<>(
				new TQSecID_F("RIZ9", 4, "FUT", "RTS-12.19", SecType.FUT), duMock)
			);
		
		control.verify();
		assertEquals(new SymbolGID("RTS-12.19", 4), gidMap.get(new TQSecID1("RIZ9", 4)));
	}

	@Test
	public void testUpdateSecurityParamsF_Opt() {
		service = new TQDirectory(ckinds, markets, boards, secParamsMock, secBoardParams, gidMap);
		SecurityParams entityMock = control.createMock(SecurityParams.class);
		secParamsMock.lock();
		expect(secParamsMock.getOrCreate(new SymbolGID("Eu-6.19M200619CA63500", 4))).andReturn(entityMock);
		entityMock.consume(duMock);
		secParamsMock.unlock();
		control.replay();

		service.updateSecurityParamsF(new TQStateUpdate<>(
				new TQSecID_F("Eu63500BF9", 4, "OPT", "Eu-6.19M200619CA63500", SecType.OPT), duMock)
			);
		
		control.verify();
		assertEquals(new SymbolGID("Eu-6.19M200619CA63500", 4), gidMap.get(new TQSecID1("Eu63500BF9", 4)));
	}
	
	@Test
	public void testUpdateSecurityParamsP_ThrowsIfNoSymbolGID() {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Symbol GID not found: TQSecID1[secCode=GAZP,marketID=0]");
		secParamsMock.lock();
		secParamsMock.unlock();
		control.replay();
		
		service.updateSecurityParamsP(new TQStateUpdate<>(new TQSecID1("GAZP", 0), duMock));
	}

	@Test
	public void testUpdateSecurityParamsP() {
		gidMap.put(new TQSecID1("RIZ9", 4), new SymbolGID("RTS-12.19", 4));
		SecurityParams entityMock = control.createMock(SecurityParams.class);
		secParamsMock.lock();
		expect(secParamsMock.getOrCreate(new SymbolGID("RTS-12.19", 4))).andReturn(entityMock);
		entityMock.consume(duMock);
		secParamsMock.unlock();
		control.replay();

		service.updateSecurityParamsP(new TQStateUpdate<>(new TQSecID1("RIZ9", 4), duMock));
		
		control.verify();
	}
	
	@Test
	public void testUpdateSecurityBoardParams_ThrowsIfNoBoard() {
		service = new TQDirectory(ckinds, markets, boards, secParams, secBoardParamsMock, gidMap);
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Board not found: FUT");
		control.replay();
		
		service.updateSecurityBoardParams(new TQStateUpdate<>(new TQSecID2("RIZ9", "FUT"), duMock));
	}
	
	@Test
	public void testUpdateSecurityBoardParams_ThrowsIfNoSymbolGID() {
		service = new TQDirectory(ckinds, markets, boards, secParams, secBoardParamsMock, gidMap);
		boards.getOrCreate("FUT").consume(new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "FUT")
				.withToken(FBoard.MARKET_ID, 4)
				.withToken(FBoard.NAME, "FORTS-Futures")
				.withToken(FBoard.TYPE, 0)
				.buildUpdate()
			);
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Symbol GID not found: TQSecID1[secCode=RIZ9,marketID=4]");
		control.replay();
		
		service.updateSecurityBoardParams(new TQStateUpdate<>(new TQSecID2("RIZ9", "FUT"), duMock));
	}
	
	@Test
	public void testUpdateSecurityBoardParams() {
		service = new TQDirectory(ckinds, markets, boards, secParams, secBoardParamsMock, gidMap);
		boards.getOrCreate("FUT").consume(new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "FUT")
				.withToken(FBoard.MARKET_ID, 4)
				.withToken(FBoard.NAME, "FORTS-Futures")
				.withToken(FBoard.TYPE, 0)
				.buildUpdate()
			);
		gidMap.put(new TQSecID1("RIZ9", 4), new SymbolGID("RTS-12.19", 4));
		SecurityBoardParams entityMock = control.createMock(SecurityBoardParams.class);
		expect(secBoardParamsMock.getOrCreate(new SymbolTID("RTS-12.19", 4, "FUT"))).andReturn(entityMock);
		entityMock.consume(duMock);
		control.replay();
		
		service.updateSecurityBoardParams(new TQStateUpdate<>(new TQSecID2("RIZ9", "FUT"), duMock));
		
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
	public void testIsExistsSecurityParams() {
		service = new TQDirectory(ckinds, markets, boards, secParams, secBoardParams, gidMap);
		gidMap.put(new TQSecID1("RIZ9", 4),  new SymbolGID("RTS-12.19", 4));
		control.replay();
		
		assertTrue(service.isExistsSecurityParams(new TQSecID1("RIZ9", 4)));
		assertFalse(service.isExistsSecurityParams(new TQSecID1("SiZ9", 4)));
		
		control.verify();
	}
	
	@Test
	public void testGetSecurityParams() {
		service = new TQDirectory(ckinds, markets, boards, secParams, secBoardParams, gidMap);
		service.updateSecurityParamsF(new TQStateUpdate<>(
				new TQSecID_F("RIZ9", 4, "FUT", "RTS-12.19", SecType.FUT),
				new DeltaUpdateBuilder()
					.withToken(FSecurity.SECCODE, "RIZ9")
					.withToken(FSecurity.DEFAULT_BOARDCODE, "FUT")
					.withToken(FSecurity.SHORT_NAME, "RTS-12.19")
					.buildUpdate())
			);
		control.replay();
		
		SecurityParams actual = service.getSecurityParams(new TQSecID1("RIZ9", 4));
		
		control.verify();
		assertNotNull(actual);
		assertEquals("RIZ9", actual.getSecCode());
		assertEquals("FUT", actual.getDefaultBoard());
		assertEquals("RTS-12.19", actual.getShortName());
	}
	
	@Test
	public void testIsExistsSecurityBoardParams() {
		service = new TQDirectory(ckinds, markets, boards, secParams, secBoardParams, gidMap);
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
				new TQSecID_F("RIZ9", 4, "FUT", "RTS-12.19", SecType.FUT),
				new DeltaUpdateBuilder()
					.withToken(FSecurity.SECCODE, "RIZ9")
					.withToken(FSecurity.DEFAULT_BOARDCODE, "FUT")
					.withToken(FSecurity.SHORT_NAME, "RTS-12.19")
					.buildUpdate())
			);
		service.updateSecurityBoardParams(new TQStateUpdate<>(new TQSecID2("RIZ9", "FUT"), new DeltaUpdateBuilder()
				.withToken(FSecurityBoard.SECCODE, "marker value")
				.buildUpdate()
			));
		control.replay();
		
		assertTrue(service.isExistsSecurityBoardParams(new TQSecID2("RIZ9", "FUT")));
		assertFalse(service.isExistsSecurityBoardParams(new TQSecID2("Eu63500BF9", "OPT")));
		assertFalse(service.isExistsSecurityBoardParams(new TQSecID2("GAZP", "EQTB")));
		
		control.verify();
	}
	
	@Test
	public void testGetSecurityBoardParams() {
		service = new TQDirectory(ckinds, markets, boards, secParams, secBoardParams, gidMap);
		service.updateBoard(new TQStateUpdate<>("FUT", new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "FUT")
				.withToken(FBoard.MARKET_ID, 4)
				.withToken(FBoard.NAME, "FORTS-Futures")
				.withToken(FBoard.TYPE, 0)
				.buildUpdate()
			));
		gidMap.put(new TQSecID1("RIZ9", 4), new SymbolGID("RTS-12.19", 4));
		SecurityBoardParams x = secBoardParams.getOrCreate(new SymbolTID("RTS-12.19", 4, "FUT"));
		control.replay();
		
		SecurityBoardParams actual = service.getSecurityBoardParams(new TQSecID2("RIZ9", "FUT"));
		
		control.verify();
		assertSame(x, actual);
	}

}

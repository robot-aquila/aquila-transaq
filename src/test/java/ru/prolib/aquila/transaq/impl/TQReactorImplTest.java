package ru.prolib.aquila.transaq.impl;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.transaq.entity.Board;
import ru.prolib.aquila.transaq.entity.CandleKind;
import ru.prolib.aquila.transaq.entity.Market;

public class TQReactorImplTest {
	private IMocksControl control;
	private TQDirectory dirMock;
	private TQSecurityHandlerRegistry shrMock;
	private TQSecurityHandlerFactory shfMock;
	private TQSecurityHandler shMock1;
	private TQReactorImpl service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dirMock = control.createMock(TQDirectory.class);
		shrMock = control.createMock(TQSecurityHandlerRegistry.class);
		shfMock = control.createMock(TQSecurityHandlerFactory.class);
		shMock1 = control.createMock(TQSecurityHandler.class);
		service = new TQReactorImpl(dirMock, shrMock, shfMock);
	}
	
	@Test
	public void testUpdateMarkets() {
		List<Market> markets = new ArrayList<>();
		markets.add(control.createMock(Market.class));
		markets.add(control.createMock(Market.class));
		markets.add(control.createMock(Market.class));
		dirMock.updateMarkets(markets);
		control.replay();
		
		service.updateMarkets(markets);
		
		control.verify();
	}
	
	@Test
	public void testUpdateBoards() {
		List<Board> boards = new ArrayList<>();
		boards.add(control.createMock(Board.class));
		boards.add(control.createMock(Board.class));
		boards.add(control.createMock(Board.class));
		dirMock.updateBoards(boards);
		control.replay();
		
		service.updateBoards(boards);
		
		control.verify();
	}
	
	@Test
	public void testUpdateCandleKinds() {
		List<CandleKind> candle_kinds = new ArrayList<>();
		candle_kinds.add(control.createMock(CandleKind.class));
		candle_kinds.add(control.createMock(CandleKind.class));
		candle_kinds.add(control.createMock(CandleKind.class));
		dirMock.updateCandleKinds(candle_kinds);
		control.replay();
		
		service.updateCandleKinds(candle_kinds);
		
		control.verify();
	}
	
	@Test
	public void testUpdateSecurity_U1() {
		TQSecID1 sec_id = new TQSecID1("foo", 26);
		DeltaUpdate duMock = control.createMock(DeltaUpdate.class);
		TQSecurityUpdate1 update = new TQSecurityUpdate1(sec_id, duMock);
		expect(shrMock.getHandler(sec_id)).andReturn(shMock1);
		shMock1.update(duMock);
		control.replay();
		
		service.updateSecurity(update);
		
		control.verify();
	}

	@Test
	public void testUpdateSecurity_U3_NewHandler() {
		TQSecID3 sec_id3 = new TQSecID3("foo", 7, "bar");
		DeltaUpdate duMock = control.createMock(DeltaUpdate.class);
		TQSecurityUpdate3 update = new TQSecurityUpdate3(sec_id3, duMock);
		expect(shrMock.getHandlerOrNull(sec_id3)).andReturn(null);
		expect(shfMock.createHandler(sec_id3)).andReturn(shMock1);
		shMock1.initialUpdate(duMock);
		shrMock.registerHandler(shMock1);
		control.replay();
		
		service.updateSecurity(update);
		
		control.verify();
	}

	@Test
	public void testUpdateSecurity_U3_ExistingHandler() {
		TQSecID3 sec_id3 = new TQSecID3("buz", 8, "bar");
		DeltaUpdate duMock = control.createMock(DeltaUpdate.class);
		TQSecurityUpdate3 update = new TQSecurityUpdate3(sec_id3, duMock);
		expect(shrMock.getHandlerOrNull(sec_id3)).andReturn(shMock1);
		shMock1.update(duMock);
		control.replay();
		
		service.updateSecurity(update);
		
		control.verify();
	}

}

package ru.prolib.aquila.transaq.impl;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.transaq.entity.Board;
import ru.prolib.aquila.transaq.entity.CKind;
import ru.prolib.aquila.transaq.entity.Market;

public class TQDirectoryTest {
	private IMocksControl control;
	private OSCRepository<Integer, CKind> ckindsMock;
	private OSCRepository<Integer, Market> marketsMock;
	private OSCRepository<String, Board> boardsMock;
	private DeltaUpdate duMock;
	private TQDirectory service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		boardsMock = control.createMock(OSCRepository.class);
		marketsMock = control.createMock(OSCRepository.class);
		ckindsMock = control.createMock(OSCRepository.class);
		duMock = control.createMock(DeltaUpdate.class);
		service = new TQDirectory(ckindsMock, marketsMock, boardsMock);
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
	public void testGetMarketName() {
		Market marketMock = control.createMock(Market.class);
		expect(marketsMock.getOrThrow(71)).andReturn(marketMock);
		expect(marketMock.getName()).andReturn("zulu24");
		control.replay();
		
		assertEquals("zulu24", service.getMarketName(71));
		
		control.verify();
	}
	
	@Test
	public void testGetMarketRepository() {
		assertSame(marketsMock, service.getMarketRepository());
	}
	
	@Test
	public void testGetCKindRepository() {
		assertSame(ckindsMock, service.getCKindRepository());
	}
	
	@Test
	public void testGetBoardRepository() {
		assertSame(boardsMock, service.getBoardRepository());
	}

}

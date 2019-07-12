package ru.prolib.aquila.transaq.impl;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.entity.Board;
import ru.prolib.aquila.transaq.entity.CandleKind;
import ru.prolib.aquila.transaq.entity.Market;

public class TQDirectoryTest {
	private IMocksControl control;
	private List<Board> boards_stub;
	private List<Market> markets_stub;
	private List<CandleKind> candle_kinds_stub;
	private TQDirectory service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		boards_stub = new ArrayList<>();
		markets_stub = new ArrayList<>();
		candle_kinds_stub = new ArrayList<>();
		service = new TQDirectory(boards_stub, markets_stub, candle_kinds_stub);
	}
	
	@Test
	public void testUpdateBoards() {
		Board
			bMock1 = control.createMock(Board.class),
			bMock2 = control.createMock(Board.class),
			bMock3 = control.createMock(Board.class),
			bMock4 = control.createMock(Board.class),
			bMock5 = control.createMock(Board.class),
			bMock6 = control.createMock(Board.class),
			bMock7 = control.createMock(Board.class);
		boards_stub.add(bMock1);
		boards_stub.add(bMock2);
		boards_stub.add(bMock3);
		
		List<Board> expected = new ArrayList<>();
		expected.add(bMock4);
		expected.add(bMock5);
		expected.add(bMock6);
		expected.add(bMock7);
		service.updateBoards(expected);
			
		assertEquals(expected, boards_stub);
	}
	
	@Test
	public void testUpdateMarkets() {
		Market
			mMock1 = control.createMock(Market.class),
			mMock2 = control.createMock(Market.class),
			mMock3 = control.createMock(Market.class),
			mMock4 = control.createMock(Market.class),
			mMock5 = control.createMock(Market.class),
			mMock6 = control.createMock(Market.class),
			mMock7 = control.createMock(Market.class);
		markets_stub.add(mMock7);
		markets_stub.add(mMock6);
		markets_stub.add(mMock5);
		
		List<Market> expected = new ArrayList<>();
		expected.add(mMock4);
		expected.add(mMock3);
		expected.add(mMock2);
		expected.add(mMock1);
		service.updateMarkets(expected);
		
		assertEquals(expected, markets_stub);
	}

	@Test
	public void testUpdateCandleKinds() {
		CandleKind
			kMock1 = control.createMock(CandleKind.class),
			kMock2 = control.createMock(CandleKind.class),
			kMock3 = control.createMock(CandleKind.class),
			kMock4 = control.createMock(CandleKind.class),
			kMock5 = control.createMock(CandleKind.class),
			kMock6 = control.createMock(CandleKind.class);
		candle_kinds_stub.add(kMock4);
		candle_kinds_stub.add(kMock5);
		candle_kinds_stub.add(kMock6);
		
		List<CandleKind> expected = new ArrayList<>();
		expected.add(kMock1);
		expected.add(kMock2);
		expected.add(kMock3);
		service.updateCandleKinds(expected);
		
		assertEquals(expected, candle_kinds_stub);
	}
	
	@Test
	public void testGetMarketName() {
		markets_stub.add(new Market(0, "foo"));
		markets_stub.add(new Market(1, "bar"));
		markets_stub.add(new Market(2, "zoo"));
		
		assertEquals("foo", service.getMarketName(0));
		assertEquals("bar", service.getMarketName(1));
		assertEquals("zoo", service.getMarketName(2));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testGetMarketName_ThrowsIfNotFound() {
		markets_stub.add(new Market(0, "foo"));
		markets_stub.add(new Market(1, "bar"));
		markets_stub.add(new Market(2, "zoo"));

		service.getMarketName(3);
	}

}

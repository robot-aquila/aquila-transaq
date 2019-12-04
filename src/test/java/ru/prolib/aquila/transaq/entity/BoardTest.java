package ru.prolib.aquila.transaq.entity;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCControllerStub;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.transaq.remote.MessageFields.FBoard;

public class BoardTest {
	private IMocksControl control;
	private OSCRepository<String, Board> repoMock;
	private EventQueue queueMock;
	private Board service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		repoMock = control.createMock(OSCRepository.class);
		queueMock = control.createMock(EventQueue.class);
		service = new BoardFactory(queueMock).produce(repoMock, "AUCT");
	}
	
	@Test
	public void testCtor4() {
		service.consume(new DeltaUpdateBuilder()
				.withToken(FBoard.CODE, "AUCT")
				.withToken(FBoard.NAME, "Auction")
				.withToken(FBoard.MARKET_ID, 1)
				.withToken(FBoard.TYPE, 2)
				.buildUpdate()
			);
		
		assertEquals("AUCT", service.getCode());
		assertEquals("Auction", service.getName());
		assertEquals(1, service.getMarketID());
		assertEquals(2, service.getTypeID());
		
		assertEquals("Board#AUCT", service.getContainerID());
		assertEquals(OSCControllerStub.class, service.getController().getClass());
	}

}

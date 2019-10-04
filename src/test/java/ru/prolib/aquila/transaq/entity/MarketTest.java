package ru.prolib.aquila.transaq.entity;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCControllerStub;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.transaq.impl.TQMarketField;

public class MarketTest {
	private IMocksControl control;
	private OSCRepository<Integer, Market> repoMock;
	private EventQueue queueMock;
	private Market service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		repoMock = control.createMock(OSCRepository.class);
		queueMock = control.createMock(EventQueue.class);
		service = new MarketFactory(queueMock).produce(repoMock, 215);
	}

	@Test
	public void testCtor2() {
		service.consume(new DeltaUpdateBuilder()
				.withToken(TQMarketField.ID, 215)
				.withToken(TQMarketField.NAME, "foobar")
				.buildUpdate()
			);
		
		assertEquals(215, service.getID());
		assertEquals("foobar", service.getName());
		
		assertEquals("Market#215", service.getContainerID());
		assertEquals(OSCControllerStub.class, service.getController().getClass());
	}

}

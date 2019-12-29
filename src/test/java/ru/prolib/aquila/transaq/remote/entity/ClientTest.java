package ru.prolib.aquila.transaq.remote.entity;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.transaq.remote.MessageFields.FClient;

public class ClientTest {
	private IMocksControl control;
	private OSCRepository<String, Client> repoMock;
	private EventQueue queueMock;

	private Client service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		repoMock = control.createMock(OSCRepository.class);
		queueMock = control.createMock(EventQueue.class);
		service = new ClientFactory(queueMock).produce(repoMock, "XXX-1234");
	}

	@Test
	public void testGetters() {
		service.consume(new DeltaUpdateBuilder()
				.withToken(FClient.ID, "XXX-2000")
				.withToken(FClient.REMOVE, false)
				.withToken(FClient.TYPE, "mct")
				.withToken(FClient.CURRENCY, "USD")
				.withToken(FClient.MARKET_ID, 14)
				.withToken(FClient.UNION_CODE, "XXX-UN")
				.withToken(FClient.FORTS_ACCOUNT, "fut25692")
				.buildUpdate());
		
		assertEquals("XXX-2000", service.getID());
		assertEquals(false, service.getRemove());
		assertEquals("mct", service.getType());
		assertEquals("USD", service.getCurrencyCode());
		assertEquals(Integer.valueOf(14), service.getMarketID());
		assertEquals("XXX-UN", service.getUnionCode());
		assertEquals("fut25692", service.getFortsAccount());
	}

}

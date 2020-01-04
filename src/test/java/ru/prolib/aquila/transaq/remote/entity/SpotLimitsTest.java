package ru.prolib.aquila.transaq.remote.entity;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.of;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.transaq.remote.ID;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FSpotLimits;

public class SpotLimitsTest {
	
	static List<Integer> toList(Integer... integers) {
		List<Integer> list = new ArrayList<>();
		for ( Integer i : integers ) {
			list.add(i);
		}
		return list;
	}
	
	private IMocksControl control;
	private OSCRepository<ID.SL, SpotLimits> repoMock;
	private EventQueue queueMock;

	private SpotLimits service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		repoMock = control.createMock(OSCRepository.class);
		queueMock = control.createMock(EventQueue.class);
		service = new SpotLimitsFactory(queueMock).produce(repoMock, new ID.SL("foo", new HashSet<>(toList(5, 8))));
	}

	@Test
	public void testGetters() {
		service.consume(new DeltaUpdateBuilder()
				.withToken(FSpotLimits.CLIENT_ID, "COOKIE")
				.withToken(FSpotLimits.UNION_CODE, "UNITED-COOKIE")
				.withToken(FSpotLimits.MARKETS, toList(14, 15))
				.withToken(FSpotLimits.SHORT_NAME, "does not matter (part 3)")
				.withToken(FSpotLimits.BUY_LIMIT, of("751.2"))
				.withToken(FSpotLimits.BUY_LIMIT_USED, of("1.54"))
				.buildUpdate());
		
		assertEquals("SpotLimits#ID.SL[clientID=foo,markets=[5, 8]]", service.getContainerID());
		assertEquals("COOKIE", service.getClientID());
		assertEquals("UNITED-COOKIE", service.getUnionCode());
		assertEquals(toList(14, 15), service.getMarkets());
		assertEquals("does not matter (part 3)", service.getShortName());
		assertEquals(of("751.2"), service.getBuyLimit());
		assertEquals(of("1.54"), service.getBuyLimitUsed());
	}

}

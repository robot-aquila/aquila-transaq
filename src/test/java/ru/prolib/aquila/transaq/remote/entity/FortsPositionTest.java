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
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FFortsPosition;

public class FortsPositionTest {
	
	static List<Integer> toList(Integer... integers) {
		List<Integer> list = new ArrayList<>();
		for ( Integer i : integers ) {
			list.add(i);
		}
		return list;
	}
	
	private IMocksControl control;
	private OSCRepository<ID.FP, FortsPosition> repoMock;
	private EventQueue queueMock;

	private FortsPosition service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		repoMock = control.createMock(OSCRepository.class);
		queueMock = control.createMock(EventQueue.class);
		service = new FortsPositionFactory(queueMock).produce(repoMock, new ID.FP("foo", "bar", new HashSet<>(toList(1))));
	}

	@Test
	public void testGetters() {
		service.consume(new DeltaUpdateBuilder()
				.withToken(FFortsPosition.SEC_ID, 44412)
				.withToken(FFortsPosition.MARKETS, toList(4, 8))
				.withToken(FFortsPosition.SEC_CODE, "RIH0")
				.withToken(FFortsPosition.CLIENT_ID, "COOKIE")
				.withToken(FFortsPosition.UNION_CODE, "UNITED-COOKIE")
				.withToken(FFortsPosition.START_NET, of(15L))
				.withToken(FFortsPosition.OPEN_BUYS, of(10L))
				.withToken(FFortsPosition.OPEN_SELLS, of(4L))
				.withToken(FFortsPosition.TOTAL_NET, of(84L))
				.withToken(FFortsPosition.TODAY_BUY, of(27L))
				.withToken(FFortsPosition.TODAY_SELL, of(19L))
				.withToken(FFortsPosition.OPT_MARGIN, of("115.23"))
				.withToken(FFortsPosition.VAR_MARGIN, of("295.1"))
				.withToken(FFortsPosition.EXPIRATION_POS, of(77681L))
				.withToken(FFortsPosition.USED_SELL_SPOT_LIMIT, of("54.26"))
				.withToken(FFortsPosition.SELL_SPOT_LIMIT, of("200.0"))
				.withToken(FFortsPosition.NETTO, of("2.15"))
				.withToken(FFortsPosition.KGO, of("0.05"))
				.buildUpdate());
		
		assertEquals("FortsPosition#ID.FP[clientID=foo,secCode=bar,markets=[1]]", service.getContainerID());
		assertEquals(Integer.valueOf(44412), service.getSecID());
		assertEquals(toList(4, 8), service.getMarkets());
		assertEquals("RIH0", service.getSecCode());
		assertEquals("COOKIE", service.getClientID());
		assertEquals("UNITED-COOKIE", service.getUnionCode());
		assertEquals(of(15L), service.getStartNet());
		assertEquals(of(10L), service.getOpenBuys());
		assertEquals(of(4L), service.getOpenSells());
		assertEquals(of(84L), service.getTotalNet());
		assertEquals(of(27L), service.getTodayBuy());
		assertEquals(of(19L), service.getTodaySell());
		assertEquals(of("115.23"), service.getOptMargin());
		assertEquals(of("295.1"), service.getVarMargin());
		assertEquals(of(77681L), service.getExpirationPos());
		assertEquals(of("54.26"), service.getUsedSellSpotLimit());
		assertEquals(of("200.0"), service.getSellSpotLimit());
		assertEquals(of("2.15"), service.getNetto());
		assertEquals(of("0.05"), service.getKgo());
	}

}

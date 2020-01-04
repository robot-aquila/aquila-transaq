package ru.prolib.aquila.transaq.remote.entity;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.util.ArrayList;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.transaq.remote.ID;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FFortsMoney;

public class FortsMoneyTest {
	
	static List<Integer> toList(Integer... integers) {
		List<Integer> list = new ArrayList<>();
		for ( Integer i : integers ) {
			list.add(i);
		}
		return list;
	}
	
	private IMocksControl control;
	private OSCRepository<ID.FM, FortsMoney> repoMock;
	private EventQueue queueMock;

	private FortsMoney service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		repoMock = control.createMock(OSCRepository.class);
		queueMock = control.createMock(EventQueue.class);
		service = new FortsMoneyFactory(queueMock).produce(repoMock, new ID.FM("CHOO-CHOO"));
	}

	@Test
	public void testGetters() {
		service.consume(new DeltaUpdateBuilder()
				.withToken(FFortsMoney.CLIENT_ID, "COOKIE")
				.withToken(FFortsMoney.UNION_CODE, "UNITED-COOKIE")
				.withToken(FFortsMoney.MARKETS, toList(4, 6))
				.withToken(FFortsMoney.SHORT_NAME, "does not matter (part 1)")
				.withToken(FFortsMoney.CURRENT, of("280.8"))
				.withToken(FFortsMoney.BLOCKED, of("54.23"))
				.withToken(FFortsMoney.FREE, of("97.14"))
				.withToken(FFortsMoney.VAR_MARGIN, of("7176.2"))
				.buildUpdate());
		
		assertEquals("FortsMoney#ID.FM[clientID=CHOO-CHOO]", service.getContainerID());
		assertEquals("COOKIE", service.getClientID());
		assertEquals("UNITED-COOKIE", service.getUnionCode());
		assertEquals(toList(4, 6), service.getMarkets());
		assertEquals("does not matter (part 1)", service.getShortName());
		assertEquals(of("280.8"), service.getCurrent());
		assertEquals(of("54.23"), service.getBlocked());
		assertEquals(of("97.14"), service.getFree());
		assertEquals(of("7176.2"), service.getVarMargin());
	}

}

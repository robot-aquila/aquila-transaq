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
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FFortsCollaterals;

public class FortsCollateralsTest {
	
	static List<Integer> toList(Integer... integers) {
		List<Integer> list = new ArrayList<>();
		for ( Integer i : integers ) {
			list.add(i);
		}
		return list;
	}
	
	private IMocksControl control;
	private OSCRepository<ID.FC, FortsCollaterals> repoMock;
	private EventQueue queueMock;

	private FortsCollaterals service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		repoMock = control.createMock(OSCRepository.class);
		queueMock = control.createMock(EventQueue.class);
		service = new FortsCollateralsFactory(queueMock).produce(repoMock, new ID.FC("foo", new HashSet<>(toList(1, 5))));
	}

	@Test
	public void testGetters() {
		service.consume(new DeltaUpdateBuilder()
				.withToken(FFortsCollaterals.CLIENT_ID, "COOKIE")
				.withToken(FFortsCollaterals.UNION_CODE, "UNITED-COOKIE")
				.withToken(FFortsCollaterals.MARKETS, toList(9, 3))
				.withToken(FFortsCollaterals.SHORT_NAME, "does not matter (part 2)")
				.withToken(FFortsCollaterals.CURRENT, of("761.2"))
				.withToken(FFortsCollaterals.BLOCKED, of("872.1"))
				.withToken(FFortsCollaterals.FREE, of("581.72"))
				.buildUpdate());

		assertEquals("FortsCollaterals#ID.FC[clientID=foo,markets=[1, 5]]", service.getContainerID());
		assertEquals("COOKIE", service.getClientID());
		assertEquals("UNITED-COOKIE", service.getUnionCode());
		assertEquals(toList(9, 3), service.getMarkets());
		assertEquals("does not matter (part 2)", service.getShortName());
		assertEquals(of("761.2"), service.getCurrent());
		assertEquals(of("872.1"), service.getBlocked());
		assertEquals(of("581.72"), service.getFree());
	}

}

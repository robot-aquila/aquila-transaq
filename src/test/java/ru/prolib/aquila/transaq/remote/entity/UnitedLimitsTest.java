package ru.prolib.aquila.transaq.remote.entity;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.of;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.transaq.remote.ID;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FUnitedLimits;

public class UnitedLimitsTest {
	private IMocksControl control;
	private OSCRepository<ID.UL, UnitedLimits> repoMock;
	private EventQueue queueMock;

	private UnitedLimits service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		repoMock = control.createMock(OSCRepository.class);
		queueMock = control.createMock(EventQueue.class);
		service = new UnitedLimitsFactory(queueMock).produce(repoMock, new ID.UL("gazzy"));
	}

	@Test
	public void testGetters() {
		service.consume(new DeltaUpdateBuilder()
				.withToken(FUnitedLimits.UNION_CODE, "KASUM-1234")
				.withToken(FUnitedLimits.OPEN_EQUITY, of("990196.70"))
				.withToken(FUnitedLimits.EQUITY, of("12651.61"))
				.withToken(FUnitedLimits.REQUIREMENTS, of("8135.1"))
				.withToken(FUnitedLimits.FREE, of("33561.12"))
				.withToken(FUnitedLimits.VAR_MARGIN, of("150.00"))
				.withToken(FUnitedLimits.FIN_RES, of("245416.89"))
				.withToken(FUnitedLimits.GO, of("0.02"))
				.buildUpdate());
		
		assertEquals("UnitedLimits#ID.UL[unionCode=gazzy]", service.getContainerID());
		assertEquals("KASUM-1234", service.getUnionCode());
		assertEquals(of("990196.70"), service.getOpenEquity());
		assertEquals(of("12651.61"), service.getEquity());
		assertEquals(of("8135.1"), service.getRequirements());
		assertEquals(of("33561.12"), service.getFree());
		assertEquals(of("150.00"), service.getVarMargin());
		assertEquals(of("245416.89"), service.getFinRes());
		assertEquals(of("0.02"), service.getGo());
	}

}

package ru.prolib.aquila.transaq.remote.entity;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.transaq.remote.ID;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FSecPosition;

public class SecPositionTest {
	private IMocksControl control;
	private OSCRepository<ID.SP, SecPosition> repoMock;
	private EventQueue queueMock;

	private SecPosition service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		repoMock = control.createMock(OSCRepository.class);
		queueMock = control.createMock(EventQueue.class);
		service = new SecPositionFactory(queueMock).produce(repoMock, new ID.SP("KOBA", "RIH0", 4, "T0"));
	}

	@Test
	public void testGetters() {
		service.consume(new DeltaUpdateBuilder()
				.withToken(FSecPosition.SEC_ID, 52013)
				.withToken(FSecPosition.MARKET_ID, 1)
				.withToken(FSecPosition.SEC_CODE, "GAZP")
				.withToken(FSecPosition.REGISTER, "T0")
				.withToken(FSecPosition.CLIENT_ID, "COOKIE")
				.withToken(FSecPosition.UNION_CODE, "UNITED-COOKIE")
				.withToken(FSecPosition.SHORT_NAME, "Газпром ОА")
				.withToken(FSecPosition.SALDO_IN, of(5L))
				.withToken(FSecPosition.SALDO_MIN, of(1L))
				.withToken(FSecPosition.BOUGHT, of(3L))
				.withToken(FSecPosition.SOLD, of(9L))
				.withToken(FSecPosition.SALDO, of(4L))
				.withToken(FSecPosition.ORD_BUY, of(7L))
				.withToken(FSecPosition.ORD_SELL, of(8L))
				.withToken(FSecPosition.AMOUNT, of("205.12"))
				.withToken(FSecPosition.EQUITY, of("46.24"))
				.buildUpdate());
		
		assertEquals("SecPosition#ID.SP[clientID=KOBA,secCode=RIH0,marketID=4,register=T0]", service.getContainerID());
		assertEquals(Integer.valueOf(52013), service.getSecID());
		assertEquals(Integer.valueOf(1), service.getMarketID());
		assertEquals("GAZP", service.getSecCode());
		assertEquals("T0", service.getRegister());
		assertEquals("COOKIE", service.getClientID());
		assertEquals("UNITED-COOKIE", service.getUnionCode());
		assertEquals("Газпром ОА", service.getShortName());
		assertEquals(of(5L), service.getSaldoIn());
		assertEquals(of(1L), service.getSaldoMin());
		assertEquals(of(3L), service.getBought());
		assertEquals(of(9L), service.getSold());
		assertEquals(of(4L), service.getSaldo());
		assertEquals(of(7L), service.getOrdBuy());
		assertEquals(of(8L), service.getOrdSell());
		assertEquals(of("205.12"), service.getAmount());
		assertEquals(of("46.24"), service.getEquity());
	}

}

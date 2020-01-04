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
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FMoneyPosition;

public class MoneyPositionTest {
	
	static List<Integer> toList(Integer... integers) {
		List<Integer> list = new ArrayList<>();
		for ( Integer i : integers ) {
			list.add(i);
		}
		return list;
	}
	
	private IMocksControl control;
	private OSCRepository<ID.MP, MoneyPosition> repoMock;
	private EventQueue queueMock;

	private MoneyPosition service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		repoMock = control.createMock(OSCRepository.class);
		queueMock = control.createMock(EventQueue.class);
		service = new MoneyPositionFactory(queueMock).produce(repoMock, new ID.MP("COOKIE", "CASH", "T0"));
	}

	@Test
	public void testGetters() {
		service.consume(new DeltaUpdateBuilder()
			.withToken(FMoneyPosition.MARKETS, toList(1, 5, 8))
			.withToken(FMoneyPosition.REGISTER, "T0")
			.withToken(FMoneyPosition.ASSET, "MICEX_FUND")
			.withToken(FMoneyPosition.CLIENT_ID, "KABAYASI")
			.withToken(FMoneyPosition.UNION_CODE, "UK")
			.withToken(FMoneyPosition.SHORT_NAME, "money position")
			.withToken(FMoneyPosition.SALDO_IN, of("726.1"))
			.withToken(FMoneyPosition.BOUGHT, of("886.5"))
			.withToken(FMoneyPosition.SOLD, of("134.12"))
			.withToken(FMoneyPosition.SALDO, of("91.92"))
			.withToken(FMoneyPosition.ORD_BUY, of("23.98"))
			.withToken(FMoneyPosition.ORB_BUY_COND, of("643.4"))
			.withToken(FMoneyPosition.COMISSION, of("85.24"))
			.buildUpdate());
		
		assertEquals("MoneyPosition#ID.MP[clientID=COOKIE,asset=CASH,register=T0]", service.getContainerID());
		assertEquals(toList(1, 5, 8), service.getMarkets());
		assertEquals("T0", service.getRegister());
		assertEquals("MICEX_FUND", service.getAsset());
		assertEquals("KABAYASI", service.getClientID());
		assertEquals("UK", service.getUnionCode());
		assertEquals("money position", service.getShortName());
		assertEquals(of("726.1"), service.getSaldoIn());
		assertEquals(of("886.5"), service.getBought());
		assertEquals(of("134.12"), service.getSold());
		assertEquals(of("91.92"), service.getSaldo());
		assertEquals(of("23.98"), service.getOrdBuy());
		assertEquals(of("643.4"), service.getOrdBuyCond());
		assertEquals(of("85.24"), service.getComission());
	}

}

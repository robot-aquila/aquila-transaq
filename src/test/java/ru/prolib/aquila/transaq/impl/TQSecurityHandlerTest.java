package ru.prolib.aquila.transaq.impl;

import org.easymock.IAnswer;
import org.easymock.IMocksControl;

import static org.easymock.EasyMock.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainer;
import ru.prolib.aquila.transaq.entity.SecType;
import ru.prolib.aquila.transaq.remote.TQSecIDF;

public class TQSecurityHandlerTest {
	private IMocksControl control;
	private DeltaUpdateConsumer consMock;
	private UpdatableStateContainer stateMock;
	private TQFieldAssembler asmMock;
	private DeltaUpdate updateMock;
	private TQSecurityHandler service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		consMock = control.createMock(DeltaUpdateConsumer.class);
		stateMock = control.createMock(UpdatableStateContainer.class);
		asmMock = control.createMock(TQFieldAssembler.class);
		updateMock = control.createMock(DeltaUpdate.class);
		service = new TQSecurityHandler(
				new TQSecIDF("foo", 2, "FORTS", "bar", SecType.BOND),
				new Symbol("F:RTS-6.19@FORTS:RUB"),
				consMock,
				stateMock, 
				asmMock
			);
	}
	
	@Test
	public void testUpdate_NoChanges() {
		stateMock.lock();
		stateMock.consume(updateMock);
		expect(stateMock.hasChanged()).andReturn(false);
		stateMock.unlock();
		control.replay();
		
		service.update(updateMock);
		
		control.verify();
	}
	
	static class MyAnswer implements IAnswer<Integer> {
		private final int retval;
		private final int token_id;
		private final Object token_val;
		
		public MyAnswer(int retval, int token_id, Object token_val) {
			this.retval = retval;
			this.token_id = token_id;
			this.token_val = token_val;
		}

		@Override
		public Integer answer() throws Throwable {
			((DeltaUpdateBuilder)getCurrentArguments()[1]).withToken(token_id, token_val);
			return retval;
		}
		
	}
	
	@Test
	public void testUpdate_NoUpdatedFields() {
		stateMock.lock();
		stateMock.consume(updateMock);
		expect(stateMock.hasChanged()).andReturn(true);
		expect(asmMock.toSecDisplayName(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLotSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickValue(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecInitialMargin(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecSettlementPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowerPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecUpperPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecOpenPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecHighPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecClosePrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecExpirationTime(same(stateMock), anyObject())).andReturn(0);
		stateMock.unlock();
		control.replay();
		
		service.update(updateMock);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_UpdatedDisplayName() {
		stateMock.lock();
		stateMock.consume(updateMock);
		expect(stateMock.hasChanged()).andReturn(true);
		expect(asmMock.toSecDisplayName(same(stateMock), anyObject())).andAnswer(new MyAnswer(1, 555, "yes"));
		expect(asmMock.toSecLotSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickValue(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecInitialMargin(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecSettlementPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowerPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecUpperPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecOpenPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecHighPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecClosePrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecExpirationTime(same(stateMock), anyObject())).andAnswer(new MyAnswer(0, 666, "baz"));
		stateMock.unlock();
		consMock.consume(new DeltaUpdateBuilder()
				.withToken(555, "yes")
				.withToken(666, "baz")
				.buildUpdate()
			);
		control.replay();
		
		service.update(updateMock);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_UpdatedLotSize() {
		stateMock.lock();
		stateMock.consume(updateMock);
		expect(stateMock.hasChanged()).andReturn(true);
		expect(asmMock.toSecDisplayName(same(stateMock), anyObject())).andAnswer(new MyAnswer(0, 555, "zulu"));
		expect(asmMock.toSecLotSize(same(stateMock), anyObject())).andReturn(1);
		expect(asmMock.toSecTickSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickValue(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecInitialMargin(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecSettlementPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowerPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecUpperPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecOpenPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecHighPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecClosePrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecExpirationTime(same(stateMock), anyObject())).andReturn(0);
		stateMock.unlock();
		consMock.consume(new DeltaUpdateBuilder().withToken(555, "zulu").buildUpdate());
		control.replay();
		
		service.update(updateMock);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_UpdatedTickSize() {
		stateMock.lock();
		stateMock.consume(updateMock);
		expect(stateMock.hasChanged()).andReturn(true);
		expect(asmMock.toSecDisplayName(same(stateMock), anyObject())).andAnswer(new MyAnswer(0, 555, "zulu"));
		expect(asmMock.toSecLotSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickSize(same(stateMock), anyObject())).andReturn(1);
		expect(asmMock.toSecTickValue(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecInitialMargin(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecSettlementPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowerPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecUpperPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecOpenPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecHighPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecClosePrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecExpirationTime(same(stateMock), anyObject())).andReturn(0);
		stateMock.unlock();
		consMock.consume(new DeltaUpdateBuilder().withToken(555, "zulu").buildUpdate());
		control.replay();
		
		service.update(updateMock);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_UpdatedTickValue() {
		stateMock.lock();
		stateMock.consume(updateMock);
		expect(stateMock.hasChanged()).andReturn(true);
		expect(asmMock.toSecDisplayName(same(stateMock), anyObject())).andAnswer(new MyAnswer(0, 555, "zulu"));
		expect(asmMock.toSecLotSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickValue(same(stateMock), anyObject())).andReturn(1);
		expect(asmMock.toSecInitialMargin(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecSettlementPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowerPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecUpperPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecOpenPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecHighPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecClosePrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecExpirationTime(same(stateMock), anyObject())).andReturn(0);
		stateMock.unlock();
		consMock.consume(new DeltaUpdateBuilder().withToken(555, "zulu").buildUpdate());
		control.replay();
		
		service.update(updateMock);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_UpdatedInitialMargin() {
		stateMock.lock();
		stateMock.consume(updateMock);
		expect(stateMock.hasChanged()).andReturn(true);
		expect(asmMock.toSecDisplayName(same(stateMock), anyObject())).andAnswer(new MyAnswer(0, 555, "zulu"));
		expect(asmMock.toSecLotSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickValue(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecInitialMargin(same(stateMock), anyObject())).andReturn(1);
		expect(asmMock.toSecSettlementPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowerPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecUpperPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecOpenPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecHighPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecClosePrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecExpirationTime(same(stateMock), anyObject())).andReturn(0);
		stateMock.unlock();
		consMock.consume(new DeltaUpdateBuilder().withToken(555, "zulu").buildUpdate());
		control.replay();
		
		service.update(updateMock);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_UpdatedSettlementPrice() {
		stateMock.lock();
		stateMock.consume(updateMock);
		expect(stateMock.hasChanged()).andReturn(true);
		expect(asmMock.toSecDisplayName(same(stateMock), anyObject())).andAnswer(new MyAnswer(0, 555, "zulu"));
		expect(asmMock.toSecLotSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickValue(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecInitialMargin(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecSettlementPrice(same(stateMock), anyObject())).andReturn(1);
		expect(asmMock.toSecLowerPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecUpperPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecOpenPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecHighPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecClosePrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecExpirationTime(same(stateMock), anyObject())).andReturn(0);
		stateMock.unlock();
		consMock.consume(new DeltaUpdateBuilder().withToken(555, "zulu").buildUpdate());
		control.replay();
		
		service.update(updateMock);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_UpdatedLowerPriceLimit() {
		stateMock.lock();
		stateMock.consume(updateMock);
		expect(stateMock.hasChanged()).andReturn(true);
		expect(asmMock.toSecDisplayName(same(stateMock), anyObject())).andAnswer(new MyAnswer(0, 555, "zulu"));
		expect(asmMock.toSecLotSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickValue(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecInitialMargin(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecSettlementPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowerPriceLimit(same(stateMock), anyObject())).andReturn(1);
		expect(asmMock.toSecUpperPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecOpenPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecHighPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecClosePrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecExpirationTime(same(stateMock), anyObject())).andReturn(0);
		stateMock.unlock();
		consMock.consume(new DeltaUpdateBuilder().withToken(555, "zulu").buildUpdate());
		control.replay();
		
		service.update(updateMock);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_UpdatedUpperPriceLimit() {
		stateMock.lock();
		stateMock.consume(updateMock);
		expect(stateMock.hasChanged()).andReturn(true);
		expect(asmMock.toSecDisplayName(same(stateMock), anyObject())).andAnswer(new MyAnswer(0, 555, "zulu"));
		expect(asmMock.toSecLotSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickValue(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecInitialMargin(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecSettlementPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowerPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecUpperPriceLimit(same(stateMock), anyObject())).andReturn(1);
		expect(asmMock.toSecOpenPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecHighPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecClosePrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecExpirationTime(same(stateMock), anyObject())).andReturn(0);
		stateMock.unlock();
		consMock.consume(new DeltaUpdateBuilder().withToken(555, "zulu").buildUpdate());
		control.replay();
		
		service.update(updateMock);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_UpdatedOpenPrice() {
		stateMock.lock();
		stateMock.consume(updateMock);
		expect(stateMock.hasChanged()).andReturn(true);
		expect(asmMock.toSecDisplayName(same(stateMock), anyObject())).andAnswer(new MyAnswer(0, 555, "zulu"));
		expect(asmMock.toSecLotSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickValue(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecInitialMargin(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecSettlementPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowerPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecUpperPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecOpenPrice(same(stateMock), anyObject())).andReturn(1);
		expect(asmMock.toSecHighPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecClosePrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecExpirationTime(same(stateMock), anyObject())).andReturn(0);
		stateMock.unlock();
		consMock.consume(new DeltaUpdateBuilder().withToken(555, "zulu").buildUpdate());
		control.replay();
		
		service.update(updateMock);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_UpdatedHighPrice() {
		stateMock.lock();
		stateMock.consume(updateMock);
		expect(stateMock.hasChanged()).andReturn(true);
		expect(asmMock.toSecDisplayName(same(stateMock), anyObject())).andAnswer(new MyAnswer(0, 555, "zulu"));
		expect(asmMock.toSecLotSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickValue(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecInitialMargin(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecSettlementPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowerPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecUpperPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecOpenPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecHighPrice(same(stateMock), anyObject())).andReturn(1);
		expect(asmMock.toSecLowPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecClosePrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecExpirationTime(same(stateMock), anyObject())).andReturn(0);
		stateMock.unlock();
		consMock.consume(new DeltaUpdateBuilder().withToken(555, "zulu").buildUpdate());
		control.replay();
		
		service.update(updateMock);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_UpdatedLowPrice() {
		stateMock.lock();
		stateMock.consume(updateMock);
		expect(stateMock.hasChanged()).andReturn(true);
		expect(asmMock.toSecDisplayName(same(stateMock), anyObject())).andAnswer(new MyAnswer(0, 555, "zulu"));
		expect(asmMock.toSecLotSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickValue(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecInitialMargin(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecSettlementPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowerPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecUpperPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecOpenPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecHighPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowPrice(same(stateMock), anyObject())).andReturn(1);
		expect(asmMock.toSecClosePrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecExpirationTime(same(stateMock), anyObject())).andReturn(0);
		stateMock.unlock();
		consMock.consume(new DeltaUpdateBuilder().withToken(555, "zulu").buildUpdate());
		control.replay();
		
		service.update(updateMock);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_UpdatedClosePrice() {
		stateMock.lock();
		stateMock.consume(updateMock);
		expect(stateMock.hasChanged()).andReturn(true);
		expect(asmMock.toSecDisplayName(same(stateMock), anyObject())).andAnswer(new MyAnswer(0, 555, "zulu"));
		expect(asmMock.toSecLotSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickValue(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecInitialMargin(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecSettlementPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowerPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecUpperPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecOpenPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecHighPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecClosePrice(same(stateMock), anyObject())).andReturn(1);
		expect(asmMock.toSecExpirationTime(same(stateMock), anyObject())).andReturn(0);
		stateMock.unlock();
		consMock.consume(new DeltaUpdateBuilder().withToken(555, "zulu").buildUpdate());
		control.replay();
		
		service.update(updateMock);
		
		control.verify();
	}
	
	@Test
	public void testUpdate_ExpirationTime() {
		stateMock.lock();
		stateMock.consume(updateMock);
		expect(stateMock.hasChanged()).andReturn(true);
		expect(asmMock.toSecDisplayName(same(stateMock), anyObject())).andAnswer(new MyAnswer(0, 555, "zulu"));
		expect(asmMock.toSecLotSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickSize(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecTickValue(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecInitialMargin(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecSettlementPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowerPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecUpperPriceLimit(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecOpenPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecHighPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecLowPrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecClosePrice(same(stateMock), anyObject())).andReturn(0);
		expect(asmMock.toSecExpirationTime(same(stateMock), anyObject())).andReturn(1);
		stateMock.unlock();
		consMock.consume(new DeltaUpdateBuilder().withToken(555, "zulu").buildUpdate());
		control.replay();
		
		service.update(updateMock);
		
		control.verify();
	}

}

package ru.prolib.aquila.transaq.impl;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainer;
import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainerImpl;
import ru.prolib.aquila.transaq.remote.MessageFields.FSecurity;

public class TQFieldAssemblerTest {
	private TQFieldAssembler service;
	private UpdatableStateContainer sec_state;
	private DeltaUpdateBuilder builder;

	@Before
	public void setUp() throws Exception {
		service = new TQFieldAssembler();
		sec_state = new UpdatableStateContainerImpl("X");
		builder = new DeltaUpdateBuilder();
	}

	@Test
	public void testToSecDisplayName() {
		sec_state.update(FSecurity.SHORT_NAME, "zulu24");
		assertTrue(sec_state.hasChanged(FSecurity.SHORT_NAME));
		
		assertEquals(1, service.toSecDisplayName(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.DISPLAY_NAME, "zulu24")
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecDisplayName_NoChanges() {
		sec_state.update(FSecurity.SHORT_NAME, "zulu24");
		sec_state.resetChanges();
		
		assertEquals(0, service.toSecDisplayName(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecLotSize() {
		sec_state.update(FSecurity.LOTSIZE, of(100L));
		
		assertEquals(1, service.toSecLotSize(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.LOT_SIZE, of(100L))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecLotSize_NoChanges() {
		sec_state.update(FSecurity.LOTSIZE, of(100L));
		sec_state.resetChanges();
		
		assertEquals(0, service.toSecLotSize(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecTickSize() {
		sec_state.update(FSecurity.MINSTEP, of("0.01"));
		
		assertEquals(1, service.toSecTickSize(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of("0.01"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecTickSize_NoChanges() {
		sec_state.update(FSecurity.MINSTEP, of("0.01"));
		sec_state.resetChanges();
		
		assertEquals(0, service.toSecTickSize(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToTickValue_Case1() {
		sec_state.update(FSecurity.DECIMALS, 2);
		sec_state.update(FSecurity.MINSTEP, of("0.02"));
		sec_state.update(FSecurity.POINT_COST, of("1"));

		assertEquals(1, service.toSecTickValue(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_VALUE, ofRUB5("0.02000"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}

	@Test
	public void testToTickValue_Case2_RTS_6_19() {
		sec_state.update(FSecurity.DECIMALS, 0);
		sec_state.update(FSecurity.MINSTEP, of("10"));
		sec_state.update(FSecurity.POINT_COST, of("129.073"));
		
		assertEquals(1, service.toSecTickValue(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_VALUE, ofRUB5("12.90730"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToTickValue_Case3_GOLD_9_19() {
		sec_state.update(FSecurity.DECIMALS, 1);
		sec_state.update(FSecurity.MINSTEP, of("0.1"));
		sec_state.update(FSecurity.POINT_COST, of("645.364"));
		
		assertEquals(1, service.toSecTickValue(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_VALUE, ofRUB5("6.45364"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToTickValue_NoChanges() {
		sec_state.update(FSecurity.DECIMALS, 2);
		sec_state.update(FSecurity.MINSTEP, of("0.02"));
		sec_state.update(FSecurity.POINT_COST, of("1"));
		sec_state.resetChanges();

		assertEquals(0, service.toSecTickValue(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToTickValue_DecimalsNotDefined() {
		//sec_state.update(TQSecField.DECIMALS, 2);
		sec_state.update(FSecurity.MINSTEP, of("0.02"));
		sec_state.update(FSecurity.POINT_COST, of("1"));
		
		assertEquals(0, service.toSecTickValue(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToTickValue_PointCostNotDefined() {
		sec_state.update(FSecurity.DECIMALS, 2);
		sec_state.update(FSecurity.MINSTEP, of("0.02"));
		//sec_state.update(TQSecField.POINT_COST, of("1"));
		
		assertEquals(0, service.toSecTickValue(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToTickValue_MinStepNotDefined() {
		sec_state.update(FSecurity.DECIMALS, 2);
		//sec_state.update(TQSecField.MINSTEP, of("0.02"));
		sec_state.update(FSecurity.POINT_COST, of("1"));
		
		assertEquals(0, service.toSecTickValue(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToTickValue_PartialUpdate_Deciamls() {
		sec_state.update(FSecurity.DECIMALS, 2);
		sec_state.update(FSecurity.MINSTEP, of("0.02"));
		sec_state.update(FSecurity.POINT_COST, of("1"));
		sec_state.resetChanges();
		sec_state.update(FSecurity.DECIMALS, 3);
		
		assertEquals(1, service.toSecTickValue(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_VALUE, ofRUB5("0.20000"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}

	@Test
	public void testToTickValue_PartialUpdate_PointCost() {
		sec_state.update(FSecurity.DECIMALS, 2);
		sec_state.update(FSecurity.MINSTEP, of("0.02"));
		sec_state.update(FSecurity.POINT_COST, of("1"));
		sec_state.resetChanges();
		sec_state.update(FSecurity.MINSTEP, of("0.05"));
		
		assertEquals(1, service.toSecTickValue(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_VALUE, ofRUB5("0.05000"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToTickValue_PartialUpdate_MinStep() {
		sec_state.update(FSecurity.DECIMALS, 2);
		sec_state.update(FSecurity.MINSTEP, of("0.02"));
		sec_state.update(FSecurity.POINT_COST, of("1"));
		sec_state.resetChanges();
		sec_state.update(FSecurity.MINSTEP, of("0.01"));
		
		assertEquals(1, service.toSecTickValue(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_VALUE, ofRUB5("0.01000"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecInitialMargin_NoChanges() {
		sec_state.update(FSecurity.BUY_DEPOSIT, of("12.284"));
		sec_state.update(FSecurity.SELL_DEPOSIT, of("10.152"));
		sec_state.resetChanges();
		
		assertEquals(0, service.toSecInitialMargin(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}

	@Test
	public void testToSecInitialMargin_BothDefined_BuyDeposit() {
		sec_state.update(FSecurity.BUY_DEPOSIT, of("12.284"));
		sec_state.update(FSecurity.SELL_DEPOSIT, of("10.152"));
		
		assertEquals(1, service.toSecInitialMargin(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.INITIAL_MARGIN, ofRUB5("12.28400"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecInitialMargin_BothDefined_SellDeposit() {
		sec_state.update(FSecurity.BUY_DEPOSIT, of("12.284"));
		sec_state.update(FSecurity.SELL_DEPOSIT, of("18.506"));
		
		assertEquals(1, service.toSecInitialMargin(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.INITIAL_MARGIN, ofRUB5("18.50600"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecInitialMargin_BuyDepositOnlyDefined() {
		sec_state.update(FSecurity.BUY_DEPOSIT, of("12.284"));
		
		assertEquals(1, service.toSecInitialMargin(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.INITIAL_MARGIN, ofRUB5("12.28400"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecInitialMargin_SellDepositOnlyDefined() {
		sec_state.update(FSecurity.SELL_DEPOSIT, of("18.506"));
		
		assertEquals(1, service.toSecInitialMargin(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.INITIAL_MARGIN, ofRUB5("18.50600"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecSettlementPrice() {
		sec_state.update(FSecurity.CLEARING_PRICE, of("142.94"));
		
		assertEquals(1, service.toSecSettlementPrice(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.SETTLEMENT_PRICE, of("142.94"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecSettlementPrice_NoChanges() {
		sec_state.update(FSecurity.CLEARING_PRICE, of("142.94"));
		sec_state.resetChanges();
		
		assertEquals(0, service.toSecSettlementPrice(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecLowerPriceLimit() {
		sec_state.update(FSecurity.MINPRICE, of("245.12"));
		
		assertEquals(1, service.toSecLowerPriceLimit(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.LOWER_PRICE_LIMIT, of("245.12"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}

	@Test
	public void testToSecLowerPriceLimit_NoChanges() {
		sec_state.update(FSecurity.MINPRICE, of("245.12"));
		sec_state.resetChanges();
		
		assertEquals(0, service.toSecLowerPriceLimit(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}

	@Test
	public void testToSecUpperPriceLimit() {
		sec_state.update(FSecurity.MAXPRICE, of("98712.312"));
		
		assertEquals(1, service.toSecUpperPriceLimit(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.UPPER_PRICE_LIMIT, of("98712.312"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecUpperPriceLimit_NoChanges() {
		sec_state.update(FSecurity.MAXPRICE, of("98712.312"));
		sec_state.resetChanges();
		
		assertEquals(0, service.toSecUpperPriceLimit(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecOpenPrice() {
		
		assertEquals(0, service.toSecOpenPrice(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecHighPrice() {
		
		assertEquals(0, service.toSecHighPrice(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecLowPrice() {
		
		assertEquals(0, service.toSecLowPrice(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecClosePrice() {
		
		assertEquals(0, service.toSecClosePrice(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}

	@Test
	public void testToSecExpirationTime_NoChanges() {
		sec_state.update(FSecurity.MAT_DATE, LocalDateTime.of(2019, 7, 9, 12, 35));
		sec_state.resetChanges();
		
		assertEquals(0, service.toSecExpirationTime(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecExpirationTime() {
		sec_state.update(FSecurity.MAT_DATE, LocalDateTime.of(2019, 7, 9, 12, 35));
		
		assertEquals(1, service.toSecExpirationTime(sec_state, builder));
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.EXPIRATION_TIME,
						ZonedDateTime.of(2019, 7, 9, 12, 35, 0, 0, ZoneId.of("Europe/Moscow")).toInstant()
					)
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
}

package ru.prolib.aquila.transaq.impl;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
import ru.prolib.aquila.transaq.engine.sds.GSymbol;
import ru.prolib.aquila.transaq.engine.sds.TSymbol;
import ru.prolib.aquila.transaq.entity.SecurityBoardParams;
import ru.prolib.aquila.transaq.entity.SecurityBoardParamsFactory;
import ru.prolib.aquila.transaq.entity.SecurityParams;
import ru.prolib.aquila.transaq.entity.SecurityParamsFactory;
import ru.prolib.aquila.transaq.entity.SecurityQuotations;
import ru.prolib.aquila.transaq.entity.SecurityQuotationsFactory;
import ru.prolib.aquila.transaq.remote.MessageFields.FQuotation;
import ru.prolib.aquila.transaq.remote.MessageFields.FSecurity;
import ru.prolib.aquila.transaq.remote.MessageFields.FSecurityBoard;

public class TQFieldAssemblerTest {
	private EventQueue queue;
	private TQFieldAssembler service;
	private SecurityQuotations sec_quots;
	private SecurityParams sec_state;
	private SecurityBoardParams sec_brd_state;
	private DeltaUpdateBuilder builder;

	@Before
	public void setUp() throws Exception {
		queue = new EventQueueFactory().createDefault();
		service = new TQFieldAssembler();
		sec_state = new SecurityParamsFactory(queue).produce(null, new GSymbol("GAZP", "MICEX", "RUB", SymbolType.STOCK));
		sec_quots = new SecurityQuotationsFactory(queue).produce(null, new TSymbol("GAZP", "EQTB", "RUB", SymbolType.STOCK));
		sec_brd_state = new SecurityBoardParamsFactory(queue).produce(null, new TSymbol("GAZP", "EQTB", "RUB",SymbolType.STOCK));
		builder = new DeltaUpdateBuilder();
	}

	@Test
	public void testToSecDisplayName() {
		sec_state.update(FSecurity.SHORT_NAME, "Zulu24");
		
		service.toSecDisplayName(sec_state, "MyBoard", builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.DISPLAY_NAME, "Zulu24 @ MyBoard")
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecDisplayName_ShortNameNotDefined() {
		sec_state.update(FSecurity.SHORT_NAME, null);
		
		service.toSecDisplayName(sec_state, "MyBoard", builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecDisplayName_BoardNameNotDefined() {
		sec_state.update(FSecurity.SHORT_NAME, "Zulu24");
		
		service.toSecDisplayName(sec_state, null, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecLotSize() {
		sec_brd_state.update(FSecurityBoard.LOTSIZE, of(100L));
		
		service.toSecLotSize(sec_brd_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.LOT_SIZE, of(100L))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecLotSize_NotDefined() {
		sec_brd_state.update(FSecurityBoard.LOTSIZE, null);
		
		service.toSecLotSize(sec_brd_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecTickSize() {
		sec_brd_state.update(FSecurityBoard.MINSTEP, of("0.01"));
		
		service.toSecTickSize(sec_brd_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_SIZE, of("0.01"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecTickSize_NotDefined() {
		sec_brd_state.update(FSecurityBoard.MINSTEP, null);
		
		service.toSecTickSize(sec_brd_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToTickValue_Case1() {
		sec_brd_state.update(FSecurityBoard.DECIMALS, 2);
		sec_brd_state.update(FSecurityBoard.MINSTEP, of("0.02"));
		sec_brd_state.update(FSecurityBoard.POINT_COST, of("1"));

		service.toSecTickValue(sec_brd_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_VALUE, ofRUB5("0.02000"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}

	@Test
	public void testToTickValue_Case2_RTS_6_19() {
		sec_brd_state.update(FSecurityBoard.DECIMALS, 0);
		sec_brd_state.update(FSecurityBoard.MINSTEP, of("10"));
		sec_brd_state.update(FSecurityBoard.POINT_COST, of("129.073"));
		
		service.toSecTickValue(sec_brd_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_VALUE, ofRUB5("12.90730"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToTickValue_Case2_RTS_3_20() {
		sec_brd_state.update(FSecurityBoard.DECIMALS, 0);
		sec_brd_state.update(FSecurityBoard.MINSTEP, of("10"));
		sec_brd_state.update(FSecurityBoard.POINT_COST, of("124.06"));

		service.toSecTickValue(sec_brd_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_VALUE, ofRUB5("12.40600"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToTickValue_Case3_GOLD_9_19() {
		sec_brd_state.update(FSecurityBoard.DECIMALS, 1);
		sec_brd_state.update(FSecurityBoard.MINSTEP, of("0.1"));
		sec_brd_state.update(FSecurityBoard.POINT_COST, of("645.364"));
		
		service.toSecTickValue(sec_brd_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_VALUE, ofRUB5("6.45364"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToTickValue_NotDefined() {

		service.toSecTickValue(sec_brd_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToTickValue_DecimalsNotDefined() {
		sec_brd_state.update(FSecurityBoard.MINSTEP, of("0.02"));
		sec_brd_state.update(FSecurityBoard.POINT_COST, of("1"));
		
		service.toSecTickValue(sec_brd_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToTickValue_PointCostNotDefined() {
		sec_brd_state.update(FSecurityBoard.DECIMALS, 2);
		sec_brd_state.update(FSecurityBoard.MINSTEP, of("0.02"));
		
		service.toSecTickValue(sec_brd_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToTickValue_MinStepNotDefined() {
		sec_brd_state.update(FSecurityBoard.DECIMALS, 2);
		sec_brd_state.update(FSecurityBoard.POINT_COST, of("1"));
		
		service.toSecTickValue(sec_brd_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToTickValue_PointCostHas6Decimals() {
		// Sec.code	| Board | Market ID | Decimals | Min.Step | Lot | Point Cost
		//	MOEX	| RPEU  | 		1	|		4  |   0.0001 |	 1	|  0.617676
		// 
		// Стоимость_шага_цены = point_cost * minstep * 10^decimals
		sec_brd_state.update(FSecurityBoard.DECIMALS, 4);
		sec_brd_state.update(FSecurityBoard.MINSTEP, of("0.0001"));
		sec_brd_state.update(FSecurityBoard.POINT_COST, of("0.617676"));
		
		service.toSecTickValue(sec_brd_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.TICK_VALUE, of("0.00617676", "RUB"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecInitialMargin_NotDefined() {
		
		service.toSecInitialMargin(sec_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}

	@Test
	public void testToSecInitialMargin_BothDefined_BuyDepositIsGreater() {
		sec_state.update(FSecurity.BUY_DEPOSIT, of("12.284"));
		sec_state.update(FSecurity.SELL_DEPOSIT, of("10.152"));
		
		service.toSecInitialMargin(sec_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.INITIAL_MARGIN, ofRUB5("12.28400"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecInitialMargin_BothDefined_SellDepositIsGreater() {
		sec_state.update(FSecurity.BUY_DEPOSIT, of("12.284"));
		sec_state.update(FSecurity.SELL_DEPOSIT, of("18.506"));
		
		service.toSecInitialMargin(sec_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.INITIAL_MARGIN, ofRUB5("18.50600"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecInitialMargin_BuyDepositOnlyDefined() {
		sec_state.update(FSecurity.BUY_DEPOSIT, of("12.284"));
		
		service.toSecInitialMargin(sec_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.INITIAL_MARGIN, ofRUB5("12.28400"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecInitialMargin_SellDepositOnlyDefined() {
		sec_state.update(FSecurity.SELL_DEPOSIT, of("18.506"));
		
		service.toSecInitialMargin(sec_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.INITIAL_MARGIN, ofRUB5("18.50600"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecSettlementPrice() {
		sec_state.update(FSecurity.CLEARING_PRICE, of("142.94"));
		
		service.toSecSettlementPrice(sec_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.SETTLEMENT_PRICE, of("142.94"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecSettlementPrice_NotDefined() {
		sec_state.update(FSecurity.CLEARING_PRICE, null);
		
		service.toSecSettlementPrice(sec_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecLowerPriceLimit() {
		sec_state.update(FSecurity.MINPRICE, of("245.12"));
		
		service.toSecLowerPriceLimit(sec_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.LOWER_PRICE_LIMIT, of("245.12"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}

	@Test
	public void testToSecLowerPriceLimit_NotDefined() {
		sec_state.update(FSecurity.MINPRICE, null);
		
		service.toSecLowerPriceLimit(sec_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}

	@Test
	public void testToSecUpperPriceLimit() {
		sec_state.update(FSecurity.MAXPRICE, of("98712.312"));
		
		service.toSecUpperPriceLimit(sec_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.UPPER_PRICE_LIMIT, of("98712.312"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecUpperPriceLimit_NotDefined() {
		sec_state.update(FSecurity.MAXPRICE, null);
		
		service.toSecUpperPriceLimit(sec_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecOpenPrice() {
		sec_quots.update(FQuotation.OPEN, of("556.209"));
		
		service.toSecOpenPrice(sec_quots, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.OPEN_PRICE, of("556.209"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecOpenPrice_NotDefined() {
		sec_quots.update(FQuotation.OPEN, null);
		
		service.toSecOpenPrice(sec_quots, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecHighPrice() {
		sec_quots.update(FQuotation.HIGH, of("201.001"));
		
		service.toSecHighPrice(sec_quots, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.HIGH_PRICE, of("201.001"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecHighPrice_NotDefined() {
		sec_quots.update(FQuotation.HIGH, null);

		service.toSecHighPrice(sec_quots, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecLowPrice() {
		sec_quots.update(FQuotation.LOW, of("501.600"));
		
		service.toSecLowPrice(sec_quots, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.LOW_PRICE, of("501.600"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());		
	}
	
	@Test
	public void testToSecLowPrice_NotDefined() {
		sec_quots.update(FQuotation.LOW, null);
		
		service.toSecLowPrice(sec_quots, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecClosePrice() {
		sec_quots.update(FQuotation.CLOSE_PRICE, of("112.920"));
		
		service.toSecClosePrice(sec_quots, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.CLOSE_PRICE, of("112.920"))
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecClosePrice_NotDefined() {
		sec_quots.update(FQuotation.CLOSE_PRICE, null);
		
		service.toSecClosePrice(sec_quots, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}

	@Test
	public void testToSecExpirationTime_NotDefined() {
		
		service.toSecExpirationTime(sec_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
	@Test
	public void testToSecExpirationTime() {
		sec_state.update(FSecurity.MAT_DATE, LocalDateTime.of(2019, 7, 9, 12, 35));
		
		service.toSecExpirationTime(sec_state, builder);
		
		DeltaUpdate expected = new DeltaUpdateBuilder()
				.withToken(SecurityField.EXPIRATION_TIME,
						ZonedDateTime.of(2019, 7, 9, 12, 35, 0, 0, ZoneId.of("Europe/Moscow")).toInstant()
					)
				.buildUpdate();
		assertEquals(expected, builder.buildUpdate());
	}
	
}

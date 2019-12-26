package ru.prolib.aquila.transaq;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;
import static ru.prolib.aquila.transaq.remote.MessageFields.*;
import static ru.prolib.aquila.transaq.TestServer.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventListenerStub;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.ContainerEvent;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityEvent;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.SecurityTickEvent;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepositoryDecoratorRO;
import ru.prolib.aquila.core.data.DataProvider;
import ru.prolib.aquila.transaq.engine.sds.TSymbol;
import ru.prolib.aquila.transaq.entity.Board;
import ru.prolib.aquila.transaq.entity.Market;
import ru.prolib.aquila.transaq.entity.SecurityQuotations;
import ru.prolib.aquila.transaq.impl.TQDataProvider;
import ru.prolib.aquila.transaq.impl.TQDirectory;

public class TransaqTerminalTest {
	private static Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(TransaqTerminalTest.class);
	}
	
	static class CountDownOnEvent implements EventListener {
		private final CountDownLatch counter;
		
		public CountDownOnEvent(CountDownLatch counter) {
			this.counter = counter;
		}

		@Override
		public void onEvent(Event event) {
			counter.countDown();
		}
		
	}
	
	static class DataProviderDecorator implements TQDataProvider {
		private final TQDataProvider dataProvider;
		
		public DataProviderDecorator(TQDataProvider data_provider) {
			this.dataProvider = data_provider;
		}

		@Override
		public long getNextOrderID() {
			return dataProvider.getNextOrderID();
		}

		@Override
		public void subscribeRemoteObjects(EditableTerminal terminal) {
			dataProvider.subscribeRemoteObjects(terminal);
		}

		@Override
		public void unsubscribeRemoteObjects(EditableTerminal terminal) {
			dataProvider.unsubscribeRemoteObjects(terminal);
		}

		@Override
		public void registerNewOrder(EditableOrder order) throws OrderException {
			dataProvider.registerNewOrder(order);
		}

		@Override
		public void cancelOrder(EditableOrder order) throws OrderException {
			dataProvider.cancelOrder(order);
		}

		@Override
		public SubscrHandler subscribe(Symbol symbol, MDLevel level, EditableTerminal terminal) {
			return dataProvider.subscribe(symbol, level, terminal);
		}

		@Override
		public SubscrHandler subscribe(Account account, EditableTerminal terminal) {
			return dataProvider.subscribe(account, terminal);
		}

		@Override
		public void close() {
			dataProvider.close();
		}

		@Override
		public TQDirectory getDirectory() {
			return dataProvider.getDirectory();
		}
		
	}
	
	private static final String SERVICE_ID = "TRANSAQ-TEST";
	private static EventQueue eventQueue;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		eventQueue = new EventQueueImpl();
	}
	
	private static Set<Integer> toSet(int ...values) {
		Set<Integer> result = new HashSet<>();
		for ( int i : values ) {
			result.add(i);
		}
		return result;
	}

	private TestConnectorService testService;
	private TQDataProvider dataProvider;
	private EditableTerminal terminal;
	private EventListenerStub listenerStub;
	
	@Before
	public void setUp() throws Exception {
		listenerStub = new EventListenerStub();
		testService = new TestConnectorService();
		testService.loadConfig("fixture/it/tq-config.ini");
		terminal = null;
	}
	
	private TQDataProvider createDefaultDataProvider() throws Exception {
		return dataProvider = new TransaqBuilder()
				.withServiceID(SERVICE_ID)
				.withEventQueue(eventQueue)
				.withConnectorFactory(testService)
				.build();
	}
	
	private void createTerminal(DataProvider data_provider) {
		terminal = new BasicTerminalBuilder()
				.withEventQueue(eventQueue)
				.withTerminalID(SERVICE_ID)
				.withDataProvider(data_provider)
				.buildTerminal();		
	}
	
	private void createTerminal() throws Exception {
		createTerminal(createDefaultDataProvider());
	}
	
	@After
	public void tearDown() throws Exception {
		if ( terminal != null ) {
			terminal.stop();
			terminal.close();
			terminal = null;
		}
	}
	
	private OSCRepository<TSymbol, SecurityQuotations> getHackedSecurityQuotations() {
		return ((OSCRepositoryDecoratorRO<TSymbol, SecurityQuotations>) dataProvider.getDirectory()
				.getSecurityQuotationsRepository()).getDecoratedRepository();
	}

	@Test
	public void testStartupDataLoading() throws Exception {
		testService.addScript("fixture/it/common-init.xml");
		testService.addScript("fixture/it/common-connected.xml");
		testService.addScript("fixture/it/common-disconnect.xml");
		testService.addScript("fixture/it/common-end.xml");
		createTerminal();
		CountDownLatch finished1 = new CountDownLatch(2);
		OSCRepository<Integer, Market> markets = dataProvider.getDirectory().getMarketRepository(); 
		markets.onEntityUpdate().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				Market market = (Market) ((ContainerEvent) event).getContainer();
				if ( market.getID() == 15 ) {
					finished1.countDown();
				}
			}
		});
		
		OSCRepository<String, Board> boards = dataProvider.getDirectory().getBoardRepository();
		boards.onEntityUpdate().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				Board board = (Board) ((ContainerEvent) event).getContainer();
				if ( board.getCode().equals("WAPS") ) {
					finished1.countDown();
				}
			}
		});
		terminal.start();
		
		assertTrue(finished1.await(1, TimeUnit.SECONDS));
		Market market = markets.getOrThrow(0);
		assertEquals(0, market.getID());
		assertEquals("Collateral", market.getName());
		market = markets.getOrThrow(1);
		assertEquals(1, market.getID());
		assertEquals("MICEX", market.getName());
		market = markets.getOrThrow(4);
		assertEquals(4, market.getID());
		assertEquals("FORTS", market.getName());
	}
	
	@Test
	public void testCaseSDS001_FirstTimeSubscr_Connected_SymbolExists() throws Exception {
		testService.addScript("fixture/it/common-init.xml");
		testService.addScript("fixture/it/common-connected.xml");
		testService.addScript("fixture/it/sds001.xml");
		testService.addScript("fixture/it/common-disconnect.xml");
		testService.addScript("fixture/it/common-end.xml");
		createTerminal();
		CountDownLatch finished = new CountDownLatch(4);
		Symbol symbol = new Symbol("F:RTS-3.20@FUT:RUB");
		EventListener listener = new EventListener() {
			@Override
			public void onEvent(Event event) {
				if ( symbol.equals(((SecurityEvent) event).getSecurity().getSymbol()) ) {
					event.getType().removeListener(this);
					finished.countDown();
				}
			}
		};
		terminal.onSecurityAvailable().addListener(listener);
		terminal.onSecurityUpdate().addListener(listener);
		terminal.onSecurityBestAsk().addListener(listener);
		terminal.onSecurityBestBid().addListener(listener);
		terminal.start();
		
		terminal.subscribe(symbol, MDLevel.L1_BBO);
		
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		assertTrue(terminal.isSecurityExists(symbol));
		Security security = terminal.getSecurity(symbol);
		assertTrue(security.isAvailable());
		assertEquals("RTS-3.20 @ FORTS-Futures", security.getDisplayName());
		assertEquals(of(1L), security.getLotSize());
		assertNull(security.getExpirationTime());
		assertNull(security.getSettlementPrice());
		assertEquals(of("10"), security.getTickSize());
		assertEquals(ofRUB5("12.45600"), security.getTickValue());
		assertEquals(ofRUB5("23031.50"), security.getInitialMargin());
		assertEquals(of(144100L), security.getLowerPriceLimit());
		assertEquals(of(160480L), security.getUpperPriceLimit());
		assertEquals(of(152420L), security.getOpenPrice());
		assertEquals(of(152770L), security.getHighPrice());
		assertEquals(of(152290L), security.getLowPrice());
		assertEquals(of(152290L), security.getClosePrice());
		
		Tick actual = security.getBestAsk();
		assertNotNull(actual);
		assertEquals(Tick.ofAsk(actual.getTime(), of(152680L), of(8L)), actual);
		assertTrue(ChronoUnit.MILLIS.between(Instant.now(), actual.getTime()) <= 100L);
		
		actual = security.getBestBid();
		assertNotNull(actual);
		assertEquals(Tick.ofBid(actual.getTime(),  of(152670L), of(15L)), actual);
		assertTrue(ChronoUnit.MILLIS.between(Instant.now(), actual.getTime()) <= 100L);
		
		assertNull(security.getLastTrade());
	}
	
	@Test
	public void testCaseSDS002_SecondTimeSubscr_Connected_SymbolExists() throws Exception {
		testService.addScript("fixture/it/common-init.xml");
		testService.addScript("fixture/it/common-connected.xml");
		testService.addScript("fixture/it/sds002.xml");
		testService.addScript("fixture/it/common-disconnect.xml");
		testService.addScript("fixture/it/common-end.xml");
		createTerminal();
		getHackedSecurityQuotations().getOrCreate(new TSymbol("RTS-3.20", "FUT", "RUB", SymbolType.FUTURES))
			.consume(new DeltaUpdateBuilder()
				.withToken(FQuotation.OFFER, of(152700L))
				.withToken(FQuotation.OFFER_DEPTH, 15)
				.withToken(FQuotation.BID, of(152650L))
				.withToken(FQuotation.BID_DEPTH, 70)
				.withToken(FQuotation.OPEN, of(152400L))
				.withToken(FQuotation.HIGH, of(152840L))
				.withToken(FQuotation.LOW, of(152230L))
				.withToken(FQuotation.CLOSE_PRICE, of(152300L))
				.buildUpdate());
		CountDownLatch finished = new CountDownLatch(4);
		Symbol symbol = new Symbol("F:RTS-3.20@FUT:RUB");
		EventListener listener = new EventListener() {
			@Override
			public void onEvent(Event event) {
				if ( symbol.equals(((SecurityEvent) event).getSecurity().getSymbol()) ) {
					event.getType().removeListener(this);
					finished.countDown();
				}
			}
		};
		terminal.onSecurityAvailable().addListener(listener);
		terminal.onSecurityUpdate().addListener(listener);
		terminal.onSecurityBestAsk().addListener(listener);
		terminal.onSecurityBestBid().addListener(listener);
		terminal.start();
		
		terminal.subscribe(symbol, MDLevel.L1_BBO);
	
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		assertTrue(terminal.isSecurityExists(symbol));
		Security security = terminal.getSecurity(symbol);
		assertTrue(security.isAvailable());
		assertEquals("RTS-3.20 @ FORTS-Futures", security.getDisplayName());
		assertEquals(of(1L), security.getLotSize());
		assertNull(security.getExpirationTime());
		assertNull(security.getSettlementPrice());
		assertEquals(of("10"), security.getTickSize());
		assertEquals(ofRUB5("12.45600"), security.getTickValue());
		assertEquals(ofRUB5("23031.50"), security.getInitialMargin());
		assertEquals(of(144100L), security.getLowerPriceLimit());
		assertEquals(of(160480L), security.getUpperPriceLimit());
		assertEquals(of(152400L), security.getOpenPrice());
		assertEquals(of(152840L), security.getHighPrice());
		assertEquals(of(152230L), security.getLowPrice());
		assertEquals(of(152300L), security.getClosePrice());
		
		Tick actual = security.getBestAsk();
		assertNotNull(actual);
		assertEquals(Tick.ofAsk(actual.getTime(), of(152700L), of(15L)), actual);
		assertTrue(ChronoUnit.MILLIS.between(Instant.now(), actual.getTime()) <= 100L);
		
		actual = security.getBestBid();
		assertNotNull(actual);
		assertEquals(Tick.ofBid(actual.getTime(),  of(152650L), of(70L)), actual);
		assertTrue(ChronoUnit.MILLIS.between(Instant.now(), actual.getTime()) <= 100L);
		
		assertNull(security.getLastTrade());
	}
	
	@Test
	public void testCaseSDS003_PendingSubscriptsWhenConnected() throws Exception {
		CountDownLatch finished = new CountDownLatch(1);
		testService.addScript("fixture/it/common-init.xml");
		testService.addScript("fixture/it/common-connected.xml");
		testService.addScript("fixture/it/sds003.xml", new Action.CountDown(finished));
		testService.addScript("fixture/it/common-disconnect.xml");
		testService.addScript("fixture/it/common-end.xml");
		createTerminal();
		
		terminal.subscribe(new Symbol("F:RTS-3.20@FUT:RUB"), MDLevel.L1);
		terminal.subscribe(new Symbol("S:GAZP@TQBR:RUB"), MDLevel.L2);
		terminal.subscribe(new Symbol("F:Si-3.20@FUT:RUB"), MDLevel.L1_BBO);
		
		terminal.start();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
	}
	
	@Test
	public void testCaseSDS004_BidAskDuplicates() throws Exception {
		CountDownLatch finished = new CountDownLatch(1);
		testService.addScript("fixture/it/common-init.xml");
		testService.addScript("fixture/it/common-connected.xml");
		testService.addScript("fixture/it/sds004.xml");
		testService.addScript("fixture/it/common-disconnected.xml");
		testService.addScript("fixture/it/common-end.xml");
		createTerminal();
		terminal.onSecurityBestAsk().addListener(listenerStub);
		terminal.onSecurityBestBid().addListener(listenerStub);
		dataProvider.getDirectory().getConnectionStatus().onDisconnected().addListener(new CountDownOnEvent(finished));
		terminal.start();
		
		terminal.subscribe(new Symbol("F:RTS-3.20@FUT:RUB"), MDLevel.L1_BBO);
		
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		Instant now = Instant.now();
		for ( int i = 0; i < listenerStub.getEventCount(); i ++ ) {
			SecurityTickEvent event = (SecurityTickEvent) listenerStub.getEvent(i);
			assertEquals(new Symbol("F:RTS-3.20@FUT:RUB"), event.getSecurity().getSymbol());
			assertTrue(ChronoUnit.MILLIS.between(event.getTime(), now) <= 100L);
			assertTrue(ChronoUnit.MILLIS.between(event.getTick().getTime(), now) <= 100L);
		}
		List<Tick> expected = new ArrayList<>();
		expected.add(Tick.ofAsk(of(152680L), of( 8L)));
		expected.add(Tick.ofBid(of(152670L), of(15L)));
		expected.add(Tick.ofAsk(of(152680L), of( 9L)));
		expected.add(Tick.ofBid(of(152670L), of(16L)));
		expected.add(Tick.ofAsk(of(152680L), of(11L)));
		expected.add(Tick.ofBid(of(152670L), of( 8L)));
		expected.add(Tick.ofAsk(of(152680L), of(10L)));
		expected.add(Tick.ofBid(of(152690L), of( 9L)));
		expected.add(Tick.ofAsk(of(152700L), of(10L)));
		expected.add(Tick.ofBid(of(152620L), of(17L)));
		for ( int i = 0; i < expected.size(); i ++ ) {
			SecurityTickEvent event = (SecurityTickEvent) listenerStub.getEvent(i);
			Tick actual = event.getTick().withTime(Instant.EPOCH);
			assertEquals("At #" + i, expected.get(i), actual);
		}
		assertEquals(expected.size(), listenerStub.getEventCount());
	}
	
	@Test
	public void testCaseSDS005_OHLCUpdates() throws Exception {
		Symbol symbol = new Symbol("F:RTS-3.20@FUT:RUB");
		CountDownLatch finished = new CountDownLatch(1);
		testService.addScript("fixture/it/common-init.xml");
		testService.addScript("fixture/it/common-connected.xml");
		testService.addScript("fixture/it/sds005.xml");
		testService.addScript("fixture/it/common-disconnected.xml");
		testService.addScript("fixture/it/common-end.xml");
		createTerminal();
		terminal.onSecurityAvailable().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				SecurityEvent e = (SecurityEvent) event;
				if ( e.getSecurity().getSymbol().equals(symbol) ) {
					e.getSecurity().onUpdate().addListener(listenerStub);
				}
			}
		});
		dataProvider.getDirectory().getConnectionStatus().onDisconnected().addListener(new CountDownOnEvent(finished));
		terminal.start();
		
		terminal.subscribe(symbol, MDLevel.L1_BBO);
		
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		SecurityEvent event;
		Instant now = Instant.now();
		List<Set<Integer>> actual = new ArrayList<>();
		Security security = terminal.getSecurity(symbol);
		for ( int i = 0; i < listenerStub.getEventCount(); i ++ ) {
			event = (SecurityEvent) listenerStub.getEvent(i);
			assertEquals(symbol, event.getSecurity().getSymbol());
			assertEquals(security.onUpdate(), event.getType());
			assertTrue(ChronoUnit.MILLIS.between(event.getTime(), now) <= 100L);
			actual.add(event.getUpdatedTokens());
		}
		List<Set<Integer>> expected = new ArrayList<>();
		int _o = SecurityField.OPEN_PRICE, _h = SecurityField.HIGH_PRICE,
				_l = SecurityField.LOW_PRICE, _c = SecurityField.CLOSE_PRICE;
		expected.add(toSet(_o, _h, _l, _c));
		expected.add(toSet(_o, _l));
		expected.add(toSet(_h));
		expected.add(toSet(_c));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCaseSDS006_StopListeningToFeedIfConnectedAndUnsubscribed() throws Exception {
		Symbol symbol = new Symbol("F:RTS-3.20@FUT:RUB");
		CountDownLatch finished = new CountDownLatch(1);
		testService.addScript("fixture/it/common-init.xml");
		testService.addScript("fixture/it/common-connected.xml");
		testService.addScript("fixture/it/sds006.xml");
		testService.addScript("fixture/it/common-disconnected.xml");
		testService.addScript("fixture/it/common-end.xml");
		createTerminal();
		dataProvider.getDirectory().getConnectionStatus().onConnected().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				SubscrHandler h1 = terminal.subscribe(symbol, MDLevel.L1_BBO);
				SubscrHandler h2 = terminal.subscribe(symbol, MDLevel.L2);
				h1.close();
				h2.close();
			}
		});
		dataProvider.getDirectory().getConnectionStatus().onDisconnected().addListener(new CountDownOnEvent(finished));
		
		terminal.start();
		
		assertTrue(finished.await(1, TimeUnit.SECONDS));
	}
	
	@Test
	public void testCaseSDS007_DoNothingIfNotConnectedAndUnsubscribed() throws Exception {
		Symbol symbol = new Symbol("F:RTS-3.20@FUT:RUB");
		CountDownLatch finished = new CountDownLatch(1);
		testService.addScript("fixture/it/common-init.xml");
		testService.addScript("fixture/it/common-connected.xml");
		testService.addScript("fixture/it/sds007.xml");
		testService.addScript("fixture/it/common-disconnected.xml");
		testService.addScript("fixture/it/common-end.xml");
		createTerminal();
		SubscrHandler h1 = terminal.subscribe(symbol, MDLevel.L1_BBO);
		SubscrHandler h2 = terminal.subscribe(symbol, MDLevel.L1);
		dataProvider.getDirectory().getConnectionStatus().onDisconnected().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				h1.close();
				h2.close();
				finished.countDown();
			}
		});
		
		terminal.start();
		
		assertTrue(finished.await(1, TimeUnit.SECONDS));
	}
	
	@Test
	public void testCaseSDS008_RestoreSubscriptionsAfterReconnect() throws Exception {
		Symbol symbol1 = new Symbol("F:Si-3.20@FUT:RUB"), symbol2 = new Symbol("F:RTS-3.20@FUT:RUB");
		testService.addScript("fixture/it/common-init.xml");
		testService.addScript("fixture/it/common-connected.xml");
		testService.addScript("fixture/it/sds008.xml");
		testService.addScript("fixture/it/common-end.xml");
		createTerminal();
		List<SubscrHandler> subscriptions = new ArrayList<>();
		subscriptions.add(terminal.subscribe(symbol1, MDLevel.L1));
		subscriptions.add(terminal.subscribe(symbol2, MDLevel.L2));
		CountDownLatch finished = new CountDownLatch(2);
		dataProvider.getDirectory().getConnectionStatus().onDisconnected().addListener(new CountDownOnEvent(finished));
		dataProvider.getDirectory().getConnectionStatus().onDisconnected().listenOnce(new EventListener() {
			@Override
			public void onEvent(Event event) {
				subscriptions.get(1).close();
				testService.ExplicitCall();
			}
		});

		terminal.start();
		
		assertTrue(finished.await(1, TimeUnit.SECONDS));
	}
	
	@Test
	public void testCaseSDS009_SubscribeNonExistentBoard() throws Exception {
		testService.addScript("fixture/it/common-init.xml");
		testService.addScript("fixture/it/common-connected.xml");
		testService.addScript("fixture/it/sds009.xml");
		testService.addScript("fixture/it/common-end.xml");
		createTerminal();
		CountDownLatch started = new CountDownLatch(1), finished = new CountDownLatch(1);
		dataProvider.getDirectory().getConnectionStatus().onConnected().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				SubscrHandler h = terminal.subscribe(new Symbol("S:GAZP@EQTB:RUB"), MDLevel.L1_BBO);
				h.close();
				started.countDown();
			}
		});
		dataProvider.getDirectory().getConnectionStatus().onDisconnected().addListener(new CountDownOnEvent(finished));
		
		terminal.start();
		
		assertTrue(started.await(1, TimeUnit.SECONDS));
		testService.ExplicitCall();
		assertTrue(finished.await(1, TimeUnit.SECONDS));
	}
	
	@Test
	public void testCaseSDS010_SkipMessagesAfterTerminalClosed() throws Exception {
		// Сложный кейс! Если начал фейлиться, то сходу без поллитры не разберешься.
		CountDownLatch tick1 = new CountDownLatch(1), tick2 = new CountDownLatch(1), tick3 = new CountDownLatch(1);
		testService.addScript("fixture/it/common-init.xml");
		testService.addScript("fixture/it/common-connected.xml");
		testService.addScript("fixture/it/sds010.xml");
		testService.addScript("fixture/it/common-end.xml");
		dataProvider = new DataProviderDecorator(createDefaultDataProvider()) {
			@Override
			public void close() {
				// Если мы здесь, значит stop терминала уже был вызван и disconnect уже зашедулен.
				// Он может быть зашедулен, но еще не обработан в потоке разбора очереди команд.
				// Если при таком раскладе из этого места вызвать ExplicitCall, то будет фейл.
				// Это связано с тем, что вызывая отсюда мы работаем с фазой сценария непосредственно
				// в данный момент. А зашедулив дисконнект в очереди движка, мы планируем работу с фазой
				// где-то в будущем.
				// 
				// Как разрулить? Видимо здесь нужно ждать onDisconnect, а в сценарий перед ExplicitCall
				// добавить ожидание дисконнекта и сервер статус="отключено" в ответ. Однако здесь другая
				// проблема. Если будет получен дисконнект, то любая работа с инструментми блокируется.
				// Все сообщения будут просто проигнорированы SDS при отсутствии коннекта.
				//
				// Вариант решения: отреагировать на требование дисконнекта очередным тиком данных
				// и подождать его перед ExpliticCall.
				try {
					tick2.await(1, TimeUnit.SECONDS);
				} catch ( InterruptedException e ) {
					throw new IllegalStateException(e);
				}
				testService.ExplicitCall();
				super.close();
			}
		};
		createTerminal(dataProvider);
		terminal.subscribe(new Symbol("S:GAZP@TQBR:RUB"), MDLevel.L1_BBO);
		terminal.onSecurityBestAsk().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				SecurityTickEvent e = (SecurityTickEvent) event;
				if ( e.getSecurity().getSymbol().equals(new Symbol("S:GAZP@TQBR:RUB")) ) {
					CDecimal price = e.getTick().getPrice();
					if ( price.equals(of("100.00")) ) {
						tick1.countDown();						
					} else if ( price.equals(of("200.00")) ) {
						tick2.countDown();
					} else if ( price.equals(of("300.00")) ) {
						tick3.countDown();
					}
				}
			}
		});
		
		terminal.start();
		
		assertTrue(tick1.await(1, TimeUnit.SECONDS));
		// Any case - close will cause disconnect. Thus we have
		// to add it to the script to make it proper sequence.
		terminal.close();
		assertTrue(tick3.await(1, TimeUnit.SECONDS));
		for ( int i = 0; i < 5 && terminal.isClosed() == false; i ++ ) {
			logger.debug("Iteration #" + i);
			Thread.sleep(50L);
		}
		assertTrue(terminal.isClosed());
	}
	
}

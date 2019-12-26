package ru.prolib.aquila.transaq.remote;

import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventListenerStub;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventQueueImpl;

public class ConnectionStatusTest {
	
	static class CountDown implements EventListener {
		private final CountDownLatch counter;
		
		CountDown(CountDownLatch counter) {
			this.counter = counter;
		}

		@Override
		public void onEvent(Event event) {
			counter.countDown();
		}
		
	}
	
	private static EventQueue queue;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		queue = new EventQueueImpl();
	}
	
	private IMocksControl control;
	private EventQueue queueMock;
	private ConnectionStatus service;
	private EventListenerStub listenerStub;

	@Before
	public void setUp() throws Exception {
		control = EasyMock.createStrictControl();
		queueMock = control.createMock(EventQueue.class);
		service = new ConnectionStatus(queue, "TEST");
		listenerStub = new EventListenerStub();
	}
	
	@Test
	public void testGetters() {
		assertFalse(service.isConnected());
		assertEquals("TEST.CONNECTED", service.onConnected().getId());
		assertEquals("TEST.DISCONNECTED", service.onDisconnected().getId());
	}
	
	@Test
	public void testSetConnected() throws Exception {
		CountDownLatch finished = new CountDownLatch(1);
		service.onDisconnected().addListener(listenerStub);
		service.onConnected().addListener(listenerStub);
		service.onConnected().addListener(new CountDown(finished));
		
		service.setConnected();
		
		assertTrue(service.isConnected());
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		assertEquals(1, listenerStub.getEventCount());
		assertEquals(new ConnectionStatusEvent(service.onConnected(), true), listenerStub.getEvent(0));
	}
	
	@Test
	public void testSetConnected_SkipRepeats() throws Exception {
		service = new ConnectionStatus(queueMock, "TEST");
		control.resetToNice();
		service.setConnected();
		control.resetToStrict();
		control.replay();
		
		assertTrue(service.isConnected());
		service.setConnected();
		
		control.verify();
	}

	@Test
	public void testSetDisconnected() throws Exception {
		CountDownLatch flushed = new CountDownLatch(1);
		service.onConnected().addListener(new CountDown(flushed));
		service.setConnected();
		assertTrue(flushed.await(1, TimeUnit.SECONDS));
		assertTrue(service.isConnected()); // Ready to disconnect
		CountDownLatch finished = new CountDownLatch(1);
		service.onConnected().addListener(listenerStub);
		service.onDisconnected().addListener(listenerStub);
		service.onDisconnected().addListener(new CountDown(finished));
		
		service.setDisconnected();
		
		assertFalse(service.isConnected());
		assertTrue(finished.await(1, TimeUnit.SECONDS));
		assertEquals(1, listenerStub.getEventCount());
		assertEquals(new ConnectionStatusEvent(service.onDisconnected(), false), listenerStub.getEvent(0));
	}
	
	@Test
	public void testSetDisconnected_SkipRepeats() {
		service = new ConnectionStatus(queueMock, "TEST");
		control.replay();
		
		service.setDisconnected();
		
		control.verify();
	}

}

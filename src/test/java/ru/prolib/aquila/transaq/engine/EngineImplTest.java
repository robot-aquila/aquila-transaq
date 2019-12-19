package ru.prolib.aquila.transaq.engine;

import static org.junit.Assert.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class EngineImplTest {
	private static Symbol symbol;
	private BlockingQueue<Cmd> queue;
	private EngineImpl service;

	@Before
	public void setUp() throws Exception {
		queue = new LinkedBlockingQueue<>();
		service = new EngineImpl(queue);
	}
	
	@Test
	public void testConnect() throws Exception {
		service.connect();

		assertEquals(new CmdConnect(), queue.poll(1, TimeUnit.SECONDS));
	}
	
	@Test
	public void testDisconnect() throws Exception {
		service.disconnect();
		
		assertEquals(new CmdDisconnect(), queue.poll(1, TimeUnit.SECONDS));
	}
	
	@Test
	public void testShutdown() throws Exception {
		CompletableFuture<Boolean> result = service.shutdown();

		Cmd actual = queue.poll(1, TimeUnit.SECONDS);
		assertEquals(new CmdShutdown(), actual);
		assertNotNull(result);
		assertSame(result, actual.getResult());
	}
	
	@Test
	public void testMessageFromServer() throws Exception {
		service.messageFromServer("hello, world");
		
		assertEquals(new CmdMsgFromServer("hello, world"), queue.poll(1, TimeUnit.SECONDS));
	}
	
	@Test
	public void testSubscribeSymbol() throws Exception {
		service.subscribeSymbol(symbol, MDLevel.L1_BBO);
		
		assertEquals(new CmdSubscrSymbol(symbol, MDLevel.L1_BBO), queue.poll(1, TimeUnit.SECONDS));
	}

	@Test
	public void testUnsubscribeSymbol() throws Exception {
		service.unsubscribeSymbol(symbol, MDLevel.L2);
		
		assertEquals(new CmdUnsubscrSymbol(symbol, MDLevel.L2), queue.poll(1, TimeUnit.SECONDS));
	}

}

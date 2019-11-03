package ru.prolib.aquila.transaq.engine;

import static org.junit.Assert.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

public class EngineImplTest {
	private BlockingQueue<Cmd> queue;
	private EngineImpl service;

	@Before
	public void setUp() throws Exception {
		queue = new LinkedBlockingQueue<>();
		service = new EngineImpl(queue);
	}
	
	@Test
	public void testShutdown() throws Exception {
		service.shutdown();

		assertEquals(new CmdShutdown(), queue.poll(1, TimeUnit.SECONDS));
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}

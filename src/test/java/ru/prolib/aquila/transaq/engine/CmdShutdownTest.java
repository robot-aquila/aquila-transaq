package ru.prolib.aquila.transaq.engine;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.engine.CmdShutdown;
import ru.prolib.aquila.transaq.engine.CmdType;

public class CmdShutdownTest {
	private CmdShutdown service;

	@Before
	public void setUp() throws Exception {
		service = new CmdShutdown();
	}

	@Test
	public void testGetters() {
		assertEquals(CmdType.SHUTDOWN, service.getType());
		assertFalse(service.getResult().isDone());
	}
	
	@Test
	public void testHashCode() {
		int expected = 618243986;
		
		assertEquals(expected, service.hashCode());
	}

	@Test
	public void testEquals() {
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
		assertTrue(service.equals(service));
		assertTrue(service.equals(new CmdShutdown()));
	}

}

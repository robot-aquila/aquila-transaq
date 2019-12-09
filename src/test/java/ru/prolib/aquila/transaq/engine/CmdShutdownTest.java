package ru.prolib.aquila.transaq.engine;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.engine.CmdShutdown;
import ru.prolib.aquila.transaq.engine.CmdType;

public class CmdShutdownTest {
	private CmdShutdown service;

	@Before
	public void setUp() throws Exception {
		service = new CmdShutdown(4);
	}
	
	@Test
	public void testCtor0() {
		service = new CmdShutdown();
		
		assertEquals(CmdType.SHUTDOWN, service.getType());
		assertEquals(0, service.getPhase());
		assertFalse(service.getResult().isDone());
	}

	@Test
	public void testGetters() {
		assertEquals(CmdType.SHUTDOWN, service.getType());
		assertEquals(4, service.getPhase());
		assertFalse(service.getResult().isDone());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(400971, 9009)
				.append(4)
				.build();
		
		assertEquals(expected, service.hashCode());
	}

	@Test
	public void testEquals() {
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
		assertTrue(service.equals(service));
		assertTrue(service.equals(new CmdShutdown(4)));
		assertFalse(service.equals(new CmdShutdown()));
		assertFalse(service.equals(new CmdShutdown(1)));
	}

}

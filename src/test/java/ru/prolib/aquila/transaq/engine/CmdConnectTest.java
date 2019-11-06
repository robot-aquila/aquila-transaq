package ru.prolib.aquila.transaq.engine;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CmdConnectTest {
	private CmdConnect service;

	@Before
	public void setUp() throws Exception {
		service = new CmdConnect();
	}
	
	@Test
	public void testGetters() {
		assertEquals(CmdType.CONNECT, service.getType());
		assertFalse(service.getResult().isDone());
	}
	
	@Test
	public void testHashCode() {
		int expected = 761524314;
		
		assertEquals(expected, service.hashCode());
	}

	@Test
	public void testEquals() {
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
		assertTrue(service.equals(service));
		assertTrue(service.equals(new CmdConnect()));
	}

}

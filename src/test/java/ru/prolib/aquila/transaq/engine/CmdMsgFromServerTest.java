package ru.prolib.aquila.transaq.engine;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.engine.CmdMsgFromServer;
import ru.prolib.aquila.transaq.engine.CmdType;

public class CmdMsgFromServerTest {
	private CmdMsgFromServer service;

	@Before
	public void setUp() throws Exception {
		service = new CmdMsgFromServer("kalimba de luna");
	}
	
	@Test
	public void testGetters() {
		assertEquals(CmdType.MSG_FROM_SERVER, service.getType());
		assertEquals("kalimba de luna", service.getMessage());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(1526007, 13)
				.append(CmdType.MSG_FROM_SERVER)
				.append("kalimba de luna")
				.build();
		
		assertEquals(expected, service.hashCode());
	}

	@Test
	public void testEquals() {
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
		assertFalse(service.equals(new CmdMsgFromServer("foobar")));
		assertTrue(service.equals(service));
		assertTrue(service.equals(new CmdMsgFromServer("kalimba de luna")));
	}

}

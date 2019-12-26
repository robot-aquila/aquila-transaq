package ru.prolib.aquila.transaq.remote;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;

public class ConnectionStatusEventTest {
	private ConnectionStatusEvent service;
	private EventType type1, type2;

	@Before
	public void setUp() throws Exception {
		type1 = new EventTypeImpl("foo.bar");
		type2 = new EventTypeImpl("buz.bar");
		service = new ConnectionStatusEvent(type1, true);
	}
	
	@Test
	public void testGetters() {
		assertSame(type1, service.getType());
		assertTrue(service.isConnected());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		assertTrue(service.equals(new ConnectionStatusEvent(type1, true)));
		assertFalse(service.equals(new ConnectionStatusEvent(type1, false)));
		assertFalse(service.equals(new ConnectionStatusEvent(type2, false)));
		assertFalse(service.equals(new ConnectionStatusEvent(type2, true)));
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(6901127, 7103)
				.append(type1)
				.append(true)
				.build();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testToString() {
		assertEquals("ConnectionStatusEvent[foo.bar connected]", new ConnectionStatusEvent(type1, true).toString());
		assertEquals("ConnectionStatusEvent[foo.bar disconnected]", new ConnectionStatusEvent(type1, false).toString());
	}

}

package ru.prolib.aquila.transaq.remote;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;

public class ConnectionStatusEventFactoryTest {
	private EventType type1, type2;
	private ConnectionStatusEventFactory service;

	@Before
	public void setUp() throws Exception {
		type1 = new EventTypeImpl("foo.bar");
		type2 = new EventTypeImpl("buz.bar");
		service = new ConnectionStatusEventFactory(true);
	}
	
	@Test
	public void testProduceEvent() {
		assertEquals(new ConnectionStatusEvent(type1, true), service.produceEvent(type1));
		assertEquals(new ConnectionStatusEvent(type2, true), service.produceEvent(type2));
		assertEquals(new ConnectionStatusEvent(type1, false), new ConnectionStatusEventFactory(false).produceEvent(type1));
		assertEquals(new ConnectionStatusEvent(type2, false), new ConnectionStatusEventFactory(false).produceEvent(type2));
	}
	
	@Test
	public void testEquals() {
		assertTrue(service.equals(service));
		assertTrue(service.equals(new ConnectionStatusEventFactory(true)));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(667891, 901)
				.append(true)
				.build();
		
		assertEquals(expected, service.hashCode());
	}

}

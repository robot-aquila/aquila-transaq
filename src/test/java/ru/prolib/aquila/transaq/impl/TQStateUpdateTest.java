package ru.prolib.aquila.transaq.impl;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;

public class TQStateUpdateTest {
	private DeltaUpdate du1, du2;
	private TQStateUpdate<String> service;

	@Before
	public void setUp() throws Exception {
		du1 = new DeltaUpdateBuilder()
				.withToken(1, "gamu")
				.withToken(2, "dune")
				.buildUpdate();
		du2 = new DeltaUpdateBuilder()
				.withToken(5, "kappa")
				.withToken(7, "bozon")
				.buildUpdate();
		service = new TQStateUpdate<>("foo", du1);
	}
	
	@Test
	public void testCtor2() {
		assertEquals("foo", service.getID());
		assertEquals(du1, service.getUpdate());
	}
	
	@Test
	public void testToString() {
		String expected = "TQStateUpdate[id=foo,update=DeltaUpdate[null {1=gamu, 2=dune}]]";
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(216688123, 5651)
				.append("foo")
				.append(du1)
				.build();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		assertTrue(service.equals(new TQStateUpdate<String>("foo", du1)));
		assertFalse(service.equals(new TQStateUpdate<String>("bar", du1)));
		assertFalse(service.equals(new TQStateUpdate<String>("foo", du2)));
		assertFalse(service.equals(new TQStateUpdate<String>("bar", du2)));
	}

}

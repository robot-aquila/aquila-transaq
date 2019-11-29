package ru.prolib.aquila.transaq.impl;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.entity.SecType;

public class TQSecID1Test {
	private TQSecID1 service;

	@Before
	public void setUp() throws Exception {
		service = new TQSecID1("foo", 220);
	}
	
	@Test
	public void testCtor2() {
		assertEquals("foo", service.getSecCode());
		assertEquals(220, service.getMarketID());
	}
	
	@Test
	public void testCtor1_SecID3() {
		service = new TQSecID1(new TQSecID_F("zulu24", 15, "UPS", "foobar", SecType.FUT));
		assertEquals("zulu24", service.getSecCode());
		assertEquals(15, service.getMarketID());
	}
	
	@Test
	public void testToString() {
		String expected = "TQSecID1[secCode=foo,marketID=220]";
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(876172303, 8123)
				.append("foo")
				.append(220)
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
		assertTrue(service.equals(new TQSecID1("foo", 220)));
		assertFalse(service.equals(new TQSecID1("bar", 220)));
		assertFalse(service.equals(new TQSecID1("foo", 110)));
		assertFalse(service.equals(new TQSecID1("bar", 110)));
	}

}

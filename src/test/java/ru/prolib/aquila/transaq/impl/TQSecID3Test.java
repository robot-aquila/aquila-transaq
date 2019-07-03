package ru.prolib.aquila.transaq.impl;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class TQSecID3Test {
	private TQSecID3 service;

	@Before
	public void setUp() throws Exception {
		service = new TQSecID3("foo", 19, "bar");
	}
	
	@Test
	public void testCtor3() {
		assertEquals("foo", service.getSecCode());
		assertEquals(19, service.getMarketID());
		assertEquals("bar", service.getShortName());
	}
	
	@Test
	public void testToString() {
		String expected = "TQSecID3[secCode=foo,marketID=19,shortName=bar]";
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(234103, 1515)
				.append("foo")
				.append(19)
				.append("bar")
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
		Variant<String> vSC = new Variant<>("foo", "zulu24");
		Variant<Integer> vMID = new Variant<>(vSC, 19, 27);
		Variant<String> vSN = new Variant<>(vMID, "bar", "buzz");
		Variant<?> iterator = vSN;
		int found_cnt = 0;
		TQSecID3 x, found = null;
		do {
			x = new TQSecID3(vSC.get(), vMID.get(), vSN.get());
			if ( service.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals("foo", found.getSecCode());
		assertEquals(19, found.getMarketID());
		assertEquals("bar", found.getShortName());
	}

}

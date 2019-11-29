package ru.prolib.aquila.transaq.impl;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.transaq.entity.SecType;

public class TQSecID_FTest {
	private TQSecID_F service;

	@Before
	public void setUp() throws Exception {
		service = new TQSecID_F("foo", 19, "brd", "bar", SecType.FUT);
	}
	
	@Test
	public void testCtor3() {
		assertEquals("foo", service.getSecCode());
		assertEquals(19, service.getMarketID());
		assertEquals("brd", service.getDefaultBoard());
		assertEquals("bar", service.getShortName());
		assertEquals(SecType.FUT, service.getType());
	}
	
	@Test
	public void testToString() {
		String expected = "TQSecID_F[secCode=foo,marketID=19,defBoard=brd,shortName=bar,type=FUT]";
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(234103, 1515)
				.append("foo")
				.append(19)
				.append("brd")
				.append("bar")
				.append(SecType.FUT)
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
		Variant<String> vBrd = new Variant<>(vMID, "brd", "oop");
		Variant<String> vSN = new Variant<>(vBrd, "bar", "buzz");
		Variant<SecType> vTYP = new Variant<>(vSN, SecType.FUT, SecType.BOND);
		Variant<?> iterator = vTYP;
		int found_cnt = 0;
		TQSecID_F x, found = null;
		do {
			x = new TQSecID_F(vSC.get(), vMID.get(), vBrd.get(), vSN.get(), vTYP.get());
			if ( service.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals("foo", found.getSecCode());
		assertEquals(19, found.getMarketID());
		assertEquals("brd", found.getDefaultBoard());
		assertEquals("bar", found.getShortName());
		assertEquals(SecType.FUT, found.getType());
	}

}

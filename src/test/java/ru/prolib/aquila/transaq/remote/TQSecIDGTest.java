package ru.prolib.aquila.transaq.remote;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.instanceOf;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.entity.SecType;
import ru.prolib.aquila.transaq.remote.TQSecIDG;

public class TQSecIDGTest {
	private TQSecIDG service;

	@Before
	public void setUp() throws Exception {
		service = new TQSecIDG("foo", 220);
	}
	
	@Test
	public void testISA() {
		assertThat(service, instanceOf(ISecIDG.class));
	}
	
	@Test
	public void testCtor2() {
		assertEquals("foo", service.getSecCode());
		assertEquals(220, service.getMarketID());
	}
	
	@Test
	public void testCtor1_SecIDF() {
		service = new TQSecIDG(new TQSecIDF("zulu24", 15, "UPS", "foobar", SecType.FUT));
		assertEquals("zulu24", service.getSecCode());
		assertEquals(15, service.getMarketID());
	}
	
	@Test
	public void testToString() {
		String expected = "TQSecIDG[secCode=foo,marketID=220]";
		
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
		assertTrue(service.equals(new TQSecIDG("foo", 220)));
		assertFalse(service.equals(new TQSecIDG("bar", 220)));
		assertFalse(service.equals(new TQSecIDG("foo", 110)));
		assertFalse(service.equals(new TQSecIDG("bar", 110)));
	}

}

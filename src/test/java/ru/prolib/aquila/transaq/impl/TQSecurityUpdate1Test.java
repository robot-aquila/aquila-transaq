package ru.prolib.aquila.transaq.impl;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.transaq.impl.TQSecField;
import ru.prolib.aquila.transaq.impl.TQSecID1;
import ru.prolib.aquila.transaq.impl.TQSecurityUpdate1;

public class TQSecurityUpdate1Test {
	private TQSecID1 sec_id;
	private DeltaUpdate update;
	private TQSecurityUpdate1 service;

	@Before
	public void setUp() throws Exception {
		sec_id = new TQSecID1("zulu24", 505);
		update = new DeltaUpdateBuilder()
				.withToken(TQSecField.ACTIVE, true)
				.withToken(TQSecField.DECIMALS, 2)
				.buildUpdate();
		service = new TQSecurityUpdate1(sec_id, update);
	}
	
	@Test
	public void testCtorX() {
		assertEquals(new TQSecID1("zulu24", 505), service.getSecID());
		assertEquals(new DeltaUpdateBuilder()
				.withToken(TQSecField.ACTIVE, true)
				.withToken(TQSecField.DECIMALS, 2)
				.buildUpdate(), service.getUpdate()
			);
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<TQSecID1> vSID = new Variant<TQSecID1>()
				.add(sec_id)
				.add(new TQSecID1("gambit", 247));
		Variant<DeltaUpdate> vUPD = new Variant<DeltaUpdate>(vSID)
				.add(update)
				.add(new DeltaUpdateBuilder()
						.withToken(TQSecField.ACTIVE, false)
						.buildUpdate()
					);
		Variant<?> iterator = vUPD;
		int foundCnt = 0;
		TQSecurityUpdate1 x, found = null;
		do {
			x = new TQSecurityUpdate1(vSID.get(), vUPD.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(new TQSecID1("zulu24", 505), found.getSecID());
		assertEquals(update, found.getUpdate());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(10087251, 7129)
				.append(sec_id)
				.append(update)
				.build();
		
		assertEquals(expected, service.hashCode());
	}

	@Test
	public void testToString() {
		String expected = new StringBuilder()
				.append("TQSecurityUpdate1[")
				.append("secID=TQSecID1[secCode=zulu24,marketID=505],")
				.append("update=DeltaUpdate[null {5203=true, 5207=2}]")
				.append("]")
				.toString();
		
		assertEquals(expected, service.toString());
	}

}

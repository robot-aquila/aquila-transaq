package ru.prolib.aquila.transaq.impl;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.transaq.entity.SecType;

public class TQSecurityUpdate3Test {
	private TQSecID_F sec_id;
	private DeltaUpdate update;
	private TQSecurityUpdate3 service;

	@Before
	public void setUp() throws Exception {
		sec_id = new TQSecID_F("ken", 95, "barbie", SecType.SHARE);
		update = new DeltaUpdateBuilder()
				.withToken(TQSecField.ACTIVE, true)
				.withToken(TQSecField.DECIMALS, 2)
				.buildUpdate();
		service = new TQSecurityUpdate3(sec_id, update);
	}
	
	@Test
	public void testCtorX() {
		assertEquals(new TQSecID_F("ken", 95, "barbie", SecType.SHARE), service.getSecID());
		assertEquals(new DeltaUpdateBuilder()
				.withToken(TQSecField.ACTIVE, true)
				.withToken(TQSecField.DECIMALS, 2)
				.buildUpdate(), service.getUpdate());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}

	@Test
	public void testEquals() {
		Variant<TQSecID_F> vSID = new Variant<TQSecID_F>()
				.add(sec_id)
				.add(new TQSecID_F("gambit", 247, "babaka", SecType.BOND));
		Variant<DeltaUpdate> vUPD = new Variant<DeltaUpdate>(vSID)
				.add(update)
				.add(new DeltaUpdateBuilder()
						.withToken(TQSecField.ACTIVE, false)
						.buildUpdate()
					);
		Variant<?> iterator = vUPD;
		int foundCnt = 0;
		TQSecurityUpdate3 x, found = null;
		do {
			x = new TQSecurityUpdate3(vSID.get(), vUPD.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(new TQSecID_F("ken", 95, "barbie", SecType.SHARE), found.getSecID());
		assertEquals(update, found.getUpdate());
	}

	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(1236564123, 9114243)
				.append(sec_id)
				.append(update)
				.build();
		
		assertEquals(expected, service.hashCode());
	}
	
	@Test
	public void testToString() {
		String expected = new StringBuilder()
				.append("TQSecurityUpdate3[")
				.append("secID=TQSecID_F[secCode=ken,marketID=95,shortName=barbie,type=SHARE],")
				.append("update=DeltaUpdate[null {5203=true, 5207=2}]")
				.append("]")
				.toString();
		
		assertEquals(expected, service.toString());
	}

}

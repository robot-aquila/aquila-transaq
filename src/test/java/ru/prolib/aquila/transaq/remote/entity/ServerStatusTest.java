package ru.prolib.aquila.transaq.remote.entity;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class ServerStatusTest {
	private ServerStatus service_conn, service_recv, service_error, service_prop;

	@Before
	public void setUp() throws Exception {
		service_conn = new ServerStatus(true, false, null);
		service_recv = new ServerStatus(false, true, null);
		service_error = new ServerStatus(false, false, "Test message");
		service_prop = new ServerStatus(true, true, "Props test");
	}
	
	public void testCtor1() {
		ServerStatus service = new ServerStatus(true);
		assertTrue(service.isConnected());
		assertFalse(service.isRecover());
		assertNull(service.getErrorMsg());
	}
	
	@Test
	public void testGetters() {
		assertTrue(service_conn.isConnected());
		assertFalse(service_conn.isRecover());
		assertFalse(service_conn.isError());
		assertNull(service_conn.getErrorMsg());
		
		assertFalse(service_recv.isConnected());
		assertTrue(service_recv.isRecover());
		assertFalse(service_recv.isError());
		assertNull(service_recv.getErrorMsg());
		
		assertFalse(service_error.isConnected());
		assertFalse(service_error.isRecover());
		assertTrue(service_error.isError());
		assertEquals("Test message", service_error.getErrorMsg());
		
		assertTrue(service_prop.isConnected());
		assertTrue(service_prop.isRecover());
		assertTrue(service_prop.isError());
		assertEquals("Props test", service_prop.getErrorMsg());
	}
	
	@Test
	public void testToString() {
		assertEquals("ServerStatus[connected=true,recover=false,errorMsg=<null>]", service_conn.toString());
		assertEquals("ServerStatus[connected=false,recover=true,errorMsg=<null>]", service_recv.toString());
		assertEquals("ServerStatus[connected=false,recover=false,errorMsg=Test message]", service_error.toString());
		assertEquals("ServerStatus[connected=true,recover=true,errorMsg=Props test]", service_prop.toString());
	}
	
	@Test
	public void testHashCode() {
		assertEquals((int) new HashCodeBuilder(1297015, 401)
				.append(true)
				.append(false)
				.append((String) null)
				.build(), service_conn.hashCode());
		assertEquals((int) new HashCodeBuilder(1297015, 401)
				.append(false)
				.append(true)
				.append((String) null)
				.build(), service_recv.hashCode());
		assertEquals((int) new HashCodeBuilder(1297015, 401)
				.append(false)
				.append(false)
				.append((String) "Test message")
				.build(), service_error.hashCode());
		assertEquals((int) new HashCodeBuilder(1297015, 401)
				.append(true)
				.append(true)
				.append((String) "Props test")
				.build(), service_prop.hashCode());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service_conn.equals(service_conn));
		assertFalse(service_conn.equals(null));
		assertFalse(service_conn.equals(this));
	}

	@Test
	public void testEquals() {
		Variant<Boolean> vConn = new Variant<>(true, false);
		Variant<Boolean> vRecv = new Variant<>(vConn, true, false);
		Variant<String> vErr = new Variant<>(vRecv, "Props test", "Hello, world");
		Variant<?> iterator = vErr;
		int found_cnt = 0;
		ServerStatus x, found = null;
		do {
			x = new ServerStatus(vConn.get(), vRecv.get(), vErr.get());
			if ( service_prop.equals(x) ) {
				found_cnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, found_cnt);
		assertEquals(true, found.isConnected());
		assertEquals(true, found.isRecover());
		assertEquals(true, found.isError());
		assertEquals("Props test", found.getErrorMsg());
	}

}

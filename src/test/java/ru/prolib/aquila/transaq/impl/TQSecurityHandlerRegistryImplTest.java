package ru.prolib.aquila.transaq.impl;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class TQSecurityHandlerRegistryImplTest {
	private IMocksControl control;
	private TQSecurityHandler shMock1, shMock2, shMock3;
	private Map<TQSecID1, TQSecurityHandler> map_sec_id1;
	private Map<TQSecID3, TQSecurityHandler> map_sec_id3;
	private TQSecurityHandlerRegistryImpl service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		shMock1 = control.createMock(TQSecurityHandler.class);
		shMock2 = control.createMock(TQSecurityHandler.class);
		shMock3 = control.createMock(TQSecurityHandler.class);
		map_sec_id1 = new LinkedHashMap<>();
		map_sec_id3 = new LinkedHashMap<>();
		service = new TQSecurityHandlerRegistryImpl(map_sec_id1, map_sec_id3);
	}
	
	@Test
	public void testGetHandlerOrNull_ID3() {
		map_sec_id3.put(new TQSecID3("app", 87, "ppa"), shMock1);
		map_sec_id3.put(new TQSecID3("bam", 21, "gap"), shMock2);
		
		assertNull(service.getHandlerOrNull(new TQSecID3("gap", 22, "map")));
		
		map_sec_id3.put(new TQSecID3("gap", 22, "map"), shMock3);
		
		assertSame(shMock3, service.getHandlerOrNull(new TQSecID3("gap", 22, "map")));
	}
	
	@Test
	public void testGetHandler_ID2() {
		map_sec_id1.put(new TQSecID1("foo", 12), shMock1);
		map_sec_id1.put(new TQSecID1("bar", 13), shMock2);
		map_sec_id1.put(new TQSecID1("buz", 14), shMock3);
		
		assertSame(shMock2, service.getHandler(new TQSecID1("bar", 13)));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testGetHandler_ID2_ThrowsIfNotExists() {
		map_sec_id1.put(new TQSecID1("foo", 12), shMock1);
		map_sec_id1.put(new TQSecID1("bar", 13), shMock2);
		map_sec_id1.put(new TQSecID1("buz", 14), shMock3);

		service.getHandler(new TQSecID1("zulu24", 586));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testRegisterHandler_ThrowsIfExists() {
		map_sec_id3.put(new TQSecID3("foo", 12, "gap"), shMock1);
		map_sec_id3.put(new TQSecID3("bar", 13, "pag"), shMock2);
		expect(shMock3.getSecID3()).andStubReturn(new TQSecID3("foo", 12, "gap"));
		control.replay();

		service.registerHandler(shMock3);
	}

	@Test
	public void testRegisterHandler() {
		map_sec_id3.put(new TQSecID3("foo", 12, "gap"), shMock1);
		map_sec_id3.put(new TQSecID3("bar", 13, "pag"), shMock2);
		expect(shMock3.getSecID3()).andStubReturn(new TQSecID3("boo", 15, "var"));
		control.replay();

		service.registerHandler(shMock3);
		
		control.verify();
		assertSame(shMock3, map_sec_id3.get(new TQSecID3("boo", 15, "var")));
		assertSame(shMock3, map_sec_id1.get(new TQSecID1("boo", 15)));
	}

}

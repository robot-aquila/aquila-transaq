package ru.prolib.aquila.transaq.impl;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.entity.SecType;
import ru.prolib.aquila.transaq.remote.TQSecIDG;
import ru.prolib.aquila.transaq.remote.ISecIDF;
import ru.prolib.aquila.transaq.remote.ISecIDG;
import ru.prolib.aquila.transaq.remote.TQSecIDF;

public class TQSecurityHandlerRegistryImplTest {
	private IMocksControl control;
	private TQSecurityHandler shMock1, shMock2, shMock3;
	private Map<ISecIDG, TQSecurityHandler> map_sec_id1;
	private Map<ISecIDF, TQSecurityHandler> map_sec_id3;
	private TQSecurityHandlerRegistry service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		shMock1 = control.createMock(TQSecurityHandler.class);
		shMock2 = control.createMock(TQSecurityHandler.class);
		shMock3 = control.createMock(TQSecurityHandler.class);
		map_sec_id1 = new LinkedHashMap<>();
		map_sec_id3 = new LinkedHashMap<>();
		service = new TQSecurityHandlerRegistry(map_sec_id1, map_sec_id3);
	}
	
	@Test
	public void testGetHandlerOrNull_FullID() {
		map_sec_id3.put(new TQSecIDF("app", 87, "SUSE", "ppa", SecType.BOND), shMock1);
		map_sec_id3.put(new TQSecIDF("bam", 21, "FUSE", "gap", SecType.SHARE), shMock2);
		
		assertNull(service.getHandlerOrNull(new TQSecIDF("gap", 22, "PASE", "map", SecType.FUT)));
		
		map_sec_id3.put(new TQSecIDF("gap", 22, "PASE", "map", SecType.FUT), shMock3);
		
		assertSame(shMock3, service.getHandlerOrNull(new TQSecIDF("gap", 22, "PASE", "map", SecType.FUT)));
	}
	
	@Test
	public void testGetHandler_ShortID() {
		map_sec_id1.put(new TQSecIDG("foo", 12), shMock1);
		map_sec_id1.put(new TQSecIDG("bar", 13), shMock2);
		map_sec_id1.put(new TQSecIDG("buz", 14), shMock3);
		
		assertSame(shMock2, service.getHandler(new TQSecIDG("bar", 13)));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testGetHandler_ShortID_ThrowsIfNotExists() {
		map_sec_id1.put(new TQSecIDG("foo", 12), shMock1);
		map_sec_id1.put(new TQSecIDG("bar", 13), shMock2);
		map_sec_id1.put(new TQSecIDG("buz", 14), shMock3);

		service.getHandler(new TQSecIDG("zulu24", 586));
	}
	
	@Test (expected=IllegalStateException.class)
	public void testRegisterHandler_ThrowsIfExists() {
		map_sec_id3.put(new TQSecIDF("foo", 12, "BOBS", "gap", SecType.BOND), shMock1);
		map_sec_id3.put(new TQSecIDF("bar", 13, "CUPS", "pag", SecType.GKO), shMock2);
		expect(shMock3.getSecID3()).andStubReturn(new TQSecIDF("foo", 12, "BOBS", "gap", SecType.BOND));
		control.replay();

		service.registerHandler(shMock3);
	}

	@Test
	public void testRegisterHandler() {
		map_sec_id3.put(new TQSecIDF("foo", 12, "BOBS", "gap", SecType.BOND), shMock1);
		map_sec_id3.put(new TQSecIDF("bar", 13, "CUPS", "pag", SecType.GKO), shMock2);
		expect(shMock3.getSecID3()).andStubReturn(new TQSecIDF("boo", 15, "ENOS", "var", SecType.FUT));
		control.replay();

		service.registerHandler(shMock3);
		
		control.verify();
		assertSame(shMock3, map_sec_id3.get(new TQSecIDF("boo", 15, "ENOS", "var", SecType.FUT)));
		assertSame(shMock3, map_sec_id1.get(new TQSecIDG("boo", 15)));
	}

}

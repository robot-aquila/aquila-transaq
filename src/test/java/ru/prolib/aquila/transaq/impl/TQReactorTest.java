package ru.prolib.aquila.transaq.impl;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.transaq.entity.SecType;

public class TQReactorTest {
	private IMocksControl control;
	private TQDirectory dirMock;
	private TQSecurityHandlerRegistry shrMock;
	private TQSecurityHandlerFactory shfMock;
	private TQSecurityHandler shMock1;
	private TQReactor service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dirMock = control.createMock(TQDirectory.class);
		shrMock = control.createMock(TQSecurityHandlerRegistry.class);
		shfMock = control.createMock(TQSecurityHandlerFactory.class);
		shMock1 = control.createMock(TQSecurityHandler.class);
		service = new TQReactor(dirMock, shrMock, shfMock);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateMarkets() {
		TQStateUpdate<Integer> upMock = control.createMock(TQStateUpdate.class);
		dirMock.updateMarket(upMock);
		control.replay();
		
		service.updateMarket(upMock);
		
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateBoard() {
		TQStateUpdate<String> upMock = control.createMock(TQStateUpdate.class);
		dirMock.updateBoard(upMock);
		control.replay();
		
		service.updateBoard(upMock);
		
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateCandleKind() {
		TQStateUpdate<Integer> upMock = control.createMock(TQStateUpdate.class);
		dirMock.updateCKind(upMock);
		control.replay();
		
		service.updateCandleKind(upMock);
		
		control.verify();
	}
	
	@Test
	public void testUpdateSecurity_U1() {
		TQSecID1 sec_id = new TQSecID1("foo", 26);
		DeltaUpdate duMock = control.createMock(DeltaUpdate.class);
		TQStateUpdate<TQSecID1> update = new TQStateUpdate<>(sec_id, duMock);
		dirMock.updateSecurityParams(update);
		expect(shrMock.getHandler(sec_id)).andReturn(shMock1);
		shMock1.update(duMock);
		control.replay();
		
		service.updateSecurity1(update);
		
		control.verify();
	}

	@Test
	public void testUpdateSecurity_U3_NewHandler() {
		TQSecID_F sec_id3 = new TQSecID_F("foo", 7, "bar", SecType.BOND);
		DeltaUpdate duMock = control.createMock(DeltaUpdate.class);
		TQStateUpdate<TQSecID_F> update = new TQStateUpdate<>(sec_id3, duMock);
		TQStateUpdate<TQSecID1> update1 = new TQStateUpdate<>(new TQSecID1("foo", 7), duMock);
		dirMock.updateSecurityParams(update1);
		expect(shrMock.getHandlerOrNull(sec_id3)).andReturn(null);
		expect(shfMock.createHandler(sec_id3)).andReturn(shMock1);
		shMock1.update(duMock);
		shrMock.registerHandler(shMock1);
		control.replay();
		
		service.updateSecurityF(update);
		
		control.verify();
	}

	@Test
	public void testUpdateSecurity_U3_ExistingHandler() {
		TQSecID_F sec_id3 = new TQSecID_F("buz", 8, "bar", SecType.GKO);
		DeltaUpdate duMock = control.createMock(DeltaUpdate.class);
		TQStateUpdate<TQSecID_F> update = new TQStateUpdate<>(sec_id3, duMock);
		TQStateUpdate<TQSecID1> update1 = new TQStateUpdate<>(new TQSecID1("buz", 8), duMock);
		dirMock.updateSecurityParams(update1);
		expect(shrMock.getHandlerOrNull(sec_id3)).andReturn(shMock1);
		shMock1.update(duMock);
		control.replay();
		
		service.updateSecurityF(update);
		
		control.verify();
	}

}

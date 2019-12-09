package ru.prolib.aquila.transaq.impl;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.engine.SymbolDataService;
import ru.prolib.aquila.transaq.entity.SecType;
import ru.prolib.aquila.transaq.remote.TQSecIDT;
import ru.prolib.aquila.transaq.remote.entity.ServerStatus;
import ru.prolib.aquila.transaq.remote.ISecIDF;
import ru.prolib.aquila.transaq.remote.ISecIDG;
import ru.prolib.aquila.transaq.remote.ISecIDT;
import ru.prolib.aquila.transaq.remote.TQSecIDF;
import ru.prolib.aquila.transaq.remote.TQSecIDG;

public class TQReactorTest {
	private IMocksControl control;
	private ServiceLocator services;
	private TQDirectory dirMock;
	private SymbolDataService sdsMock;
	private TQSecurityHandlerRegistry shrMock;
	private TQSecurityHandlerFactory shfMock;
	private TQSecurityHandler shMock1;
	private DeltaUpdate duMock;
	private TQReactor service;

	@Before
	public void setUp() throws Exception {
		services = new ServiceLocator();
		control = createStrictControl();
		services.setDirectory(dirMock = control.createMock(TQDirectory.class));
		services.setSymbolDataService(sdsMock = control.createMock(SymbolDataService.class));
		shrMock = control.createMock(TQSecurityHandlerRegistry.class);
		shfMock = control.createMock(TQSecurityHandlerFactory.class);
		shMock1 = control.createMock(TQSecurityHandler.class);
		duMock = control.createMock(DeltaUpdate.class);
		service = new TQReactor(services, shrMock, shfMock);
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
	public void testUpdateSecurity1_U1() {
		TQSecIDG sec_id = new TQSecIDG("foo", 26);
		TQStateUpdate<ISecIDG> update = new TQStateUpdate<>(sec_id, duMock);
		dirMock.updateSecurityParamsP(update);
		expect(shrMock.getHandler(sec_id)).andReturn(shMock1);
		shMock1.update(duMock);
		control.replay();
		
		service.updateSecurity1(update);
		
		control.verify();
	}

	@Test
	public void testUpdateSecurityF_U3_NewHandler() {
		TQSecIDF sec_id3 = new TQSecIDF("foo", 7, "OPT", "bar", SecType.BOND);
		TQStateUpdate<ISecIDF> update = new TQStateUpdate<>(sec_id3, duMock);
		dirMock.updateSecurityParamsF(update);
		expect(shrMock.getHandlerOrNull(sec_id3)).andReturn(null);
		expect(shfMock.createHandler(sec_id3)).andReturn(shMock1);
		shMock1.update(duMock);
		shrMock.registerHandler(shMock1);
		control.replay();
		
		service.updateSecurityF(update);
		
		control.verify();
	}

	@Test
	public void testUpdateSecurityF_U3_ExistingHandler() {
		TQSecIDF sec_id3 = new TQSecIDF("buz", 8, "BOSS", "bar", SecType.GKO);
		TQStateUpdate<ISecIDF> update = new TQStateUpdate<>(sec_id3, duMock);
		dirMock.updateSecurityParamsF(update);
		expect(shrMock.getHandlerOrNull(sec_id3)).andReturn(shMock1);
		shMock1.update(duMock);
		control.replay();
		
		service.updateSecurityF(update);
		
		control.verify();
	}
	
	@Test
	public void testUpdateSecurityBoard() {
		TQSecIDT sec_id = new TQSecIDT("foo", "bar");
		TQStateUpdate<ISecIDT> update = new TQStateUpdate<>(sec_id, duMock);
		dirMock.updateSecurityBoardParams(update);
		control.replay();
		
		service.updateSecurityBoard(update);
		
		control.verify();
	}
	
	@Test
	public void testUpdateServerStatus() {
		sdsMock.onConnectionStatusChange(true);
		sdsMock.onConnectionStatusChange(false);
		control.replay();
		
		service.updateServerStatus(new ServerStatus(true));
		service.updateServerStatus(new ServerStatus(false));
		
		control.verify();
	}
	
	@Test
	public void testUpdateSecurityQuotations() {
		ISecIDT sec_id = new TQSecIDT("SBER", "XXL");
		dirMock.updateSecurityQuotations(new TQStateUpdate<>(sec_id, duMock));
		control.replay();
		
		service.updateSecurityQuotations(new TQStateUpdate<>(sec_id, duMock));
		
		control.verify();
	}

}

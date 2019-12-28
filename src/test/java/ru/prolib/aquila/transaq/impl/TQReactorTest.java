package ru.prolib.aquila.transaq.impl;

import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.engine.sds.SymbolDataService;
import ru.prolib.aquila.transaq.entity.SecType;
import ru.prolib.aquila.transaq.remote.TQSecIDT;
import ru.prolib.aquila.transaq.remote.entity.Quote;
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
	private DeltaUpdate duMock;
	private TQReactor service;

	@Before
	public void setUp() throws Exception {
		services = new ServiceLocator();
		control = createStrictControl();
		services.setDirectory(dirMock = control.createMock(TQDirectory.class));
		services.setSymbolDataService(sdsMock = control.createMock(SymbolDataService.class));
		duMock = control.createMock(DeltaUpdate.class);
		service = new TQReactor(services);
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
	public void testUpdateSecurity1() {
		TQSecIDG sec_id = new TQSecIDG("foo", 26);
		TQStateUpdate<ISecIDG> update = new TQStateUpdate<>(sec_id, duMock);
		sdsMock.onSecurityUpdateG(update);
		control.replay();
		
		service.updateSecurity1(update);
		
		control.verify();
	}

	@Test
	public void testUpdateSecurityF() {
		TQSecIDF sec_id3 = new TQSecIDF("foo", 7, "OPT", "bar", SecType.BOND);
		TQStateUpdate<ISecIDF> update = new TQStateUpdate<>(sec_id3, duMock);
		sdsMock.onSecurityUpdateF(update);
		control.replay();
		
		service.updateSecurityF(update);
		
		control.verify();
	}
	
	@Test
	public void testUpdateSecurityBoard() {
		TQSecIDT sec_id = new TQSecIDT("foo", "bar");
		TQStateUpdate<ISecIDT> update = new TQStateUpdate<>(sec_id, duMock);
		sdsMock.onSecurityBoardUpdate(update);
		control.replay();
		
		service.updateSecurityBoard(update);
		
		control.verify();
	}
	
	@Test
	public void testUpdateServerStatus_Case1() {
		dirMock.updateConnectionStatus(true);
		sdsMock.onConnectionStatusChange(true);
		dirMock.updateConnectionStatus(false);
		sdsMock.onConnectionStatusChange(false);
		control.replay();
		
		service.updateServerStatus(new ServerStatus(true));
		service.updateServerStatus(new ServerStatus(false));

		
		control.verify();
	}
	
	@Test
	public void testUpdateSecurityQuotations() {
		ISecIDT sec_id = new TQSecIDT("SBER", "XXL");
		TQStateUpdate<ISecIDT> update = new TQStateUpdate<>(sec_id, duMock);
		sdsMock.onSecurityQuotationUpdate(update);
		control.replay();
		
		service.updateSecurityQuotations(update);
		
		control.verify();
	}
	
	@Test
	public void testRegisterTrade() {
		ISecIDT sec_id = new TQSecIDT("GAZP", "TQBR");
		TQStateUpdate<ISecIDT> update = new TQStateUpdate<>(sec_id, duMock);
		sdsMock.onSecurityTrade(update);
		control.replay();
		
		service.registerTrade(update);
		
		control.verify();
	}
	
	@Test
	public void testRegisterQuotes() {
		List<Quote> quotes = new ArrayList<>();
		quotes.add(control.createMock(Quote.class));
		quotes.add(control.createMock(Quote.class));
		quotes.add(control.createMock(Quote.class));
		sdsMock.onSecurityQuotes(quotes);
		control.replay();
		
		service.registerQuotes(quotes);
		
		control.verify();
	}

}

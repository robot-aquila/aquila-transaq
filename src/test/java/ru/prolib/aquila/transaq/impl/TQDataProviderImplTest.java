package ru.prolib.aquila.transaq.impl;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class TQDataProviderImplTest {
	private IMocksControl control;
	private EditableSecurity securityMock;
	private EditablePortfolio portfolioMock;
	private TQDataProviderImpl service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		securityMock = control.createMock(EditableSecurity.class);
		service = new TQDataProviderImpl();
	}
	
	@Test
	public void testSubscribeStateUpdates_Security() {
		control.replay();
		
		service.subscribeStateUpdates(securityMock);
		
		control.verify();
	}
	
	@Test
	public void testSubscribeLevel1Data() {
		control.replay();
		
		service.subscribeLevel1Data(new Symbol("foo"), securityMock);
		
		control.verify();
	}
	
	@Test
	public void testSubscribeLevel2Data() {
		control.replay();
		
		service.subscribeLevel2Data(new Symbol("bar"), securityMock);
		
		control.verify();
	}
	
	@Test (expected=UnsupportedOperationException.class)
	public void testSubscribeStateUpdates_Portfolio() {
		control.replay();
		
		service.subscribeStateUpdates(portfolioMock);
		
	}
	
	@Test (expected=UnsupportedOperationException.class)
	public void testGetNextOrderID() {
		control.replay();
		
		service.getNextOrderID();
		
	}
	
	@Test (expected=UnsupportedOperationException.class)
	public void testRegisterNewOrder() throws Exception {
		control.replay();
		
		service.registerNewOrder(null);
		
	}
	
	@Test (expected=UnsupportedOperationException.class)
	public void testCancelOrder() throws Exception {
		control.replay();
		
		service.cancelOrder(null);
	}

	@Ignore
	@Test
	public void testSubscribeRemoteObject() {
		fail("Not yet implemented");
	}
	
	@Ignore
	@Test
	public void testUnsubscribeRemoteObject() {
		fail("Not yet implemented");
	}

}

package ru.prolib.aquila.transaq.impl;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class TQDataProviderImplTest {
	private IMocksControl control;
	private EditableSecurity securityMock;
	private EditablePortfolio portfolioMock;
	private EditableTerminal terminalMock;
	private TQConnectorFactory factoryMock;
	private TQConnector connMock;
	private TQDataProviderImpl service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		securityMock = control.createMock(EditableSecurity.class);
		portfolioMock = control.createMock(EditablePortfolio.class);
		terminalMock = control.createMock(EditableTerminal.class);
		factoryMock = control.createMock(TQConnectorFactory.class);
		connMock = control.createMock(TQConnector.class);
		service = new TQDataProviderImpl(factoryMock);
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

	@Test
	public void testSubscribeRemoteObject() throws Exception {
		expect(factoryMock.createInstance(terminalMock)).andReturn(connMock);
		connMock.init();
		connMock.connect();
		control.replay();
		
		service.subscribeRemoteObjects(terminalMock);
		
		control.verify();
	}
	
	@Test
	public void testUnsubscribeRemoteObject() throws Exception {
		expect(factoryMock.createInstance(terminalMock)).andReturn(connMock);
		connMock.init();
		connMock.connect();
		control.replay();
		service.subscribeRemoteObjects(terminalMock);
		control.resetToStrict();
		
		connMock.disconnect();
		connMock.close();
		control.replay();
		
		service.unsubscribeRemoteObjects(terminalMock);
		
		control.verify();
	}

}

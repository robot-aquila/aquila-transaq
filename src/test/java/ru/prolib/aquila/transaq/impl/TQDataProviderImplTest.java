package ru.prolib.aquila.transaq.impl;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class TQDataProviderImplTest {
	private IMocksControl control;
	private EditableTerminal terminalMock;
	private TQConnectorFactory factoryMock;
	private TQConnector connMock;
	private TQDataProviderImpl service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminalMock = control.createMock(EditableTerminal.class);
		factoryMock = control.createMock(TQConnectorFactory.class);
		connMock = control.createMock(TQConnector.class);
		service = new TQDataProviderImpl(factoryMock);
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
	
	@Test
	public void testSubscribe_Symbol() throws Exception {
		control.replay();
		
		service.subscribe(new Symbol("foo"), terminalMock);
		
		control.verify();
	}
	
	@Test
	public void testUnsubscribe_Symbol() throws Exception {
		control.replay();
		
		service.unsubscribe(new Symbol("bar"), terminalMock);
		
		control.verify();
	}
	
	@Test
	public void testSubscribe_Account() throws Exception {
		control.replay();
		
		service.subscribe(new Account("charlie"), terminalMock);
		
		control.verify();
	}
	
	@Test
	public void gtestUnsubscribe_Account() throws Exception {
		control.replay();
		
		service.unsubscribe(new Account("gamma"), terminalMock);
		
		control.verify();
	}

}

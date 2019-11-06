package ru.prolib.aquila.transaq.impl;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.transaq.engine.Engine;
import ru.prolib.aquila.transaq.engine.EngineBuilder;
import ru.prolib.aquila.transaq.engine.ServiceLocator;

public class TQDataProviderImplTest {
	private IMocksControl control;
	private EditableTerminal terminalMock;
	private TQConnector connMock;
	private Engine engineMock;
	private EngineBuilder engBuilderMock;
	private ServiceLocator engServicesMock;
	private TQDataProviderImpl service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminalMock = control.createMock(EditableTerminal.class);
		connMock = control.createMock(TQConnector.class);
		engineMock = control.createMock(Engine.class);
		engBuilderMock = control.createMock(EngineBuilder.class);
		engServicesMock = control.createMock(ServiceLocator.class);
		service = new TQDataProviderImpl(connMock, engineMock, engBuilderMock, engServicesMock);
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
		engBuilderMock.initSecondary(engServicesMock, terminalMock);
		connMock.connect();
		control.replay();
		
		service.subscribeRemoteObjects(terminalMock);
		
		control.verify();
	}
	
	@Test
	public void testUnsubscribeRemoteObject() throws Exception {
		connMock.disconnect();
		control.replay();
		
		service.unsubscribeRemoteObjects(terminalMock);
		
		control.verify();
	}
	
	@Test
	public void testSubscribe_Symbol() throws Exception {
		control.replay();
		
		service.subscribe(new Symbol("foo"), MDLevel.L1_BBO, terminalMock);
		
		control.verify();
	}
	
	@Test
	public void testUnsubscribe_Symbol() throws Exception {
		control.replay();
		
		service.unsubscribe(new Symbol("bar"), MDLevel.L1, terminalMock);
		
		control.verify();
	}
	
	@Test
	public void testSubscribe_Account() throws Exception {
		control.replay();
		
		service.subscribe(new Account("charlie"), terminalMock);
		
		control.verify();
	}
	
	@Test
	public void testUnsubscribe_Account() throws Exception {
		control.replay();
		
		service.unsubscribe(new Account("gamma"), terminalMock);
		
		control.verify();
	}
	
	@Test
	public void testClose() {
		connMock.close();
		engineMock.shutdown();
		control.replay();
		
		service.close();
		
		control.verify();
	}

}

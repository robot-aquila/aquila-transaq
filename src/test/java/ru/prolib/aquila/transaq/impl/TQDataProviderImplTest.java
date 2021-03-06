package ru.prolib.aquila.transaq.impl;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandlerStub;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.transaq.engine.Engine;
import ru.prolib.aquila.transaq.engine.EngineBuilder;
import ru.prolib.aquila.transaq.engine.ServiceLocator;

public class TQDataProviderImplTest {
	private IMocksControl control;
	private EditableTerminal terminalMock;
	private Engine engMock;
	private EngineBuilder engBuilderMock;
	private ServiceLocator engServicesMock;
	private TQDataProviderImpl service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminalMock = control.createMock(EditableTerminal.class);
		engMock = control.createMock(Engine.class);
		engBuilderMock = control.createMock(EngineBuilder.class);
		engServicesMock = control.createMock(ServiceLocator.class);
		service = new TQDataProviderImpl(engMock, engBuilderMock, engServicesMock);
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
		engMock.connect();
		control.replay();
		
		service.subscribeRemoteObjects(terminalMock);
		
		control.verify();
	}
	
	@Test
	public void testUnsubscribeRemoteObject() throws Exception {
		engMock.disconnect();
		control.replay();
		
		service.unsubscribeRemoteObjects(terminalMock);
		
		control.verify();
	}
	
	@Test
	public void testSubscribe_Symbol() throws Exception {
		engMock.subscribeSymbol(new Symbol("foo"), MDLevel.L1_BBO);
		control.replay();
		
		SubscrHandler actual = service.subscribe(new Symbol("foo"), MDLevel.L1_BBO, terminalMock);
		
		control.verify();
		SubscrHandler expected = new TQSymbolSubscrHandler(engMock, new Symbol("foo"), MDLevel.L1_BBO);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSubscribe_Account() throws Exception {
		control.replay();
		
		SubscrHandler actual = service.subscribe(new Account("charlie"), terminalMock);
		
		control.verify();
		assertEquals(SubscrHandlerStub.class, actual.getClass());
	}
	
	@Test
	public void testClose() {
		engMock.shutdown();
		control.replay();
		
		service.close();
		
		control.verify();
	}

}

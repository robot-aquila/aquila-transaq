package ru.prolib.aquila.transaq.impl;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.engine.Engine;
import ru.prolib.aquila.transaq.remote.MessageInterceptor;

public class TransaqHandlerTest {
	private IMocksControl control;
	private Engine engineMock;
	private MessageInterceptor interceptorMock;
	private TransaqHandler service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		engineMock = control.createMock(Engine.class);
		interceptorMock = control.createMock(MessageInterceptor.class);
		service = new TransaqHandler(engineMock, interceptorMock);
	}

	@Test
	public void testHandle() {
		interceptorMock.incoming("foobar");
		engineMock.messageFromServer("foobar");
		control.replay();
		
		service.Handle("foobar");
		
		control.verify();
	}

}

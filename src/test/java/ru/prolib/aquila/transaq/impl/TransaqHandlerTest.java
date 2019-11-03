package ru.prolib.aquila.transaq.impl;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.engine.Engine;

public class TransaqHandlerTest {
	private IMocksControl control;
	private Engine engineMock;
	private TransaqHandler service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		engineMock = control.createMock(Engine.class);
		service = new TransaqHandler(engineMock);
	}

	@Test
	public void testHandle() {
		engineMock.messageFromServer("foobar");
		control.replay();
		
		service.Handle("foobar");
		
		control.verify();
	}

}

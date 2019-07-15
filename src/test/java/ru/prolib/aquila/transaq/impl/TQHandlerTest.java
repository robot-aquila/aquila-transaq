package ru.prolib.aquila.transaq.impl;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class TQHandlerTest {
	private IMocksControl control;
	private TQMessageRouter routerMock;
	private TQHandler service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		routerMock = control.createMock(TQMessageRouter.class);
		service = new TQHandler(routerMock);
	}

	@Test
	public void testHandle() {
		routerMock.dispatchMessage("foobar");
		control.replay();
		
		service.Handle("foobar");
		
		control.verify();
	}

}

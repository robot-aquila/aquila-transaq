package ru.prolib.aquila.transaq.engine;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.transaq.engine.mp.MessageRouter;
import ru.prolib.aquila.transaq.impl.TQConnector;
import ru.prolib.aquila.transaq.impl.TQDirectory;
import ru.prolib.aquila.transaq.impl.TQParser;
import ru.prolib.aquila.transaq.impl.TQReactor;

public class ServiceLocatorTest {
	@Rule
	public ExpectedException eex = ExpectedException.none();
	private IMocksControl control;
	private TQParser parserMock;
	private TQReactor reactorMock;
	private TQDirectory dirMock;
	private MessageRouter mrouterMock;
	private TQConnector connMock;
	private SecurityDataService sdsMock;
	private ServiceLocator service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		parserMock = control.createMock(TQParser.class);
		reactorMock = control.createMock(TQReactor.class);
		dirMock = control.createMock(TQDirectory.class);
		mrouterMock = control.createMock(MessageRouter.class);
		connMock = control.createMock(TQConnector.class);
		sdsMock = control.createMock(SecurityDataService.class);
		service = new ServiceLocator();
	}
	
	@Test
	public void testGetParser_ThrowsIfNotDefined() {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Parser was not defined");
		control.replay();
		
		service.getParser();
	}
	
	@Test
	public void testGetParser() {
		service.setParser(parserMock);
		control.replay();
		
		assertSame(parserMock, service.getParser());
		
		control.verify();
	}
	
	@Test
	public void testGetReactor_ThrowsIfNotDefined() {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Reactor was not defined");
		control.replay();
		
		service.getReactor();
	}

	@Test
	public void testGetReactor() {
		service.setReactor(reactorMock);
		control.replay();
		
		assertSame(reactorMock, service.getReactor());
		
		control.verify();
	}
	
	@Test
	public void testGetDirectory_ThrowsIfNotDefined() {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Directory was not defined");
		control.replay();
		
		service.getDirectory();
	}
	
	@Test
	public void testGetDirectory() {
		service.setDirectory(dirMock);
		control.replay();
		
		assertSame(dirMock, service.getDirectory());
		
		control.verify();
	}
	
	@Test
	public void testGetMessageRouter_ThrowsIfNotDefined() {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Message router was not defined");
		control.replay();
		
		service.getMessageRouter();
	}
	
	@Test
	public void testGetMessageRouter() {
		service.setMessageRouter(mrouterMock);
		control.replay();
		
		assertSame(mrouterMock, service.getMessageRouter());
		
		control.verify();
	}
	
	@Test
	public void testGetConnector_ThrowsIfNotDefined() {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Connector was not defined");
		control.replay();
		
		service.getConnector();
	}
	
	@Test
	public void testGetConnector() {
		service.setConnector(connMock);
		control.replay();
		
		assertSame(connMock, service.getConnector());
		
		control.verify();
	}
	
	@Test
	public void testGetSecurityDataService_ThrowsIfNotDefined() {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Security data service was not defined");
		control.replay();
		
		service.getSecurityDataService();
	}
	
	@Test
	public void testGetSecurityDataService() {
		service.setSecurityDataService(sdsMock);
		control.replay();
		
		assertSame(sdsMock, service.getSecurityDataService());

		control.verify();
	}

}

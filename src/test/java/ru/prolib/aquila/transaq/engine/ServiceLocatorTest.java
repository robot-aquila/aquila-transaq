package ru.prolib.aquila.transaq.engine;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.transaq.engine.mp.MessageRouter;
import ru.prolib.aquila.transaq.impl.TQDirectory;
import ru.prolib.aquila.transaq.impl.TQFieldAssembler;
import ru.prolib.aquila.transaq.impl.TQParser;
import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.remote.Connector;

public class ServiceLocatorTest {
	@Rule
	public ExpectedException eex = ExpectedException.none();
	private IMocksControl control;
	private TQParser parserMock;
	private TQReactor reactorMock;
	private TQDirectory dirMock;
	private MessageRouter mrouterMock;
	private Connector connMock;
	private TQFieldAssembler asmMock;
	private SymbolDataService sdsMock;
	private EditableTerminal termMock;
	private EventQueue queueMock;
	private ServiceLocator service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		parserMock = control.createMock(TQParser.class);
		reactorMock = control.createMock(TQReactor.class);
		dirMock = control.createMock(TQDirectory.class);
		mrouterMock = control.createMock(MessageRouter.class);
		connMock = control.createMock(Connector.class);
		sdsMock = control.createMock(SymbolDataService.class);
		asmMock = control.createMock(TQFieldAssembler.class);
		termMock = control.createMock(EditableTerminal.class);
		queueMock = control.createMock(EventQueue.class);
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
	public void testGetSymbolDataService_ThrowsIfNotDefined() {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Symbol data service was not defined");
		control.replay();
		
		service.getSymbolDataService();
	}
	
	@Test
	public void testGetSymbolDataService() {
		service.setSymbolDataService(sdsMock);
		control.replay();
		
		assertSame(sdsMock, service.getSymbolDataService());

		control.verify();
	}
	
	@Test
	public void testGetAssembler_ThrowsIfNotDefined() {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Field assembler was not defined");
		control.replay();
		
		service.getAssembler();
	}
	
	@Test
	public void testGetAssembler() {
		service.setAssembler(asmMock);
		control.replay();
		
		assertSame(asmMock, service.getAssembler());
		
		control.verify();
	}
	
	@Test
	public void testGetTerminal_ThrowsIfNotDefined() {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Terminal was not defined");
		control.replay();
		
		service.getTerminal();
	}
	
	@Test
	public void testGetTerminal() {
		service.setTerminal(termMock);
		control.replay();
		
		assertSame(termMock, service.getTerminal());
		
		control.verify();
	}
	
	@Test
	public void testGetEventQueue_ThrowsIfNotDefined() {
		eex.expect(IllegalStateException.class);
		eex.expectMessage("Event queue was not defined");
		control.replay();
		
		service.getEventQueue();
	}
	
	@Test
	public void testGetEventQueue() {
		service.setEventQueue(queueMock);
		control.replay();
		
		assertSame(queueMock, service.getEventQueue());
		
		control.verify();
	}
	
}

package ru.prolib.aquila.transaq.engine;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
	private ServiceLocator service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		parserMock = control.createMock(TQParser.class);
		reactorMock = control.createMock(TQReactor.class);
		dirMock = control.createMock(TQDirectory.class);
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

}

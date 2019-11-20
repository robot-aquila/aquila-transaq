package ru.prolib.aquila.transaq.engine;

import static org.junit.Assert.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;

import static org.easymock.EasyMock.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.transaq.engine.mp.MessageRouter;
import ru.prolib.aquila.transaq.impl.TQConnector;

public class EngineCmdProcessorTest {
	private static Symbol symbol = new Symbol("kappa");
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	private IMocksControl control;
	private BlockingQueue<Cmd> queue;
	private ServiceLocator serviceLocator;
	private TQConnector connMock;
	private MessageRouter mrouterMock;
	private SecurityDataService sdsMock;
	private EngineCmdProcessor service;
	private Thread thread;
	private Cmd cmd;

	@Before
	public void setUp() throws Exception {
		cmd = null;
		control = createStrictControl();
		queue = new LinkedBlockingQueue<>(1);
		serviceLocator = new ServiceLocator();
		serviceLocator.setConnector(connMock = control.createMock(TQConnector.class));
		serviceLocator.setMessageRouter(mrouterMock = control.createMock(MessageRouter.class));
		serviceLocator.setSecurityDataService(sdsMock = control.createMock(SecurityDataService.class));
		service = new EngineCmdProcessor(queue, serviceLocator);
		thread = new Thread(service);
		thread.start();
	}
	
	@After
	public void tearDown() throws Exception {
		control.resetToNice();
		queue.put(cmd = new CmdShutdown());
	}
	
	@Test
	public void testShutdown() throws Exception {
		connMock.close();
		control.replay();
		
		queue.put(cmd = new CmdShutdown());
		
		assertTrue(cmd.getResult().get(1, TimeUnit.SECONDS));
		control.verify();
	}
	
	@Test
	public void testConnect() throws Exception {
		connMock.connect();
		control.replay();
		
		queue.put(cmd = new CmdConnect());

		assertTrue(cmd.getResult().get(1, TimeUnit.SECONDS));
		control.verify();
	}
	
	@Test
	public void testDisconnect() throws Exception {
		connMock.disconnect();
		control.replay();
		
		queue.put(cmd = new CmdDisconnect());

		assertTrue(cmd.getResult().get(1, TimeUnit.SECONDS));
		control.verify();
	}
	
	@Test
	public void testMsgFromServer() throws Exception {
		mrouterMock.dispatchMessage("caboose");
		control.replay();
		
		queue.put(cmd = new CmdMsgFromServer("caboose"));
		
		assertTrue(cmd.getResult().get(1, TimeUnit.SECONDS));
		control.verify();
	}
	
	@Test
	public void testSubscrSymbol() throws Exception {
		sdsMock.subscribe(symbol, MDLevel.L1_BBO);
		control.replay();
		
		queue.put(cmd = new CmdSubscrSymbol(symbol, MDLevel.L1_BBO));
		
		assertTrue(cmd.getResult().get(1, TimeUnit.SECONDS));
		control.verify();
	}

	@Test
	public void testUnsubscrSymbol() throws Exception {
		sdsMock.unsubscribe(symbol, MDLevel.L1);
		control.replay();
		
		queue.put(cmd = new CmdUnsubscrSymbol(symbol, MDLevel.L1));
		
		assertTrue(cmd.getResult().get(1, TimeUnit.SECONDS));
		control.verify();
	}

}

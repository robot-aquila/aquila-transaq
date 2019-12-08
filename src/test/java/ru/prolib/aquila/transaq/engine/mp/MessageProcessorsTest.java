package ru.prolib.aquila.transaq.engine.mp;

import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamReader;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.engine.mp.BoardsProcessor;
import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.impl.TQStateUpdate;
import ru.prolib.aquila.transaq.remote.ISecIDF;
import ru.prolib.aquila.transaq.remote.ISecIDG;
import ru.prolib.aquila.transaq.remote.ISecIDT;
import ru.prolib.aquila.transaq.remote.MessageParser;
import ru.prolib.aquila.transaq.remote.entity.ServerStatus;

@SuppressWarnings("unchecked")
public class MessageProcessorsTest {
	private IMocksControl control;
	private MessageParser parserMock;
	private TQReactor reactorMock;
	private ServiceLocator services;
	private XMLStreamReader readerMock;

	@Before
	public void setUp() throws Exception {
		services = new ServiceLocator();
		control = createStrictControl();
		services.setParser(parserMock = control.createMock(MessageParser.class));
		services.setReactor(reactorMock = control.createMock(TQReactor.class));
		readerMock = control.createMock(XMLStreamReader.class);
	}
	
	@Test
	public void testClientProcessor() throws Exception {
		System.out.println(getClass() + " Not yet implemented");
		Thread.sleep(5000L);
	}

	@Test
	public void testBoardsProcessor() throws Exception {
		TQStateUpdate<String> upMock1, upMock2, upMock3;
		List<TQStateUpdate<String>> updates = new ArrayList<>();
		updates.add(upMock1 = control.createMock(TQStateUpdate.class));
		updates.add(upMock2 = control.createMock(TQStateUpdate.class));
		updates.add(upMock3 = control.createMock(TQStateUpdate.class));
		expect(parserMock.readBoards(readerMock)).andReturn(updates);
		reactorMock.updateBoard(upMock1);
		reactorMock.updateBoard(upMock2);
		reactorMock.updateBoard(upMock3);
		control.replay();
		BoardsProcessor service = new BoardsProcessor(services);
		
		service.processMessage(readerMock);
		
		control.verify();
	}
	
	@Test
	public void testCandleKindsProcessor() throws Exception {
		TQStateUpdate<Integer> upMock1, upMock2, upMock3;
		List<TQStateUpdate<Integer>> updates = new ArrayList<>();
		updates.add(upMock1 = control.createMock(TQStateUpdate.class));
		updates.add(upMock2 = control.createMock(TQStateUpdate.class));
		updates.add(upMock3 = control.createMock(TQStateUpdate.class));
		expect(parserMock.readCandleKinds(readerMock)).andReturn(updates);
		reactorMock.updateCandleKind(upMock1);
		reactorMock.updateCandleKind(upMock2);
		reactorMock.updateCandleKind(upMock3);
		control.replay();
		CandleKindsProcessor service = new CandleKindsProcessor(services);
		
		service.processMessage(readerMock);
		
		control.verify();
	}

	@Test
	public void testMarketsProcessor() throws Exception {
		TQStateUpdate<Integer> upMock1, upMock2, upMock3;
		List<TQStateUpdate<Integer>> updates = new ArrayList<>();
		updates.add(upMock1 = control.createMock(TQStateUpdate.class));
		updates.add(upMock2 = control.createMock(TQStateUpdate.class));
		updates.add(upMock3 = control.createMock(TQStateUpdate.class));
		expect(parserMock.readMarkets(readerMock)).andReturn(updates);
		reactorMock.updateMarket(upMock1);
		reactorMock.updateMarket(upMock2);
		reactorMock.updateMarket(upMock3);
		control.replay();
		MarketsProcessor service = new MarketsProcessor(services);
		
		service.processMessage(readerMock);
		
		control.verify();
	}
	
	@Test
	public void testPitsProcessor() throws Exception {
		List<TQStateUpdate<ISecIDT>> updates = new ArrayList<>();
		TQStateUpdate<ISecIDT> duMock1, duMock2, duMock3;
		updates.add(duMock1 = control.createMock(TQStateUpdate.class));
		updates.add(duMock2 = control.createMock(TQStateUpdate.class));
		updates.add(duMock3 = control.createMock(TQStateUpdate.class));
		expect(parserMock.readPits(readerMock)).andReturn(updates);
		reactorMock.updateSecurityBoard(duMock1);
		reactorMock.updateSecurityBoard(duMock2);
		reactorMock.updateSecurityBoard(duMock3);
		control.replay();
		PitsProcessor service = new PitsProcessor(services);
		
		service.processMessage(readerMock);
		
		control.verify();
	}
	
	@Test
	public void testSecInfoProcessor() throws Exception {
		TQStateUpdate<ISecIDG> suMock = control.createMock(TQStateUpdate.class);
		expect(parserMock.readSecInfo(readerMock)).andReturn(suMock);
		reactorMock.updateSecurity1(suMock);
		control.replay();
		SecInfoProcessor service = new SecInfoProcessor(services);
		
		service.processMessage(readerMock);
		
		control.verify();
	}
	
	@Test
	public void testSecInfoUpdProcessor() throws Exception {
		TQStateUpdate<ISecIDG> suMock = control.createMock(TQStateUpdate.class);
		expect(parserMock.readSecInfoUpd(readerMock)).andReturn(suMock);
		reactorMock.updateSecurity1(suMock);
		control.replay();
		SecInfoUpdProcessor service = new SecInfoUpdProcessor(services);
		
		service.processMessage(readerMock);
		
		control.verify();
	}

	@Test
	public void testSecuritiesProcessor() throws Exception {
		List<TQStateUpdate<ISecIDF>> updates = new ArrayList<>();
		TQStateUpdate<ISecIDF> duMock1, duMock2, duMock3;
		updates.add(duMock1 = control.createMock(TQStateUpdate.class));
		updates.add(duMock2 = control.createMock(TQStateUpdate.class));
		updates.add(duMock3 = control.createMock(TQStateUpdate.class));
		expect(parserMock.readSecurities(readerMock)).andReturn(updates);
		reactorMock.updateSecurityF(duMock1);
		reactorMock.updateSecurityF(duMock2);
		reactorMock.updateSecurityF(duMock3);
		control.replay();
		SecuritiesProcessor service = new SecuritiesProcessor(services);
		
		service.processMessage(readerMock);
		
		control.verify();
	}

	@Test
	public void testServerStatusProcessor() throws Exception {
		ServerStatus update = new ServerStatus(true, true, "Hello, world!");
		expect(parserMock.readServerStatus(readerMock)).andReturn(update);
		reactorMock.updateServerStatus(update);
		control.replay();
		ServerStatusProcessor service = new ServerStatusProcessor(services);
		
		service.processMessage(readerMock);
		
		control.verify();
	}

}

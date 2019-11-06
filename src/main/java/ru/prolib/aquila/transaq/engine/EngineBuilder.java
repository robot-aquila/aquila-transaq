package ru.prolib.aquila.transaq.engine;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.tuple.Pair;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.transaq.engine.mp.BoardsProcessor;
import ru.prolib.aquila.transaq.engine.mp.CandleKindsProcessor;
import ru.prolib.aquila.transaq.engine.mp.ClientProcessor;
import ru.prolib.aquila.transaq.engine.mp.DefaultMessageProcessor;
import ru.prolib.aquila.transaq.engine.mp.MarketsProcessor;
import ru.prolib.aquila.transaq.engine.mp.MessageRouter;

import ru.prolib.aquila.transaq.engine.mp.MessageRouterImpl;
import ru.prolib.aquila.transaq.engine.mp.PitsProcessor;
import ru.prolib.aquila.transaq.engine.mp.ProcessorRegistryBuilder;
import ru.prolib.aquila.transaq.engine.mp.RawMessageDumper;
import ru.prolib.aquila.transaq.engine.mp.SecInfoProcessor;
import ru.prolib.aquila.transaq.engine.mp.SecInfoUpdProcessor;
import ru.prolib.aquila.transaq.engine.mp.SecuritiesMessageProcessor;
import ru.prolib.aquila.transaq.impl.TQDirectory;
import ru.prolib.aquila.transaq.impl.TQFieldAssembler;
import ru.prolib.aquila.transaq.impl.TQParser;
import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.impl.TQSecurityHandlerFactory;
import ru.prolib.aquila.transaq.impl.TQSecurityHandlerRegistry;

public class EngineBuilder {
	
	public Pair<ServiceLocator, Engine> build() {
		ServiceLocator services = new ServiceLocator();
		BlockingQueue<Cmd> cmd_queue = new LinkedBlockingQueue<>();
		Thread t = new Thread(new EngineCmdProcessor(cmd_queue, services));
		t.setDaemon(true);
		t.setName("TRANSAQ-ENGINE");
		t.start();
		return Pair.of(services, new EngineImpl(cmd_queue));
	}

	MessageRouter standardRouter(ServiceLocator services) {
		return new MessageRouterImpl(new ProcessorRegistryBuilder()
				.withDefaultProcessor(new DefaultMessageProcessor(services))
				.withProcessor("securities", new SecuritiesMessageProcessor(services))
				.withProcessor("sec_info", new SecInfoProcessor(services))
				.withProcessor("sec_info_upd", new SecInfoUpdProcessor(services))
				.withProcessor("markets", new MarketsProcessor(services))
				.withProcessor("boards", new BoardsProcessor(services))
				.withProcessor("candlekinds", new CandleKindsProcessor(services))
				.withProcessor("pits", new PitsProcessor(services))
				// TODO: do it
				.withProcessor("client", new ClientProcessor())
				.withProcessor("messages", new RawMessageDumper())
				.withProcessor("positions", new RawMessageDumper())
				.withProcessor("union", new RawMessageDumper())
				.withProcessor("overnight", new RawMessageDumper())
				.withProcessor("server_status", new RawMessageDumper())
				
				.build());
	}

	
	public void initPrimary(ServiceLocator services, EventQueue eventQueue) {
		services.setMessageRouter(standardRouter(services));
		services.setDirectory(new TQDirectory(eventQueue));
		services.setParser(TQParser.getInstance());
	}
	
	public void initSecondary(ServiceLocator services, EditableTerminal terminal) {
		TQReactor reactor = new TQReactor(
				services.getDirectory(),
				new TQSecurityHandlerRegistry(),
				new TQSecurityHandlerFactory(terminal, new TQFieldAssembler(services.getDirectory()))
			);
		services.setReactor(reactor);
	}

}

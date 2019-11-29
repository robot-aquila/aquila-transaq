package ru.prolib.aquila.transaq.engine;

import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrRepository;
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
import ru.prolib.aquila.transaq.engine.sds.SymbolDataServiceImpl;
import ru.prolib.aquila.transaq.engine.sds.SymbolStateFactory;
import ru.prolib.aquila.transaq.engine.sds.SymbolStateRepository;
import ru.prolib.aquila.transaq.impl.TQDirectory;
import ru.prolib.aquila.transaq.impl.TQFieldAssembler;
import ru.prolib.aquila.transaq.impl.TQParser;
import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.impl.TQSecurityHandlerFactory;
import ru.prolib.aquila.transaq.impl.TQSecurityHandlerRegistry;

public class EngineBuilderRoutines {

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
	
	public void initPrimary(ServiceLocator services) {
		services.setMessageRouter(standardRouter(services));
		services.setDirectory(new TQDirectory(services.getEventQueue()));
		services.setParser(TQParser.getInstance());
		services.setAssembler(new TQFieldAssembler(services.getDirectory()));
	}
	
	public void initSecondary(ServiceLocator services, EditableTerminal terminal) {
		TQReactor reactor = new TQReactor(
				services.getDirectory(),
				new TQSecurityHandlerRegistry(),
				new TQSecurityHandlerFactory(terminal, services.getAssembler())
			);
		services.setReactor(reactor);
		services.setTerminal(terminal);
		services.setSymbolDataService(new SymbolDataServiceImpl(
				services,
				new SymbolStateFactory(services),
				new SymbolStateRepository(),
				new SymbolSubscrRepository(services.getEventQueue(), "TRANSAQ-SUBSCR")
			));
	}

}

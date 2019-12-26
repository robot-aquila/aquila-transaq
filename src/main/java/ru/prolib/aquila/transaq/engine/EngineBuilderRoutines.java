package ru.prolib.aquila.transaq.engine;

import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrRepository;
import ru.prolib.aquila.transaq.engine.mp.AlltradesProcessor;
import ru.prolib.aquila.transaq.engine.mp.BoardsProcessor;
import ru.prolib.aquila.transaq.engine.mp.CandleKindsProcessor;
import ru.prolib.aquila.transaq.engine.mp.ClientProcessor;
import ru.prolib.aquila.transaq.engine.mp.DefaultMessageProcessor;
import ru.prolib.aquila.transaq.engine.mp.MarketsProcessor;
import ru.prolib.aquila.transaq.engine.mp.MessageProcessor;
import ru.prolib.aquila.transaq.engine.mp.MessageRouter;

import ru.prolib.aquila.transaq.engine.mp.MessageRouterImpl;
import ru.prolib.aquila.transaq.engine.mp.PitsProcessor;
import ru.prolib.aquila.transaq.engine.mp.ProcessorRegistryBuilder;
import ru.prolib.aquila.transaq.engine.mp.QuotationsProcessor;
import ru.prolib.aquila.transaq.engine.mp.MessageProcessorStub;
import ru.prolib.aquila.transaq.engine.mp.SecInfoProcessor;
import ru.prolib.aquila.transaq.engine.mp.SecInfoUpdProcessor;
import ru.prolib.aquila.transaq.engine.mp.SecuritiesProcessor;
import ru.prolib.aquila.transaq.engine.mp.ServerStatusProcessor;
import ru.prolib.aquila.transaq.engine.sds.SymbolDataServiceImpl;
import ru.prolib.aquila.transaq.engine.sds.StateOfDataFeedsFactory;
import ru.prolib.aquila.transaq.impl.TQDirectory;
import ru.prolib.aquila.transaq.impl.TQFieldAssembler;
import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.remote.MessageParser;

public class EngineBuilderRoutines {

	MessageRouter standardRouter(ServiceLocator services) {
		MessageProcessor default_processor = new DefaultMessageProcessor(services);
		return new MessageRouterImpl(new ProcessorRegistryBuilder()
				.withDefaultProcessor(default_processor)
				.withProcessor(DefaultMessageProcessor.DUMP_PROC_ID, default_processor)
				.withProcessor("server_status", new ServerStatusProcessor(services))
				.withProcessor("securities", new SecuritiesProcessor(services))
				.withProcessor("sec_info", new SecInfoProcessor(services))
				.withProcessor("sec_info_upd", new SecInfoUpdProcessor(services))
				.withProcessor("markets", new MarketsProcessor(services))
				.withProcessor("boards", new BoardsProcessor(services))
				.withProcessor("candlekinds", new CandleKindsProcessor(services))
				.withProcessor("pits", new PitsProcessor(services))
				.withProcessor("quotations", new QuotationsProcessor(services))
				.withProcessor("alltrades", new AlltradesProcessor(services))
				// TODO: do it
				.withProcessor("quotes", new MessageProcessorStub())
				.withProcessor("client", new ClientProcessor())
				.withProcessor("messages", new MessageProcessorStub())
				.withProcessor("positions", new MessageProcessorStub())
				.withProcessor("union", new MessageProcessorStub())
				.withProcessor("overnight", new MessageProcessorStub())
				
				.build());
	}
	
	public void initPrimary(ServiceLocator services) {
		services.setMessageRouter(standardRouter(services));
		services.setDirectory(new TQDirectory(services.getEventQueue()));
		services.setParser(MessageParser.getInstance());
		services.setAssembler(new TQFieldAssembler());
		services.setReactor(new TQReactor(services));
		services.setSymbolDataService(new SymbolDataServiceImpl(
				services,
				new StateOfDataFeedsFactory(),
				new SymbolSubscrRepository(services.getEventQueue(), "TRANSAQ-SUBSCR")
			));
	}
	
	public void initSecondary(ServiceLocator services, EditableTerminal terminal) {
		services.setTerminal(terminal);
	}

}

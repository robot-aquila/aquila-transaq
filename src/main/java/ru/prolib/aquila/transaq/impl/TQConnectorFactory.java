package ru.prolib.aquila.transaq.impl;

import org.ini4j.Profile.Section;

import ru.prolib.JTransaq.JTransaqServer;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.transaq.impl.mp.BoardsProcessor;
import ru.prolib.aquila.transaq.impl.mp.CandleKindsProcessor;
import ru.prolib.aquila.transaq.impl.mp.DefaultMessageProcessor;
import ru.prolib.aquila.transaq.impl.mp.MarketsProcessor;
import ru.prolib.aquila.transaq.impl.mp.SecInfoProcessor;
import ru.prolib.aquila.transaq.impl.mp.SecInfoUpdProcessor;
import ru.prolib.aquila.transaq.impl.mp.SecuritiesMessageProcessor;

public class TQConnectorFactory {
	private final Section config;
	
	public TQConnectorFactory(Section config) {
		this.config = config;
	}
	
	public TQConnector createInstance(EditableTerminal terminal) throws Exception {
		TQParser parser = TQParser.getInstance();
		TQDirectory directory = new TQDirectory();
		TQFieldAssembler assembler = new TQFieldAssembler(directory);
		TQReactor reactor = new TQReactor(
				directory,
				new TQSecurityHandlerRegistry(),
				new TQSecurityHandlerFactory(terminal, assembler)
			);
		TQMessageRouter router = new TQMessageRouter(new TQMessageProcessorRegistryBuilder()
				.withDefaultProcessor(new DefaultMessageProcessor(parser))
				.withProcessor("securities", new SecuritiesMessageProcessor(reactor, parser))
				.withProcessor("sec_info", new SecInfoProcessor(reactor, parser))
				.withProcessor("sec_info_upd", new SecInfoUpdProcessor(reactor, parser))
				.withProcessor("markets", new MarketsProcessor(reactor, parser))
				.withProcessor("boards", new BoardsProcessor(reactor, parser))
				.withProcessor("candlekinds", new CandleKindsProcessor(reactor, parser))
				.build());
		TQHandler handler = new TQHandler(router);
		JTransaqServer server = new JTransaqServer(handler);
		TQConnector conn = new TQConnector(config, server, handler);
		return conn;
	}

}

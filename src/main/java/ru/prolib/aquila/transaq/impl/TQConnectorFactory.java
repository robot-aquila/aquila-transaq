package ru.prolib.aquila.transaq.impl;

import org.ini4j.Profile.Section;

import ru.prolib.JTransaq.JTransaqServer;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.transaq.impl.mp.BoardsProcessor;
import ru.prolib.aquila.transaq.impl.mp.CandleKindsProcessor;
import ru.prolib.aquila.transaq.impl.mp.ClientProcessor;
import ru.prolib.aquila.transaq.impl.mp.DefaultMessageProcessor;
import ru.prolib.aquila.transaq.impl.mp.MarketsProcessor;
import ru.prolib.aquila.transaq.impl.mp.RawMessageDumper;
import ru.prolib.aquila.transaq.impl.mp.SecInfoProcessor;
import ru.prolib.aquila.transaq.impl.mp.SecInfoUpdProcessor;
import ru.prolib.aquila.transaq.impl.mp.SecuritiesMessageProcessor;

public class TQConnectorFactory {
	private final Section config;
	private final TQDirectory directory;
	
	public TQConnectorFactory(Section config, TQDirectory directory) {
		this.config = config;
		this.directory = directory;
	}
	
	public TQDirectory getDirectory() {
		return directory;
	}
	
	public TQConnector createInstance(EditableTerminal terminal) throws Exception {
		TQParser parser = TQParser.getInstance();
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
				// TODO: do it
				.withProcessor("client", new ClientProcessor())
				.withProcessor("pits", new RawMessageDumper())
				.withProcessor("messages", new RawMessageDumper())
				.withProcessor("positions", new RawMessageDumper())
				.withProcessor("union", new RawMessageDumper())
				.withProcessor("overnight", new RawMessageDumper())
				.withProcessor("server_status", new RawMessageDumper())
				
				.build());
		TQHandler handler = new TQHandler(router);
		JTransaqServer server = new JTransaqServer(handler);
		TQConnector conn = new TQConnector(config, server, handler);
		return conn;
	}

}

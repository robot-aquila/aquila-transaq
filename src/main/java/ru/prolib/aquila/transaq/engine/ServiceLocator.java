package ru.prolib.aquila.transaq.engine;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.transaq.engine.mp.MessageRouter;
import ru.prolib.aquila.transaq.impl.TQDirectory;
import ru.prolib.aquila.transaq.impl.TQFieldAssembler;
import ru.prolib.aquila.transaq.impl.TQReactor;
import ru.prolib.aquila.transaq.remote.Connector;
import ru.prolib.aquila.transaq.remote.MessageParser;

public class ServiceLocator {
	private EventQueue queue;
	private MessageParser parser;
	private TQReactor reactor;
	private TQDirectory directory;
	private MessageRouter msgRouter;
	private Connector connector;
	private TQFieldAssembler assembler;
	private SymbolDataService symbolDataService;
	private EditableTerminal terminal;
	
	public synchronized MessageParser getParser() {
		if ( parser == null ) {
			throw new IllegalStateException("Parser was not defined");
		}
		return parser;
	}
	
	public synchronized void setParser(MessageParser parser) {
		this.parser = parser;
	}
	
	public synchronized TQReactor getReactor() {
		if ( reactor == null ) {
			throw new IllegalStateException("Reactor was not defined");
		}
		return reactor;
	}

	public synchronized void setReactor(TQReactor reactor) {
		this.reactor = reactor;
	}
	
	public synchronized TQDirectory getDirectory() {
		if ( directory == null ) {
			throw new IllegalStateException("Directory was not defined");
		}
		return directory;
	}
	
	public synchronized void setDirectory(TQDirectory directory) {
		this.directory = directory;
	}
	
	public synchronized MessageRouter getMessageRouter() {
		if ( msgRouter == null ) {
			throw new IllegalStateException("Message router was not defined");
		}
		return msgRouter;
	}
	
	public synchronized void setMessageRouter(MessageRouter msg_router) {
		this.msgRouter = msg_router;
	}
	
	public synchronized Connector getConnector() {
		if ( connector == null ) {
			throw new IllegalStateException("Connector was not defined");
		}
		return connector;
	}
	
	public synchronized void setConnector(Connector connector) {
		this.connector = connector;
	}
	
	public synchronized SymbolDataService getSymbolDataService() {
		if ( symbolDataService == null ) {
			throw new IllegalStateException("Symbol data service was not defined");
		}
		return symbolDataService;
	}
	
	public synchronized void setSymbolDataService(SymbolDataService service) {
		this.symbolDataService = service;
	}
	
	public synchronized TQFieldAssembler getAssembler() {
		if ( assembler == null ) {
			throw new IllegalStateException("Field assembler was not defined");
		}
		return assembler;
	}
	
	public synchronized void setAssembler(TQFieldAssembler assembler) {
		this.assembler = assembler;
	}
	
	public synchronized EditableTerminal getTerminal() {
		if ( terminal == null ) {
			throw new IllegalStateException("Terminal was not defined");
		}
		return terminal;
	}
	
	public synchronized void setTerminal(EditableTerminal terminal) {
		this.terminal = terminal;
	}
	
	public synchronized EventQueue getEventQueue() {
		if ( queue == null ) {
			throw new IllegalStateException("Event queue was not defined");
		}
		return queue;
	}
	
	public synchronized void setEventQueue(EventQueue service) {
		this.queue = service;
	}

}

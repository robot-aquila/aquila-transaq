package ru.prolib.aquila.transaq.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandlerStub;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.transaq.engine.Engine;
import ru.prolib.aquila.transaq.engine.EngineBuilderRoutines;
import ru.prolib.aquila.transaq.engine.ServiceLocator;

public class TQDataProviderImpl implements TQDataProvider {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(TQDataProviderImpl.class);
	}
	
	private final Engine engine;
	private final EngineBuilderRoutines engineBuilder;
	private final ServiceLocator engineServices;
	
	public TQDataProviderImpl(
			Engine engine,
			EngineBuilderRoutines engine_builder,
			ServiceLocator engine_services)
	{
		this.engine = engine;
		this.engineBuilder = engine_builder;
		this.engineServices = engine_services;
	}
	
	@Override
	public TQDirectory getDirectory() {
		// Do not return locator itself because most of services aren't safe to
		// use among threads and do not intended to use outside of engine dispatching queue
		return engineServices.getDirectory();
	}

	@Override
	public long getNextOrderID() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void registerNewOrder(EditableOrder order) throws OrderException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void cancelOrder(EditableOrder order) throws OrderException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void subscribeRemoteObjects(EditableTerminal terminal) {
		engineBuilder.initSecondary(engineServices, terminal);
		engine.connect();
	}

	@Override
	public void unsubscribeRemoteObjects(EditableTerminal terminal) {
		engine.disconnect();
	}

	@Override
	public SubscrHandler subscribe(Symbol symbol, MDLevel level, EditableTerminal terminal) {
		engine.subscribeSymbol(symbol, level);
		return new TQSymbolSubscrHandler(engine, symbol, level);
	}

	@Override
	public SubscrHandler subscribe(Account account, EditableTerminal terminal) {
		return new SubscrHandlerStub();
	}
	
	@Override
	public void close() {
		CompletableFuture<Boolean> closed = engine.shutdown();
		try {
			closed.get(5, TimeUnit.SECONDS);
		} catch ( Exception e ) {
			logger.error("Unexpected exception: ", e);
		}
	}

}

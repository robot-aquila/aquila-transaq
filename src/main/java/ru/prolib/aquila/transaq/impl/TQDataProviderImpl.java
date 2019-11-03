package ru.prolib.aquila.transaq.impl;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.DataProvider;
import ru.prolib.aquila.transaq.engine.Engine;
import ru.prolib.aquila.transaq.engine.EngineBuilder;
import ru.prolib.aquila.transaq.engine.ServiceLocator;

public class TQDataProviderImpl implements DataProvider {
	private final TQConnector connector;
	private final Engine engine;
	private final EngineBuilder engineBuilder;
	private final ServiceLocator engineServices;
	
	public TQDataProviderImpl(
			TQConnector connector,
			Engine engine,
			EngineBuilder engine_builder,
			ServiceLocator engine_services)
	{
		this.connector = connector;
		this.engine = engine;
		this.engineBuilder = engine_builder;
		this.engineServices = engine_services;
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
		try {
			engineBuilder.initSecondary(engineServices, terminal);
			connector.connect();
		} catch ( Exception e ) {
			throw new RuntimeException("Establishing connection failed: ", e);
		}
	}

	@Override
	public void unsubscribeRemoteObjects(EditableTerminal terminal) {
		connector.disconnect();
	}

	@Override
	public void subscribe(Symbol symbol, EditableTerminal terminal) {
		
	}

	@Override
	public void unsubscribe(Symbol symbol, EditableTerminal terminal) {
		
	}

	@Override
	public void subscribe(Account account, EditableTerminal terminal) {
		
	}

	@Override
	public void unsubscribe(Account account, EditableTerminal terminal) {
		
	}
	
	@Override
	public void close() {
		connector.close();
		engine.shutdown();
	}

}

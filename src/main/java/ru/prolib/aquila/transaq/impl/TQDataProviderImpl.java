package ru.prolib.aquila.transaq.impl;

import org.ini4j.Profile.Section;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.DataProvider;

public class TQDataProviderImpl implements DataProvider {
	private final TQConnectorFactory factory;
	private TQConnector connector;
	
	public TQDataProviderImpl(TQConnectorFactory factory) {
		this.factory = factory;
	}
	
	public TQDataProviderImpl(Section config) {
		this(new TQConnectorFactory(config));
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
			connector = factory.createInstance(terminal);
			connector.init();
			connector.connect();
		} catch ( Exception e ) {
			throw new RuntimeException("Establishing connection failed: ", e);
		}
	}

	@Override
	public void unsubscribeRemoteObjects(EditableTerminal terminal) {
		connector.disconnect();
		connector.close();
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

}

package ru.prolib.aquila.transaq.impl;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.L1UpdatableStreamContainer;
import ru.prolib.aquila.core.BusinessEntities.MDUpdatableStreamContainer;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.DataProvider;

public class TQDataProviderImpl implements DataProvider {

	@Override
	public void subscribeStateUpdates(EditableSecurity security) {
		
	}

	@Override
	public void subscribeLevel1Data(Symbol symbol, L1UpdatableStreamContainer container) {
		
	}

	@Override
	public void subscribeLevel2Data(Symbol symbol, MDUpdatableStreamContainer container) {
		
	}

	@Override
	public void subscribeStateUpdates(EditablePortfolio portfolio) {
		throw new UnsupportedOperationException();
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unsubscribeRemoteObjects(EditableTerminal terminal) {
		// TODO Auto-generated method stub
		
	}

}

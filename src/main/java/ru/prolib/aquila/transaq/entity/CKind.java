package ru.prolib.aquila.transaq.entity;

import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainerImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;
import ru.prolib.aquila.transaq.impl.TQCKindField;

public class CKind extends ObservableStateContainerImpl {
	
	public CKind(OSCParams params) {
		super(params);
	}
	
	public int getID() {
		return this.getInteger(TQCKindField.CKIND_ID);
	}
	
	public int getPeriod() {
		return this.getInteger(TQCKindField.CKIND_PERIOD);
	}
	
	public String getName() {
		return this.getString(TQCKindField.CKIND_NAME);
	}

}

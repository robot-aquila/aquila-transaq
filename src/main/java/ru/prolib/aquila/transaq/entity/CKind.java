package ru.prolib.aquila.transaq.entity;

import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainerImpl;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCParams;
import ru.prolib.aquila.transaq.remote.MessageFields.FCKind;

public class CKind extends ObservableStateContainerImpl {
	
	public CKind(OSCParams params) {
		super(params);
	}
	
	public int getID() {
		return this.getInteger(FCKind.CKIND_ID);
	}
	
	public int getPeriod() {
		return this.getInteger(FCKind.CKIND_PERIOD);
	}
	
	public String getName() {
		return this.getString(FCKind.CKIND_NAME);
	}

}

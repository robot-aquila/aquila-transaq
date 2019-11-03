package ru.prolib.aquila.transaq.impl;

import ru.prolib.JTransaq.JTransaqHandler;
import ru.prolib.aquila.transaq.engine.Engine;

public class TransaqHandler extends JTransaqHandler {
	private final Engine engine;
	
	public TransaqHandler(Engine engine) {
		super();
		this.engine = engine;
	}

	@Override
	public boolean Handle(String msg) {
		engine.messageFromServer(msg);
		return true;
	}
	
}

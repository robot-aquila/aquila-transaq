package ru.prolib.aquila.transaq.impl;

import ru.prolib.JTransaq.JTransaqHandler;
import ru.prolib.aquila.transaq.engine.Engine;
import ru.prolib.aquila.transaq.remote.MessageInterceptor;
import ru.prolib.aquila.transaq.remote.MessageInterceptorStub;

public class TransaqHandler extends JTransaqHandler {
	private final Engine engine;
	private final MessageInterceptor interceptor;
	
	public TransaqHandler(Engine engine, MessageInterceptor interceptor) {
		super();
		this.engine = engine;
		this.interceptor = interceptor;
	}
	
	public TransaqHandler(Engine engine) {
		this(engine, new MessageInterceptorStub());
	}

	@Override
	public boolean Handle(String msg) {
		interceptor.incoming(msg);
		engine.messageFromServer(msg);
		return true;
	}
	
}

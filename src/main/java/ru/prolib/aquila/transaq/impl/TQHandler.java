package ru.prolib.aquila.transaq.impl;

import ru.prolib.JTransaq.JTransaqHandler;

public class TQHandler extends JTransaqHandler {
	private final TQMessageRouter router;
	
	public TQHandler(TQMessageRouter router) {
		super();
		this.router = router;
	}

	@Override
	public boolean Handle(String msg) {
		router.dispatchMessage(msg);
		return true;
	}
	
}

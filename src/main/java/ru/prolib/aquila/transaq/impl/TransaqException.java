package ru.prolib.aquila.transaq.impl;

public class TransaqException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public TransaqException() {
		
	}
	
	public TransaqException(String msg) {
		super(msg);
	}
	
	public TransaqException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public TransaqException(Throwable t) {
		super(t);
	}

}

package ru.prolib.aquila.transaq.impl;

public class TQConnectorException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public TQConnectorException() {
		
	}
	
	public TQConnectorException(String msg) {
		super(msg);
	}
	
	public TQConnectorException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public TQConnectorException(Throwable t) {
		super(t);
	}

}

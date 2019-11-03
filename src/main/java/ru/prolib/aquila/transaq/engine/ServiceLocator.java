package ru.prolib.aquila.transaq.engine;

import ru.prolib.aquila.transaq.impl.TQDirectory;
import ru.prolib.aquila.transaq.impl.TQParser;
import ru.prolib.aquila.transaq.impl.TQReactor;

public class ServiceLocator {
	private TQParser parser;
	private TQReactor reactor;
	private TQDirectory directory;
	
	public synchronized TQParser getParser() {
		if ( parser == null ) {
			throw new IllegalStateException("Parser was not defined");
		}
		return parser;
	}
	
	public synchronized void setParser(TQParser parser) {
		this.parser = parser;
	}
	
	public synchronized TQReactor getReactor() {
		if ( reactor == null ) {
			throw new IllegalStateException("Reactor was not defined");
		}
		return reactor;
	}

	public synchronized void setReactor(TQReactor reactor) {
		this.reactor = reactor;
	}
	
	public synchronized TQDirectory getDirectory() {
		if ( directory == null ) {
			throw new IllegalStateException("Directory was not defined");
		}
		return directory;
	}
	
	public synchronized void setDirectory(TQDirectory directory) {
		this.directory = directory;
	}

}

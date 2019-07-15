package ru.prolib.aquila.transaq.impl;

import java.util.HashMap;
import java.util.Map;

public class TQMessageProcessorRegistryBuilder {
	private final Map<String, TQMessageProcessor> map;
	private TQMessageProcessor default_proc;
	
	public TQMessageProcessorRegistryBuilder() {
		map = new HashMap<>();
	}
	
	public TQMessageProcessorRegistryBuilder withProcessor(String name, TQMessageProcessor processor) {
		map.put(name, processor);
		return this;
	}
	
	public TQMessageProcessorRegistryBuilder withDefaultProcessor(TQMessageProcessor processor) {
		default_proc = processor;
		return this;
	}
	
	public TQMessageProcessorRegistry build() {
		if ( default_proc == null ) {
			throw new IllegalStateException("Default processor must be defined");
		}
		return new TQMessageProcessorRegistryImpl(default_proc, map);
	}

}

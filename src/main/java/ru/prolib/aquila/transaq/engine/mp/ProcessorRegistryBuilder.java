package ru.prolib.aquila.transaq.engine.mp;

import java.util.HashMap;
import java.util.Map;

public class ProcessorRegistryBuilder {
	private final Map<String, MessageProcessor> map;
	private MessageProcessor default_proc;
	
	public ProcessorRegistryBuilder() {
		map = new HashMap<>();
	}
	
	public ProcessorRegistryBuilder withProcessor(String name, MessageProcessor processor) {
		map.put(name, processor);
		return this;
	}
	
	public ProcessorRegistryBuilder withDefaultProcessor(MessageProcessor processor) {
		default_proc = processor;
		return this;
	}
	
	public ProcessorRegistry build() {
		if ( default_proc == null ) {
			throw new IllegalStateException("Default processor must be defined");
		}
		return new ProcessorRegistryImpl(default_proc, map);
	}

}

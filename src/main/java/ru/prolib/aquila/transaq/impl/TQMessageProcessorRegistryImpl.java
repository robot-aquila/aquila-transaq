package ru.prolib.aquila.transaq.impl;

import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class TQMessageProcessorRegistryImpl implements TQMessageProcessorRegistry {
	private final TQMessageProcessor defaultProcessor;
	private final Map<String, TQMessageProcessor> processorMap;
	
	public TQMessageProcessorRegistryImpl(TQMessageProcessor default_processor,
			Map<String, TQMessageProcessor> processor_map)
	{
		this.defaultProcessor = default_processor;
		this.processorMap = processor_map;
	}
	
	public TQMessageProcessor getDefaultProcessor() {
		return defaultProcessor;
	}
	
	public Map<String, TQMessageProcessor> getProcessorMap() {
		return processorMap;
	}

	@Override
	public TQMessageProcessor get(String processor_id) {
		TQMessageProcessor p = processorMap.get(processor_id);
		return p == null ? defaultProcessor : p;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TQMessageProcessorRegistryImpl.class ) {
			return false;
		}
		TQMessageProcessorRegistryImpl o = (TQMessageProcessorRegistryImpl) other;
		return new EqualsBuilder()
				.append(o.defaultProcessor, defaultProcessor)
				.append(o.processorMap, processorMap)
				.build();
	}

}

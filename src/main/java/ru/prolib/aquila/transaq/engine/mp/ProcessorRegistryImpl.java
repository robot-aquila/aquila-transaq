package ru.prolib.aquila.transaq.engine.mp;

import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class ProcessorRegistryImpl implements ProcessorRegistry {
	private final MessageProcessor defaultProcessor;
	private final Map<String, MessageProcessor> processorMap;
	
	public ProcessorRegistryImpl(MessageProcessor default_processor,
			Map<String, MessageProcessor> processor_map)
	{
		this.defaultProcessor = default_processor;
		this.processorMap = processor_map;
	}
	
	public MessageProcessor getDefaultProcessor() {
		return defaultProcessor;
	}
	
	public Map<String, MessageProcessor> getProcessorMap() {
		return processorMap;
	}

	@Override
	public MessageProcessor get(String processor_id) {
		MessageProcessor p = processorMap.get(processor_id);
		return p == null ? defaultProcessor : p;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ProcessorRegistryImpl.class ) {
			return false;
		}
		ProcessorRegistryImpl o = (ProcessorRegistryImpl) other;
		return new EqualsBuilder()
				.append(o.defaultProcessor, defaultProcessor)
				.append(o.processorMap, processorMap)
				.build();
	}

}

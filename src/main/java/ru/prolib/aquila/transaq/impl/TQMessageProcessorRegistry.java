package ru.prolib.aquila.transaq.impl;

public interface TQMessageProcessorRegistry {
	TQMessageProcessor get(String processor_id);
}

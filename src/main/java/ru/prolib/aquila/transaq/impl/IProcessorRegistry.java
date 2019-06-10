package ru.prolib.aquila.transaq.impl;

public interface IProcessorRegistry {
	IMessageProcessor get(String processor_id);
}

package ru.prolib.aquila.transaq.engine.mp;

public interface ProcessorRegistry {
	MessageProcessor get(String processor_id);
}

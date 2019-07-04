package ru.prolib.aquila.transaq.impl;

public interface TQSecurityHandlerFactory {
	TQSecurityHandler createHandler(TQSecID3 sec_id);
}

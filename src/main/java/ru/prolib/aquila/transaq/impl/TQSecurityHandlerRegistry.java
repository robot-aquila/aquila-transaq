package ru.prolib.aquila.transaq.impl;

public interface TQSecurityHandlerRegistry {
	TQSecurityHandler getHandlerOrNull(TQSecID_F sec_id);
	TQSecurityHandler getHandler(TQSecID1 sec_id);
	void registerHandler(TQSecurityHandler handler);
}

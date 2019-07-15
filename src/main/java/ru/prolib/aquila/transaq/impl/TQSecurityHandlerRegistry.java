package ru.prolib.aquila.transaq.impl;

import java.util.HashMap;
import java.util.Map;

public class TQSecurityHandlerRegistry {
	private final Map<TQSecID1, TQSecurityHandler> mapSecID1;
	private final Map<TQSecID_F, TQSecurityHandler> mapSecID3;
	
	TQSecurityHandlerRegistry(Map<TQSecID1, TQSecurityHandler> map_sec_id1,
			Map<TQSecID_F, TQSecurityHandler> map_sec_id3)
	{
		this.mapSecID1 = map_sec_id1;
		this.mapSecID3 = map_sec_id3;
	}
	
	public TQSecurityHandlerRegistry() {
		this(new HashMap<>(), new HashMap<>());
	}

	public synchronized TQSecurityHandler getHandlerOrNull(TQSecID_F sec_id) {
		return mapSecID3.get(sec_id);
	}

	public synchronized TQSecurityHandler getHandler(TQSecID1 sec_id) {
		TQSecurityHandler x = mapSecID1.get(sec_id);
		if ( x != null ) {
			return x; 
		}
		throw new IllegalArgumentException("Handler not exists: " + sec_id);
	}

	public synchronized void registerHandler(TQSecurityHandler handler) {
		TQSecID_F sec_id3 = handler.getSecID3();
		if ( mapSecID3.containsKey(sec_id3) ) {
			throw new IllegalStateException("Handler already exists: " + sec_id3);
		}
		mapSecID3.put(sec_id3, handler);
		mapSecID1.put(new TQSecID1(sec_id3), handler);
	}

}

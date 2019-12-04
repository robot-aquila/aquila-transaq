package ru.prolib.aquila.transaq.impl;

import java.util.HashMap;
import java.util.Map;

import ru.prolib.aquila.transaq.remote.TQSecIDG;
import ru.prolib.aquila.transaq.remote.ISecIDF;
import ru.prolib.aquila.transaq.remote.ISecIDG;

@Deprecated
public class TQSecurityHandlerRegistry {
	private final Map<ISecIDG, TQSecurityHandler> mapSecID1;
	private final Map<ISecIDF, TQSecurityHandler> mapSecID3;
	
	TQSecurityHandlerRegistry(Map<ISecIDG, TQSecurityHandler> map_sec_id1,
			Map<ISecIDF, TQSecurityHandler> map_sec_id3)
	{
		this.mapSecID1 = map_sec_id1;
		this.mapSecID3 = map_sec_id3;
	}
	
	public TQSecurityHandlerRegistry() {
		this(new HashMap<>(), new HashMap<>());
	}

	public synchronized TQSecurityHandler getHandlerOrNull(ISecIDF sec_id) {
		return mapSecID3.get(sec_id);
	}

	public synchronized TQSecurityHandler getHandler(ISecIDG sec_id) {
		TQSecurityHandler x = mapSecID1.get(sec_id);
		if ( x != null ) {
			return x; 
		}
		throw new IllegalArgumentException("Handler not exists: " + sec_id);
	}

	public synchronized void registerHandler(TQSecurityHandler handler) {
		ISecIDF sec_id3 = handler.getSecID3();
		if ( mapSecID3.containsKey(sec_id3) ) {
			throw new IllegalStateException("Handler already exists: " + sec_id3);
		}
		mapSecID3.put(sec_id3, handler);
		mapSecID1.put(new TQSecIDG(sec_id3), handler);
	}

}

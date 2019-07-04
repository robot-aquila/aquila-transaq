package ru.prolib.aquila.transaq.impl;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateConsumer;

public interface TQSecurityHandler {
	void initialUpdate(DeltaUpdate update);
	void update(DeltaUpdate update);
	void setConsumer(DeltaUpdateConsumer consumer);
	TQSecID3 getSecID3();
}

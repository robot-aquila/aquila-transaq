package ru.prolib.aquila.transaq.entity;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCControllerStub;
import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.transaq.remote.MessageFields.FCKind;

public class CandleKindTest {
	private IMocksControl control;
	private OSCRepository<Integer, CKind> repoMock;
	private EventQueue queueMock;
	private CKind service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		repoMock = control.createMock(OSCRepository.class);
		queueMock = control.createMock(EventQueue.class);
		service = new CKindFactory(queueMock).produce(repoMock, 25);
	}
	
	@Test
	public void testCtor3() {
		service.consume(new DeltaUpdateBuilder()
				.withToken(FCKind.CKIND_ID, 25)
				.withToken(FCKind.CKIND_PERIOD, 60)
				.withToken(FCKind.CKIND_NAME, "60 minutes")
				.buildUpdate()
			);
		
		assertEquals(25, service.getID());
		assertEquals(60, service.getPeriod());
		assertEquals("60 minutes", service.getName());
		
		assertEquals("CKind#25", service.getContainerID());
		assertEquals(OSCControllerStub.class, service.getController().getClass());
	}

}

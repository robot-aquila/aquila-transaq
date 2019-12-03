package ru.prolib.aquila.transaq;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.ini4j.Profile.Section;

import ru.prolib.JTransaq.JTransaqHandler;
import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.aquila.core.data.DataProvider;
import ru.prolib.aquila.transaq.engine.Cmd;
import ru.prolib.aquila.transaq.engine.Engine;
import ru.prolib.aquila.transaq.engine.EngineBuilderRoutines;
import ru.prolib.aquila.transaq.engine.EngineCmdProcessor;
import ru.prolib.aquila.transaq.engine.EngineImpl;
import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.impl.TQDataProviderImpl;
import ru.prolib.aquila.transaq.impl.TransaqException;
import ru.prolib.aquila.transaq.impl.TransaqHandler;
import ru.prolib.aquila.transaq.remote.ConnectorFactory;
import ru.prolib.aquila.transaq.remote.StdConnectorFactory;

public class TransaqBuilder {
	private static final String DEFAULT_SERVICE_ID = "TRANSAQ";
	private String serviceID;
	private EventQueue queue;
	private ConnectorFactory connectorFactory;

	public TransaqBuilder withServiceID(String service_id) {
		this.serviceID = service_id;
		return this;
	}

	public TransaqBuilder withEventQueue(EventQueue queue) {
		this.queue = queue;
		return this;
	}

	public TransaqBuilder withConnectorFactory(ConnectorFactory factory) {
		this.connectorFactory = factory;
		return this;
	}
	
	public TransaqBuilder withConnectorFactoryStd(Section config) {
		return withConnectorFactory(new StdConnectorFactory(config));
	}

	public DataProvider build() throws TransaqException {
		if ( connectorFactory == null ) {
			throw new IllegalStateException("Connector factory was not defined");
		}

		String _service_id = serviceID == null ? DEFAULT_SERVICE_ID : serviceID;
		ServiceLocator services = new ServiceLocator();
		services.setEventQueue(queue == null ? new EventQueueImpl(_service_id) : queue);
		
		BlockingQueue<Cmd> cmd_queue = new LinkedBlockingQueue<>();
		Engine engine = new EngineImpl(cmd_queue);
		Thread t = new Thread(new EngineCmdProcessor(cmd_queue, services));
		t.setDaemon(true);
		t.setName(_service_id);
		t.start();
		JTransaqHandler _handler = new TransaqHandler(engine);

		services.setConnector(connectorFactory.produce(_handler));
		new EngineBuilderRoutines().initPrimary(services);
		return new TQDataProviderImpl(engine, new EngineBuilderRoutines(), services);
	}

}

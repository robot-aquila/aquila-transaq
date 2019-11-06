package ru.prolib.aquila.transaq.engine;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.transaq.impl.TQConnectorException;

public class EngineCmdProcessor implements Runnable {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(EngineCmdProcessor.class);
	}
	
	private final BlockingQueue<Cmd> cmdQueue;
	private final ServiceLocator services;
	
	public EngineCmdProcessor(BlockingQueue<Cmd> cmd_queue, ServiceLocator services) {
		this.cmdQueue = cmd_queue;
		this.services = services;
	}

	@Override
	public void run() {
		try {
			Cmd cmd = null;
			boolean shutdown = false;
			while ( shutdown == false && (cmd = cmdQueue.take()) != null ) {
				switch ( cmd.getType() ) {
				case SHUTDOWN:
					services.getConnector().close();
					shutdown = true;
					break;
				case CONNECT:
					try {
						// TODO: This should start some controller to track connection state.
						services.getConnector().connect();
					} catch ( TQConnectorException e ) {
						logger.error("Connect failed: ", e);
					}
					break;
				case DISCONNECT:
					services.getConnector().disconnect();
					break;
				case MSG_FROM_SERVER:
					services.getMessageRouter().dispatchMessage(((CmdMsgFromServer)cmd).getMessage());
					break;
				case SUBSCR_SYMBOL:
					// TODO: 
					break;
				case UNSUBSCR_SYMBOL:
					// TODO: 
					break;
				default:
					throw new IllegalArgumentException("Unidentified command: " + cmd);	
				}
				cmd.getResult().complete(true);
			}
		} catch ( InterruptedException e ) {
			logger.error("Unexpected interruption: ", e);
			Thread.currentThread().interrupt();
		}
	}

}

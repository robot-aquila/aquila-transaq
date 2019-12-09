package ru.prolib.aquila.transaq.engine;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.transaq.impl.TransaqException;

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
				try {
					shutdown = processCmd(cmd);
					cmd.getResult().complete(true);
				} catch ( Exception e ) {
					logger.error("Error processing message: {}", cmd, e);
					cmd.getResult().completeExceptionally(e);
				}
			}
		} catch ( InterruptedException e ) {
			logger.error("Unexpected interruption: ", e);
			Thread.currentThread().interrupt();
		}
	}
	
	private boolean processCmd(Cmd cmd) {
		switch ( cmd.getType() ) {
		case SHUTDOWN:
		{
			switch ( ((CmdShutdown) cmd).getPhase() ) {
			case 1:
				logger.debug("Shutdown phase 1");
				return true;
			case 0:
			default:
				logger.debug("Shutdown phase 0");
				services.getConnector().close();
				try {
					cmdQueue.put(new CmdShutdown(1));
					return false;
				} catch ( Exception e ) {
					logger.error("Unexpected exception cause immediate shutdown (no additional phase): ", e);
					return true;
				}
			}
		}
		case CONNECT:
		{
			try {
				// TODO: This should start some controller to track connection state.
				services.getConnector().connect();
			} catch ( TransaqException e ) {
				logger.error("Connect failed: ", e);
			}
			break;
		}
		case DISCONNECT:
		{
			services.getConnector().disconnect();
			break;
		}
		case MSG_FROM_SERVER:
		{
			services.getMessageRouter().dispatchMessage(((CmdMsgFromServer)cmd).getMessage());
			break;
		}
		case SUBSCR_SYMBOL:
		{
			CmdSubscrSymbol _cmd = (CmdSubscrSymbol) cmd;
			services.getSymbolDataService().onSubscribe(_cmd.getSymbol(), _cmd.getLevel());
			break;
		}
		case UNSUBSCR_SYMBOL:
		{
			CmdUnsubscrSymbol _cmd = (CmdUnsubscrSymbol) cmd;
			services.getSymbolDataService().onUnsubscribe(_cmd.getSymbol(), _cmd.getLevel());
			break;
		}
		default:
		{
			IllegalArgumentException e = new IllegalArgumentException("Unidentified command: " + cmd); 
			cmd.getResult().completeExceptionally(e);
			throw e;
		}}
		return false;
	}

}

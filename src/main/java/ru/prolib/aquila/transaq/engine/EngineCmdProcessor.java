package ru.prolib.aquila.transaq.engine;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.transaq.engine.mp.MessageRouter;

public class EngineCmdProcessor implements Runnable {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(EngineCmdProcessor.class);
	}
	
	private final BlockingQueue<Cmd> cmdQueue;
	private final MessageRouter msgRouter;
	
	public EngineCmdProcessor(
			BlockingQueue<Cmd> cmd_queue,
			MessageRouter msg_router
		)
	{
		this.cmdQueue = cmd_queue;
		this.msgRouter = msg_router;
	}

	@Override
	public void run() {
		try {
			Cmd cmd = null;
			boolean shutdown = false;
			while ( shutdown == false && (cmd = cmdQueue.take()) != null ) {
				switch ( cmd.getType() ) {
				case SHUTDOWN:
					shutdown = true;
					break;
				case MSG_FROM_SERVER:
					msgRouter.dispatchMessage(((CmdMsgFromServer)cmd).getMessage());
					break;
				}
			}
		} catch ( InterruptedException e ) {
			logger.error("Unexpected interruption: ", e);
			Thread.currentThread().interrupt();
		}
	}

}

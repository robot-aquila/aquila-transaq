package ru.prolib.aquila.transaq.engine;

import java.util.concurrent.BlockingQueue;

public class EngineImpl implements Engine {
	private final BlockingQueue<Cmd> cmdQueue;
	
	public EngineImpl(BlockingQueue<Cmd> cmd_queue) {
		this.cmdQueue = cmd_queue;
	}

	@Override
	public void shutdown() {
		cmdQueue.add(new CmdShutdown());
	}

	@Override
	public void messageFromServer(String message) {
		cmdQueue.add(new CmdMsgFromServer(message));
	}

}

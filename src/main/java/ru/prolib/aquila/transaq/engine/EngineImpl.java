package ru.prolib.aquila.transaq.engine;

import java.util.concurrent.BlockingQueue;

import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class EngineImpl implements Engine {
	private final BlockingQueue<Cmd> cmdQueue;
	
	public EngineImpl(BlockingQueue<Cmd> cmd_queue) {
		this.cmdQueue = cmd_queue;
	}
	
	private void enqueue(Cmd cmd) {
		try {
			cmdQueue.put(cmd);
		} catch ( InterruptedException e ) {
			throw new IllegalStateException("Unexpected interruption: ", e);
		}
	}
	
	@Override
	public void connect() {
		enqueue(new CmdConnect());
	}
	
	@Override
	public void disconnect() {
		enqueue(new CmdDisconnect());
	}

	@Override
	public void shutdown() {
		enqueue(new CmdShutdown());
	}

	@Override
	public void messageFromServer(String message) {
		enqueue(new CmdMsgFromServer(message));
	}

	@Override
	public void subscribeSymbol(Symbol symbol, MDLevel level) {
		enqueue(new CmdSubscrSymbol(symbol, level));
	}

	@Override
	public void unsubscribeSymbol(Symbol symbol, MDLevel level) {
		enqueue(new CmdUnsubscrSymbol(symbol, level));
	}

}

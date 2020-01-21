package ru.prolib.aquila.transaq.engine;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class EngineImpl implements Engine {
	protected static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(EngineImpl.class);
	}
	
	private final BlockingQueue<Cmd> cmdQueue;
	
	public EngineImpl(BlockingQueue<Cmd> cmd_queue) {
		this.cmdQueue = cmd_queue;
	}
	
	private CompletableFuture<Boolean> enqueue(Cmd cmd) {
		try {
			//logger.debug("enqueue({})", cmd.getClass().getSimpleName());
			cmdQueue.put(cmd);
			return cmd.getResult();
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
	public CompletableFuture<Boolean> shutdown() {
		return enqueue(new CmdShutdown());
	}

	@Override
	public void messageFromServer(String message) {
		enqueue(new CmdMsgFromServer(message));
	}

	@Override
	public CompletableFuture<Boolean> subscribeSymbol(Symbol symbol, MDLevel level) {
		return enqueue(new CmdSubscrSymbol(symbol, level));
	}

	@Override
	public void unsubscribeSymbol(Symbol symbol, MDLevel level) {
		enqueue(new CmdUnsubscrSymbol(symbol, level));
	}

}

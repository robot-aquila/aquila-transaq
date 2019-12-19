package ru.prolib.aquila.transaq.engine;

import java.util.concurrent.CompletableFuture;

abstract public class Cmd {
	private final CmdType type;
	private final CompletableFuture<Boolean> result;
	
	public Cmd(CmdType type, CompletableFuture<Boolean> result) {
		this.type = type;
		this.result = result;
	}
	
	public Cmd(CmdType type) {
		this(type, new CompletableFuture<>());
	}
	
	public CmdType getType() {
		return type;
	}
	
	public CompletableFuture<Boolean> getResult() {
		return result;
	}
	
	@Override
	public abstract int hashCode();
	
	@Override
	public abstract boolean equals(Object other);
	
}

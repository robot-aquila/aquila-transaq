package ru.prolib.aquila.transaq.engine;

abstract public class Cmd {
	private final CmdType type;
	
	public Cmd(CmdType type) {
		this.type = type;
	}
	
	public CmdType getType() {
		return type;
	}
	
	@Override
	public abstract int hashCode();
	
	@Override
	public abstract boolean equals(Object other);
	
}

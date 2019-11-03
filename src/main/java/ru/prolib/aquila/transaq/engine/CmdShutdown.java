package ru.prolib.aquila.transaq.engine;

public class CmdShutdown extends Cmd {

	public CmdShutdown() {
		super(CmdType.SHUTDOWN);
	}

	@Override
	public int hashCode() {
		return 618243986;
	}

	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == CmdShutdown.class;
	}

}

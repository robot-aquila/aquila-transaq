package ru.prolib.aquila.transaq.engine;

public class CmdDisconnect extends Cmd {

	public CmdDisconnect() {
		super(CmdType.DISCONNECT);
	}

	@Override
	public int hashCode() {
		return 127163422;
	}

	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == CmdDisconnect.class;
	}

}

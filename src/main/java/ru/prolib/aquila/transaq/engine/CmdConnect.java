package ru.prolib.aquila.transaq.engine;

public class CmdConnect extends Cmd {

	public CmdConnect() {
		super(CmdType.CONNECT);
	}

	@Override
	public int hashCode() {
		return 761524314;
	}

	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == CmdConnect.class;
	}

}

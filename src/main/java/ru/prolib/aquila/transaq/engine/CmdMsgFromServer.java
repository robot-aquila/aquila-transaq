package ru.prolib.aquila.transaq.engine;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CmdMsgFromServer extends Cmd {
	private final String message;
	
	public CmdMsgFromServer(String message) {
		super(CmdType.MSG_FROM_SERVER);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(1526007, 13)
				.append(getType())
				.append(message)
				.build();
	}

	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CmdMsgFromServer.class ) {
			return false;
		}
		CmdMsgFromServer o = (CmdMsgFromServer) other;
		return new EqualsBuilder()
				.append(o.getType(), getType())
				.append(o.message, message)
				.build();
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append(getClass().getSimpleName()).append("[").append(message).append("]").toString();
	}

}

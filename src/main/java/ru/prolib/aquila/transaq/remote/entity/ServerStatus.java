package ru.prolib.aquila.transaq.remote.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ServerStatus {
	private final boolean connected, recover;
	private final String errorMsg;
	
	public ServerStatus(boolean connected, boolean recover, String error_msg) {
		this.connected = connected;
		this.recover = recover;
		this.errorMsg = error_msg;
	}
	
	public ServerStatus(boolean connected) {
		this(connected, false, null);
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public boolean isRecover() {
		return recover;
	}
	
	public boolean isError() {
		return errorMsg != null;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(1297015, 401)
				.append(connected)
				.append(recover)
				.append(errorMsg)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != ServerStatus.class ) {
			return false;
		}
		ServerStatus o = (ServerStatus) other;
		return new EqualsBuilder()
				.append(o.connected, connected)
				.append(o.recover, recover)
				.append(o.errorMsg, errorMsg)
				.build();
	}

}

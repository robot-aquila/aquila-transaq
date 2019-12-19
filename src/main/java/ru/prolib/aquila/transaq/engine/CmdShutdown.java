package ru.prolib.aquila.transaq.engine;

import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CmdShutdown extends Cmd {
	private final int phase;
	
	public CmdShutdown(CompletableFuture<Boolean> result, int phase) {
		super(CmdType.SHUTDOWN, result);
		this.phase = phase;
	}

	public CmdShutdown(int phase) {
		super(CmdType.SHUTDOWN);
		this.phase = phase;
	}
	
	public CmdShutdown() {
		this(0);
	}
	
	public int getPhase() {
		return phase;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(400971, 9009)
				.append(phase)
				.build();
	}

	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CmdShutdown.class ) {
			return false;
		}
		CmdShutdown o = (CmdShutdown) other;
		return new EqualsBuilder()
				.append(o.phase, phase)
				.build();
	}

}

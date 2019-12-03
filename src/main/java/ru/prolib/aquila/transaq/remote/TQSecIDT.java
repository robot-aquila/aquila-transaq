package ru.prolib.aquila.transaq.remote;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Unable to inherit of TQSecIDG because some messages do not provide market ID.
 */
public class TQSecIDT implements ISecIDT {
	protected final String secCode;
	protected final String boardCode;

	public TQSecIDT(String sec_code, String board_code) {
		if ( sec_code == null || sec_code.length() == 0 ) {
			throw new IllegalArgumentException("Invalid security code: " + sec_code);
		}
		if ( board_code == null || board_code.length() == 0 ) {
			throw new IllegalArgumentException("Invalid board code: " + board_code);
		}
		this.secCode = sec_code;
		this.boardCode = board_code;
	}
	
	@Override
	public String getSecCode() {
		return secCode;
	}
	
	@Override
	public String getBoardCode() {
		return boardCode;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(178624381, 95)
				.append(secCode)
				.append(boardCode)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TQSecIDT.class ) {
			return false;
		}
		TQSecIDT o = (TQSecIDT) other;
		return new EqualsBuilder()
				.append(o.secCode, secCode)
				.append(o.boardCode, boardCode)
				.build();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}

package ru.prolib.aquila.transaq.impl;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TQSecID2 {
	private final String secCode, boardCode;
	
	public TQSecID2(String sec_code, String board_code) {
		if ( sec_code == null || sec_code.length() == 0 ) {
			throw new IllegalArgumentException("Invalid security code: [" + sec_code + "]");
		}
		if ( board_code == null || board_code.length() == 0 ) {
			throw new IllegalArgumentException("Invalid board code: [" + board_code + "]");
		}
		this.secCode = sec_code;
		this.boardCode = board_code;
	}
	
	public String getSecCode() {
		return secCode;
	}
	
	public String getBoardCode() {
		return boardCode;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
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
		if ( other == null || other.getClass() != TQSecID2.class ) {
			return false;
		}
		TQSecID2 o = (TQSecID2) other;
		return new EqualsBuilder()
				.append(o.secCode, secCode)
				.append(o.boardCode, boardCode)
				.build();
	}

}

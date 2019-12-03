package ru.prolib.aquila.transaq.remote;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.transaq.entity.SecType;

public class TQSecIDF extends TQSecIDG implements ISecIDF {
	protected final String boardCode;
	protected final String shortName;
	protected final SecType type;
	
	public TQSecIDF(String sec_code, int market_id, String default_board, String short_name, SecType type) {
		super(sec_code, market_id);
		if ( default_board == null || default_board.length() == 0 ) {
			throw new IllegalArgumentException("Invalid board code: " + default_board);
		}
		if ( short_name == null || short_name.length() == 0 ) {
			throw new IllegalArgumentException("Invalid short name: " + short_name);
		}
		if ( type == null ) {
			throw new IllegalArgumentException("Type must be defined");
		}
		this.boardCode = default_board;
		this.shortName = short_name;
		this.type = type;
	}
	
	@Override
	public String getBoardCode() {
		return boardCode;
	}
	
	@Override
	public String getDefaultBoard() {
		return getBoardCode();
	}

	@Override
	public String getShortName() {
		return shortName;
	}
	
	@Override
	public SecType getType() {
		return type;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(234103, 1515)
				.append(secCode)
				.append(marketID)
				.append(boardCode)
				.append(shortName)
				.append(type)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TQSecIDF.class ) {
			return false;
		}
		TQSecIDF o = (TQSecIDF) other;
		return new EqualsBuilder()
				.append(o.secCode, secCode)
				.append(o.marketID, marketID)
				.append(o.boardCode, boardCode)
				.append(o.shortName, shortName)
				.append(o.type, type)
				.build();
	}

}

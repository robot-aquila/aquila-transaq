package ru.prolib.aquila.transaq.engine.sds;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Symbol Tradable Identifier.
 * <p>
 * See {@link SymbolGID} for more info.
 */
public class SymbolTID extends SymbolGID {
	protected String board;
	
	public SymbolTID(String ticker, int market_id, String board) {
		super(ticker, market_id);
		this.board = board;
	}
	
	public String getBoard() {
		return board;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(903115, 1207)
				.append(ticker)
				.append(marketID)
				.append(board)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SymbolTID.class ) {
			return false;
		}
		SymbolTID o = (SymbolTID) other;
		return new EqualsBuilder()
				.append(o.ticker, ticker)
				.append(o.marketID,  marketID)
				.append(o.board,  board)
				.build();
	}
	
	public SymbolGID toGID() {
		return new SymbolGID(ticker, marketID);
	}

}

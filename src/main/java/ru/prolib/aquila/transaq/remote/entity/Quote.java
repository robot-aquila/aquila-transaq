package ru.prolib.aquila.transaq.remote.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.transaq.remote.ISecIDT;

public class Quote {
	private final ISecIDT id;
	private final CDecimal price;
	private final Long yield, buy, sell;

	public Quote(ISecIDT id, CDecimal price, Long yield, Long buy, Long sell) {
		this.id = id;
		this.price = price;
		this.yield = yield;
		this.buy = buy;
		this.sell = sell;
	}
	
	public ISecIDT getID() {
		return id;
	}
	
	public CDecimal getPrice() {
		return price;
	}
	
	public Long getYield() {
		return yield;
	}
	
	public Long getBuy() {
		return buy;
	}
	
	public Long getSell() {
		return sell;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append(getClass().getSimpleName())
				.append("[secCode=").append(id.getSecCode())
				.append(",boardCode=").append(id.getBoardCode())
				.append(",price=").append(price)
				.append(",yield=").append(yield)
				.append(",buy=").append(buy)
				.append(",sell=").append(sell)
				.append("]")
				.toString();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(2000981, 5043)
				.append(id)
				.append(price)
				.append(yield)
				.append(buy)
				.append(sell)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != Quote.class ) {
			return false;
		}
		Quote o = (Quote) other;
		return new EqualsBuilder()
				.append(o.id, id)
				.append(o.price, price)
				.append(o.yield, yield)
				.append(o.buy, buy)
				.append(o.sell, sell)
				.build();
	}
	
}

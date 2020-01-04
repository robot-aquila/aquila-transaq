package ru.prolib.aquila.transaq.remote;

import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class ID {
	
	public enum Type {
		MONEY_POSITION,
		SEC_POSITION,
		FORTS_POSITION,
		FORTS_MONEY,
		FORTS_COLLATERALS,
		SPOT_LIMIT,
		UNITED_LIMITS
	}
	
	public abstract Type getType();
	
	/**
	 * ID of money_position.
	 */
	public static class MP extends ID {
		private final String clientID;
		private final String asset;
		private final String register;
		
		public MP(String client_id, String asset, String register) {
			this.clientID = client_id;
			this.asset = asset;
			this.register = register;
		}
		
		@Override
		public Type getType() {
			return Type.MONEY_POSITION;
		}
		
		public String getClientID() {
			return clientID;
		}
		
		public String getAsset() {
			return asset;
		}
		
		public String getRegister() {
			return register;
		}
		
		@Override
		public int hashCode() {
			return new HashCodeBuilder(6009817, 5501)
					.append(clientID)
					.append(asset)
					.append(register)
					.build();
		}
		
		@Override
		public String toString() {
			return new StringBuilder()
					.append("ID.MP[clientID=").append(clientID)
					.append(",asset=").append(asset)
					.append(",register=").append(register).append("]")
					.toString();
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != MP.class ) {
				return false;
			}
			MP o = (MP) other;
			return new EqualsBuilder()
					.append(o.clientID, clientID)
					.append(o.asset, asset)
					.append(o.register, register)
					.build();
		}
		
	}
	
	/**
	 * ID of sec_position.
	 */
	public static class SP extends ID {
		private final String clientID;
		private final String secCode;
		private final int marketID;
		private final String register;
		
		public SP(String client_id, String sec_code, int market_id, String register) {
			this.clientID = client_id;
			this.secCode = sec_code;
			this.marketID = market_id;
			this.register = register;
		}
		
		@Override
		public Type getType() {
			return Type.SEC_POSITION;
		}
		
		public String getClientID() {
			return clientID;
		}
		
		public String getSecCode() {
			return secCode;
		}
		
		public int getMarketID() {
			return marketID;
		}
		
		public String getRegister() {
			return register;
		}
		
		@Override
		public String toString() {
			return new StringBuilder()
					.append("ID.SP[clientID=").append(clientID)
					.append(",secCode=").append(secCode)
					.append(",marketID=").append(marketID)
					.append(",register=").append(register).append("]")
					.toString();
		}
		
		@Override
		public int hashCode() {
			return new HashCodeBuilder(297159, 901)
					.append(clientID)
					.append(secCode)
					.append(marketID)
					.append(register)
					.build();
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != SP.class ) {
				return false;
			}
			SP o = (SP) other;
			return new EqualsBuilder()
					.append(o.clientID, clientID)
					.append(o.secCode, secCode)
					.append(o.marketID, marketID)
					.append(o.register, register)
					.build();
		}
		
	}
	
	/**
	 * ID of forts_position.
	 */
	public static class FP extends ID {
		private final String clientID;
		private final String secCode;
		private final Set<Integer> markets;
		
		public FP(String client_id, String sec_code, Set<Integer> markets) {
			this.clientID = client_id;
			this.secCode = sec_code;
			this.markets = markets;
		}
		
		@Override
		public Type getType() {
			return Type.FORTS_POSITION;
		}
		
		public String getClientID() {
			return clientID;
		}
		
		public String getSecCode() {
			return secCode;
		}
		
		public Set<Integer> getMarkets() {
			return markets;
		}
		
		@Override
		public String toString() {
			return new StringBuilder()
					.append("ID.FP[clientID=").append(clientID)
					.append(",secCode=").append(secCode)
					.append(",markets=").append(markets).append("]")
					.toString();
		}
		
		@Override
		public int hashCode() {
			return new HashCodeBuilder(900163, 309)
					.append(clientID)
					.append(secCode)
					.append(markets)
					.build();
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != FP.class ) {
				return false;
			}
			FP o = (FP) other;
			return new EqualsBuilder()
					.append(o.clientID, clientID)
					.append(o.secCode, secCode)
					.append(o.markets, markets)
					.build();
		}
		
	}

	static abstract class X extends ID {
		private final String clientID;
		private final Set<Integer> markets;
		
		public X(String client_id, Set<Integer> markets) {
			this.clientID = client_id;
			this.markets = markets;
		}
		
		public String getClientID() {
			return clientID;
		}
		
		public Set<Integer> getMarkets() {
			return markets;
		}
		
		@Override
		public String toString() {
			return new StringBuilder()
					.append("ID.").append(getClass().getSimpleName())
					.append("[clientID=").append(clientID)
					.append(",markets=").append(markets).append("]")
					.toString();
		}
		
		@Override
		public int hashCode() {
			return new HashCodeBuilder(getInitHcbNum(), getMultHcbNum())
					.append(clientID)
					.append(markets)
					.build();
		}
		
		protected abstract int getInitHcbNum();
		protected abstract int getMultHcbNum();
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != getClass() ) {
				return false;
			}
			X o = (X) other;
			return new EqualsBuilder()
					.append(o.clientID, clientID)
					.append(o.markets, markets)
					.build();
		}
		
	}
	
	/**
	 * ID of forts_money.
	 */
	public static class FM extends ID {
		private final String clientID;

		public FM(String client_id) {
			this.clientID = client_id;
		}
		
		@Override
		public Type getType() {
			return Type.FORTS_MONEY;
		}
		
		public String getClientID() {
			return clientID;
		}
		
		protected int getInitHcbNum() {
			return 7009715;
		}

		protected int getMultHcbNum() {
			return 51;
		}
		
		@Override
		public int hashCode() {
			return new HashCodeBuilder(getInitHcbNum(), getMultHcbNum())
					.append(clientID)
					.build();
		}
		
		@Override
		public String toString() {
			return new StringBuilder().append("ID.FM[clientID=").append(clientID).append("]").toString();
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null || other.getClass() != FM.class ) {
				return false;
			}
			FM o = (FM) other;
			return new EqualsBuilder()
					.append(o.clientID, clientID)
					.build();
		}
		
	}
	
	/**
	 * ID of forts_collaterals.
	 */
	public static class FC extends X {

		public FC(String client_id, Set<Integer> markets) {
			super(client_id, markets);
		}
		
		@Override
		public Type getType() {
			return Type.FORTS_COLLATERALS;
		}

		@Override
		protected int getInitHcbNum() {
			return 855501;
		}

		@Override
		protected int getMultHcbNum() {
			return 77;
		}
		
	}
	
	/**
	 * ID of spot_limit.
	 */
	public static class SL extends X {

		public SL(String client_id, Set<Integer> markets) {
			super(client_id, markets);
		}
		
		@Override
		public Type getType() {
			return Type.SPOT_LIMIT;
		}

		@Override
		protected int getInitHcbNum() {
			return 9997861;
		}

		@Override
		protected int getMultHcbNum() {
			return 331;
		}
		
	}
	
	/**
	 * ID of united_limits.
	 */
	public static class UL extends ID {
		private final String unionCode;
		
		public UL(String union_code) {
			this.unionCode = union_code;
		}
		
		@Override
		public Type getType() {
			return Type.UNITED_LIMITS;
		}
		
		public String getUnionCode() {
			return unionCode;
		}
		
		@Override
		public String toString() {
			return new StringBuilder()
					.append("ID.UL[unionCode=").append(unionCode).append("]")
					.toString();
		}
		
		@Override
		public int hashCode() {
			return new HashCodeBuilder(7781, 13)
					.append(unionCode)
					.build();
		}
		
		@Override
		public boolean equals(Object other) {
			if ( other == this ) {
				return true;
			}
			if( other == null || other.getClass() != UL.class ) {
				return false;
			}
			UL o = (UL) other;
			return new EqualsBuilder()
					.append(o.unionCode, unionCode)
					.build();
		}
		
	}

}
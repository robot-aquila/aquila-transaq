package ru.prolib.aquila.transaq.remote;

public class MessageFields {
	
	/**
	 * Field identifiers of messages:
	 * <p>
	 * securities/security</br>
	 * sec_info</br>
	 * sec_info_upd</br>
	 */
	public static class FSecurity {
		public static final int SECID				= 5200; // int
		public static final int SECCODE				= 5201; // string
		public static final int MARKETID			= 5202; // int
		public static final int ACTIVE				= 5203; // bool
		public static final int SECCLASS			= 5204; // string
		public static final int DEFAULT_BOARDCODE	= 5205; // string
		public static final int SHORT_NAME			= 5206; // string
		public static final int DECIMALS			= 5207; // int
		public static final int MINSTEP				= 5208; // CDecimal
		public static final int LOTSIZE				= 5209; // CDecimal
		public static final int POINT_COST			= 5210; // CDecimal
		public static final int OPMASK				= 5211; // int
		public static final int SECTYPE				= 5212; // SecType
		public static final int SECTZ				= 5213; // string
		public static final int QUOTESTYPE			= 5214; // int
		public static final int SECNAME				= 5220; // string
		public static final int PNAME				= 5225; // string
		public static final int MAT_DATE			= 5226; // LocalDateTime
		public static final int CLEARING_PRICE		= 5227; // CDecimal
		public static final int MINPRICE			= 5228; // CDecimal
		public static final int MAXPRICE			= 5229; // CDecimal
		public static final int BUY_DEPOSIT			= 5230; // CDecimal
		public static final int SELL_DEPOSIT		= 5231; // CDecimal
		public static final int BGO_C				= 5232; // CDecimal
		public static final int BGO_NC				= 5233; // CDecimal
		public static final int ACCRUED_INT			= 5234; // CDecimal
		public static final int COUPON_VALUE		= 5235; // CDecimal
		public static final int COUPON_DATE			= 5236; // LocalDateTime
		public static final int COUPON_PERIOD		= 5237; // int
		public static final int FACE_VALUE			= 5238; // CDecimal
		public static final int PUT_CALL			= 5239; // string
		public static final int OPT_TYPE			= 5240; // string
		public static final int LOT_VOLUME			= 5241; // int
		public static final int BGO_BUY				= 5242; // CDecimal
	}

	/**
	 * Field identifiers of markets/market message.
	 */
	public static class FMarket {
		public static final int ID					= 5301; // int
		public static final int NAME				= 5302; // string
	}

	/**
	 * Field identifiers of boards/board message
	 */
	public static class FBoard {
		public static final int CODE				= 5401; // string
		public static final int NAME				= 5402; // string
		public static final int TYPE				= 5403; // int
		public static final int MARKET_ID			= 5404; // int
	}
	
	/**
	 * Field identifiers of candlekinds/kind message
	 */
	public static class FCKind {
		public static final int CKIND_ID			= 5521; // int
		public static final int CKIND_PERIOD		= 5522; // int
		public static final int CKIND_NAME			= 5523; // string
	}
	
	/**
	 * Field identifiers of messages:
	 * <p>
	 * pits/pit</br>
	 */
	public static class FSecurityBoard {
		public static final int SECCODE				= 5602;
		public static final int BOARD				= 5603;
		public static final int MARKET				= 5604;
		public static final int DECIMALS			= 5605;
		public static final int MINSTEP				= 5606;
		public static final int LOTSIZE				= 5607;
		public static final int POINT_COST			= 5608;
	}
	
	/**
	 * Field identifiers of quotations/quotation message.
	 */
	public static class FQuotation {
		public static final int SECID				= 5709;
		public static final int BOARD				= 5710;
		public static final int SECCODE				= 5711;
		public static final int POINT_COST			= 5712;
		public static final int ACCRUED_INT_VALUE	= 5713;
		public static final int OPEN				= 5714;
		public static final int WA_PRICE			= 5715;
		public static final int BID_DEPTH			= 5716;
		public static final int BID_DEPTH_T			= 5717;
		public static final int NUM_BIDS			= 5718;
		public static final int OFFER_DEPTH			= 5719;
		public static final int OFFER_DEPTH_T		= 5720;
		public static final int BID					= 5721;
		public static final int OFFER				= 5722;
		public static final int NUM_OFFERS			= 5723;
		public static final int NUM_TRADES			= 5724;
		public static final int VOL_TODAY			= 5725;
		public static final int OPEN_POSITIONS		= 5726;
		public static final int DELTA_POSITIONS		= 5727;
		public static final int LAST				= 5728;
		public static final int QUANTITY			= 5729;
		public static final int TIME				= 5730;
		public static final int CHANGE				= 5731;
		public static final int PRICE_MINUS_PREV_WA_PRICE = 5732;
		public static final int VAL_TODAY			= 5733;
		public static final int YIELD				= 5734;
		public static final int YIELD_AT_WA_PRICE	= 5735;
		public static final int MARKET_PRICE_TODAY	= 5736;
		public static final int HIGH_BID			= 5737;
		public static final int LOW_OFFER			= 5738;
		public static final int HIGH				= 5739;
		public static final int LOW					= 5740;
		public static final int CLOSE_PRICE			= 5741;
		public static final int CLOSE_YIELD			= 5742;
		public static final int STATUS				= 5743;
		public static final int TRADING_STATUS		= 5744;
		public static final int BUY_DEPOSIT			= 5745;
		public static final int SELL_DEPOSIT		= 5746;
		public static final int VOLATILITY			= 5747;
		public static final int THEORETICAL_PRICE	= 5748;
		public static final int BGO_BUY				= 5749;
		public static final int L_CURRENT_PRICE		= 5750;
	}
	
	/**
	 * Field identifiers of alltrades/trade message.
	 */
	public static class FTrade {
		public static final int SECID				= 5809;
		public static final int SECCODE				= 5810;
		public static final int TRADENO				= 5811;
		public static final int TIME				= 5812;
		public static final int BOARD				= 5813;
		public static final int PRICE				= 5814;
		public static final int QUANTITY			= 5815;
		public static final int BUYSELL				= 5816;
		public static final int OPENINTEREST		= 5817;
		public static final int PERIOD				= 5818;
	}
	
	/**
	 * Field identifiers of quotes/quote message.
	 */
	//public static class FQuote {
	//	public static final int SECID				= 5909;
	//	public static final int SECCODE				= 5910;
	//	public static final int BOARD				= 5911;
	//	public static final int PRICE				= 5912;
	//	public static final int SOURCE				= 5913;
	//	public static final int YIELD				= 5914;
	//	public static final int BUY					= 5915;
	//	public static final int SELL				= 5916;
	//}

}

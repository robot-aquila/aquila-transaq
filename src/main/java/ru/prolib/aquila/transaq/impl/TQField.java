package ru.prolib.aquila.transaq.impl;

public class TQField {
	
	public static class FSecurity {
		// <securities>
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

	public static class FMarket {
		public static final int ID					= 5301; // int
		public static final int NAME				= 5302; // string
	}

	public static class FBoard {
		public static final int CODE				= 5401; // string
		public static final int NAME				= 5402; // string
		public static final int TYPE				= 5403; // int
		public static final int MARKET_ID			= 5404; // int
	}
	
	public static class FCKind {
		public static final int CKIND_ID			= 5521; // int
		public static final int CKIND_PERIOD		= 5522; // int
		public static final int CKIND_NAME			= 5523; // string
	}
	
	public static class FSecurityBoard {
		public static final int SECCODE				= 5602;
		public static final int BOARD				= 5603;
		public static final int MARKET				= 5604;
		public static final int DECIMALS			= 5605;
		public static final int MINSTEP				= 5606;
		public static final int LOTSIZE				= 5607;
		public static final int POINT_COST			= 5608;
	}
	
	public static class FQuotation {
		public static final int SECID				= 57009;
		public static final int BOARD				= 57010;
		public static final int SECCODE				= 57011;
		public static final int POINT_COST			= 57012;
		public static final int ACCRUED_INT_VALUE	= 57013;
		public static final int OPEN				= 57014;
		public static final int WA_PRICE			= 57015;
		public static final int BID_DEPTH			= 57016;
		public static final int BID_DEPTH_T			= 57017;
		public static final int NUM_BIDS			= 57018;
		public static final int OFFER_DEPTH			= 57019;
		public static final int OFFER_DEPTH_T		= 57020;
		public static final int BID					= 57021;
		public static final int OFFER				= 57022;
		public static final int NUM_OFFERS			= 57023;
		public static final int NUM_TRADES			= 57024;
		public static final int VOL_TODAY			= 57025;
		public static final int OPEN_POSITIONS		= 57026;
		public static final int DELTA_POSITIONS		= 57027;
		public static final int LAST				= 57028;
		public static final int QUANTITY			= 57029;
		public static final int TIME				= 57030;
		public static final int CHANGE				= 57031;
		public static final int PRICE_MINUS_PREV_WA_PRICE = 57032;
		public static final int VAL_TODAY			= 57033;
		public static final int YIELD				= 57034;
		public static final int YIELD_AT_WA_PRICE	= 57035;
		public static final int MARKET_PRICE_TODAY	= 57036;
		public static final int HIGH_BID			= 57037;
		public static final int LOW_OFFER			= 57038;
		public static final int HIGH				= 57039;
		public static final int LOW					= 57040;
		public static final int CLOSE_PRICE			= 57041;
		public static final int CLOSE_YIELD			= 57042;
		public static final int STATUS				= 57043;
		public static final int TRADING_STATUS		= 57044;
		public static final int BUY_DEPOSIT			= 57045;
		public static final int SELL_DEPOSIT		= 57046;
		public static final int VOLATILITY			= 57047;
		public static final int THEORETICAL_PRICE	= 57048;
		public static final int BGO_BUY				= 57049;
		public static final int L_CURRENT_PRICE		= 57050;
	}

}

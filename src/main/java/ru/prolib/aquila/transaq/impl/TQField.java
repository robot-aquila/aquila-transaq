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
		public static final int NAME				= 5402; // int
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

}

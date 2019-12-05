package ru.prolib.aquila.transaq.ui;

import ru.prolib.aquila.core.text.Messages;
import ru.prolib.aquila.core.text.MsgID;

public class TQMessages {
	public static final String SECTION_ID = "Transaq";
	
	static{
		Messages.registerLoader(SECTION_ID, TQMessages.class.getClassLoader());
		Messages.setDefaultMsgIDs(SECTION_ID, TQMessages.class);
	}
	
	public static MsgID
		SERVICE_MENU,
		SHOW_CKINDS,
		SHOW_MARKETS,
		SHOW_BOARDS,
		SHOW_SEC_PARAMS,
		SHOW_SEC_BRD_PARAMS,
		SHOW_SEC_QUOTATIONS,
		DIALOG_TITLE_MARKETS,
		DIALOG_TITLE_BOARDS,
		DIALOG_TITLE_CKINDS,
		DIALOG_TITLE_SEC_PARAMS,
		DIALOG_TITLE_SEC_BRD_PARAMS,
		DIALOG_TITLE_SEC_QUOTATIONS,
		MARKET_ID,
		TYPE_ID,
		SEC_ID,
		SEC_CODE,
		ACTIVE,
		SEC_CLASS,
		BOARD,
		DEFAULT_BOARD,
		SHORT_NAME,
		DECIMALS,
		MIN_STEP,
		LOT_SIZE,
		POINT_COST,
		OPMASK,
		SEC_TYPE,
		SEC_TZ,
		QUOTES_TYPE,
		SEC_NAME,
		PNAME,
		MAT_DATE,
		CLEARING_PRICE,
		MIN_PRICE,
		MAX_PRICE,
		BUY_DEPOSIT,
		SELL_DEPOSIT,
		BGO_C,
		BGO_NC,
		ACCRUED_INT,
		COUPON_VALUE,
		COUPON_DATE,
		COUPON_PERIOD,
		FACE_VALUE,
		PUT_CALL,
		OPT_TYPE,
		LOT_VOLUME,
		BGO_BUY,
		THEORETICAL_PRICE,
		VOLATILITY,
		TRADING_STATUS,
		CLOSE_YIELD,
		CLOSE_PRICE,
		LOW_OFFER,
		HIGH_BID,
		MARKET_PRICE_TODAY,
		YIELD_AT_WA_PRICE,
		YIELD,
		VAL_TODAY,
		PRICE_MINUS_PREV_WA_PRICE,
		CHANGE,
		DELTA_POSITIONS,
		OPEN_POSITIONS,
		VOL_TODAY,
		NUM_TRADES,
		NUM_OFFERS,
		OFFER_DEPTH_T,
		NUM_BIDS,
		BID_DEPTH_T,
		WA_PRICE
		;
	
}

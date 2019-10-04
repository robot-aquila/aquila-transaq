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
		DIALOG_TITLE_MARKETS,
		DIALOG_TITLE_BOARDS,
		DIALOG_TITLE_CKINDS,
		MARKET_ID,
		TYPE_ID;
	
}

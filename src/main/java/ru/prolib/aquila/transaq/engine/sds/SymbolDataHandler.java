package ru.prolib.aquila.transaq.engine.sds;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.SecurityField;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.transaq.engine.ServiceLocator;
import ru.prolib.aquila.transaq.entity.SecurityBoardParams;
import ru.prolib.aquila.transaq.entity.SecurityParams;
import ru.prolib.aquila.transaq.impl.TQFieldAssembler;

public class SymbolDataHandler {
	/*
	private final SymbolTID tid;
	private final Symbol symbol;
	private final ServiceLocator services;
	private EditableSecurity security;
	private SecurityParams securityParams;
	
	public SymbolDataHandler(SymbolTID tid, Symbol symbol, ServiceLocator services) {
		this.tid = tid;
		this.symbol = symbol;
		this.services = services;
	}
	
	private EditableSecurity getSecurity() {
		if ( security == null ) {
			security = services.getTerminal().getEditableSecurity(symbol);
		}
		return security;
	}
	
	private SecurityParams getSecurityParams() {
		if ( securityParams == null ) {
			if ( services.getDirectory().isExistsSecurityParams(tid) ) {
				securityParams = services.getDirectory().getSecurityParams(tid);
			}
		}
		return securityParams;
	}
	*/
	
	public void updateSecurityParams() {
		//SecurityParams sp = getSecurityParams();
		//if ( sp == null ) {
		//	return;
		//}
		
		//TQFieldAssembler asm = services.getAssembler();
		//DeltaUpdateBuilder builder = new DeltaUpdateBuilder();
		

		//SecurityBoardParams sbp = null;
		//sbp.getm
		
		/*
			SecurityField.DISPLAY_NAME			= sp#ShortName@board#Code;
			SecurityField.INITIAL_MARGIN		= MAX(sp#BuyDeposit, sp#SellDeposit) (FORTS only)
			SecurityField.UPPER_PRICE_LIMIT		= sp#MinPrice (FORTS only)
			SecurityField.LOWER_PRICE_LIMIT		= sp#MaxPrice (FORTS only)
			SecurityField.SETTLEMENT_PRICE		= N/A (получение данных не подтверждено) sp#ClearingPrice (FORTS only)
			SecurityField.EXPIRATION_TIME		= N/A (получение данных не подтверждено) sp@MatDate
			
			Проблема: settlement price определить не удается. Не передается clearing_price даже для фортса.
						
			SecurityField.LOT_SIZE 				= sbp#LotSize
			SecurityField.TICK_SIZE				= sbp#MinStep
			SecurityField.TICK_VALUE			= 10 ^ sbp#Decimals * sbp#PointCost * sbp#MinStep / 100

			SecurityField.OPEN_PRICE			= quot#Open
			SecurityField.HIGH_PRICE			= quot#High
			SecurityField.LOW_PRICE				= quot#Low
			SecurityField.CLOSE_PRICE			= quot#ClosePrice
			
			BestBid								= price=quot#Bid, qty=quot#BidDepth, time=NOW
			BestAsk								= price=quot#Offer, qty=quot#OfferDepth, time=NOW
			Last								= Поток alltrades 
			MarketDepth							= Поток quotes
			
		*/

	}
	
	public void updateSecurityBoardParams() {
		
	}
	
	public void updateQuotations() {
		
	}

}

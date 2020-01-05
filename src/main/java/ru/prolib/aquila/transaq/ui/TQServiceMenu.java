package ru.prolib.aquila.transaq.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableRowSorter;

import ru.prolib.aquila.core.BusinessEntities.osc.OSCRepository;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.transaq.engine.sds.GSymbol;
import ru.prolib.aquila.transaq.engine.sds.TSymbol;
import ru.prolib.aquila.transaq.entity.Board;
import ru.prolib.aquila.transaq.entity.CKind;
import ru.prolib.aquila.transaq.entity.Market;
import ru.prolib.aquila.transaq.entity.SecurityBoardParams;
import ru.prolib.aquila.transaq.entity.SecurityParams;
import ru.prolib.aquila.transaq.entity.SecurityQuotations;
import ru.prolib.aquila.transaq.impl.TQDirectory;
import ru.prolib.aquila.transaq.remote.ID;
import ru.prolib.aquila.transaq.remote.MessageFields.FBoard;
import ru.prolib.aquila.transaq.remote.MessageFields.FCKind;
import ru.prolib.aquila.transaq.remote.MessageFields.FClient;
import ru.prolib.aquila.transaq.remote.MessageFields.FMarket;
import ru.prolib.aquila.transaq.remote.MessageFields.FQuotation;
import ru.prolib.aquila.transaq.remote.MessageFields.FSecurity;
import ru.prolib.aquila.transaq.remote.MessageFields.FSecurityBoard;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FFortsCollaterals;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FFortsMoney;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FFortsPosition;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FMoneyPosition;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FSecPosition;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FSpotLimits;
import ru.prolib.aquila.transaq.remote.MessageFields.Positions.FUnitedLimits;
import ru.prolib.aquila.transaq.remote.entity.Client;
import ru.prolib.aquila.transaq.remote.entity.FortsCollaterals;
import ru.prolib.aquila.transaq.remote.entity.FortsMoney;
import ru.prolib.aquila.transaq.remote.entity.FortsPosition;
import ru.prolib.aquila.transaq.remote.entity.MoneyPosition;
import ru.prolib.aquila.transaq.remote.entity.SecPosition;
import ru.prolib.aquila.transaq.remote.entity.SpotLimits;
import ru.prolib.aquila.transaq.remote.entity.UnitedLimits;
import ru.prolib.aquila.ui.ITableModel;
import ru.prolib.aquila.ui.TableModelController;
import ru.prolib.aquila.ui.form.OSCRepositoryTableModel;
import ru.prolib.aquila.ui.msg.CommonMsg;
import ru.prolib.aquila.ui.msg.SecurityMsg;

public class TQServiceMenu implements ActionListener {
	public static final String ITEM_SHOW_MARKETS = "SHOW_MARKETS";
	public static final String ITEM_SHOW_BOARDS = "SHOW_BOARDS";
	public static final String ITEM_SHOW_CKINDS = "SHOW_CKINDS";
	public static final String ITEM_SHOW_SEC_PARAMS = "SHOW_SEC_PARAMS";
	public static final String ITEM_SHOW_SEC_BRD_PARAMS = "SHOW_SEC_BRD_PARAMS";
	public static final String ITEM_SHOW_SEC_QUOTATIONS = "SHOW_SEC_QUOTATIONS";
	public static final String ITEM_SHOW_CLIENTS = "SHOW_CLIENTS";
	public static final String ITEM_SHOW_POS_MONEY_POSITIONS = "SHOW_POS_MONEY_POSITIONS";
	public static final String ITEM_SHOW_POS_SEC_POSITIONS = "SHOW_POS_SEC_POSITIONS";
	public static final String ITEM_SHOW_POS_FORTS_MONEY = "SHOW_POS_FORTS_MONEY";
	public static final String ITEM_SHOW_POS_FORTS_POSITIONS = "SHOW_POS_FORTS_POSITIONS";
	public static final String ITEM_SHOW_POS_FORTS_COLLATERALS = "SHOW_POS_FORTS_COLLATERALS";
	public static final String ITEM_SHOW_POS_SPOT_LIMITS = "SHOW_POS_SPOT_LIMITS";
	public static final String ITEM_SHOW_POS_UNITED_LIMITS = "SHOW_POS_UNITED_LIMITS";
	
	interface DialogFactory {
		JDialog produce();
	}
	
	static class LazyDialogInitializer implements DialogFactory {
		private final DialogFactory factory;
		private JDialog dialog;
		
		public LazyDialogInitializer(DialogFactory factory) {
			this.factory = factory;
		}

		@Override
		public JDialog produce() {
			if ( dialog == null ) {
				dialog = factory.produce();
			}
			return dialog;
		}
		
	}
	
	private final IMessages messages;
	private final JFrame frame;
	private final TQDirectory directory;
	private final Map<String, DialogFactory> actionCmdToDialogMap = new HashMap<>();
	
	public TQServiceMenu(IMessages messages, JFrame frame, TQDirectory directory) {
		this.messages = messages;
		this.frame = frame;
		this.directory = directory;
	}
	
	public OSCRepositoryTableModel<Market> createMarketTableModel(OSCRepository<Integer, Market> repository) {
		List<Integer> column_id_list = new ArrayList<>();
		column_id_list.add(FMarket.ID);
		column_id_list.add(FMarket.NAME);
		Map<Integer, MsgID> column_id_to_header = new HashMap<>();
		column_id_to_header.put(FMarket.ID, CommonMsg.ID);
		column_id_to_header.put(FMarket.NAME, CommonMsg.NAME);
		return new OSCRepositoryTableModel<>(messages, repository, column_id_list, column_id_to_header);
	}
	
	public OSCRepositoryTableModel<Board> createBoardTableModel(OSCRepository<String, Board> repository) {
		List<Integer> column_id_list = new ArrayList<>();
		column_id_list.add(FBoard.CODE);
		column_id_list.add(FBoard.MARKET_ID);
		column_id_list.add(FBoard.NAME);
		column_id_list.add(FBoard.TYPE);
		Map<Integer, MsgID> column_id_to_header = new HashMap<>();
		column_id_to_header.put(FBoard.CODE, CommonMsg.CODE);
		column_id_to_header.put(FBoard.MARKET_ID, TQMessages.MARKET_ID);
		column_id_to_header.put(FBoard.NAME, CommonMsg.NAME);
		column_id_to_header.put(FBoard.TYPE, TQMessages.TYPE_ID);
		return new OSCRepositoryTableModel<>(messages, repository, column_id_list, column_id_to_header);
	}
	
	public OSCRepositoryTableModel<CKind> createCKindTableModel(OSCRepository<Integer, CKind> repository) {
		List<Integer> column_id_list = new ArrayList<>();
		column_id_list.add(FCKind.CKIND_ID);
		column_id_list.add(FCKind.CKIND_PERIOD);
		column_id_list.add(FCKind.CKIND_NAME);
		Map<Integer, MsgID> column_id_to_header = new HashMap<>();
		column_id_to_header.put(FCKind.CKIND_ID, CommonMsg.ID);
		column_id_to_header.put(FCKind.CKIND_PERIOD, CommonMsg.PERIOD);
		column_id_to_header.put(FCKind.CKIND_NAME, CommonMsg.NAME);
		return new OSCRepositoryTableModel<>(messages, repository, column_id_list, column_id_to_header);
	}
	
	public OSCRepositoryTableModel<SecurityParams>
		createSecurityParamsTableModel(OSCRepository<GSymbol, SecurityParams> repository)
	{
		List<Integer> column_id_list = new ArrayList<>();
		column_id_list.add(FSecurity.SECID);
		column_id_list.add(FSecurity.SECCODE);
		column_id_list.add(FSecurity.MARKETID);
		column_id_list.add(FSecurity.ACTIVE);
		column_id_list.add(FSecurity.SECCLASS);
		column_id_list.add(FSecurity.DEFAULT_BOARDCODE);
		column_id_list.add(FSecurity.SHORT_NAME);
		column_id_list.add(FSecurity.DECIMALS);
		column_id_list.add(FSecurity.MINSTEP);
		column_id_list.add(FSecurity.LOTSIZE);
		column_id_list.add(FSecurity.POINT_COST);
		column_id_list.add(FSecurity.OPMASK);
		column_id_list.add(FSecurity.SECTYPE);
		column_id_list.add(FSecurity.SECTZ);
		column_id_list.add(FSecurity.QUOTESTYPE);
		column_id_list.add(FSecurity.SECNAME);
		column_id_list.add(FSecurity.PNAME);
		column_id_list.add(FSecurity.MAT_DATE);
		column_id_list.add(FSecurity.CLEARING_PRICE);
		column_id_list.add(FSecurity.MINPRICE);
		column_id_list.add(FSecurity.MAXPRICE);
		column_id_list.add(FSecurity.BUY_DEPOSIT);
		column_id_list.add(FSecurity.SELL_DEPOSIT);
		column_id_list.add(FSecurity.BGO_C);
		column_id_list.add(FSecurity.BGO_NC);
		column_id_list.add(FSecurity.ACCRUED_INT);
		column_id_list.add(FSecurity.COUPON_VALUE);
		column_id_list.add(FSecurity.COUPON_DATE);
		column_id_list.add(FSecurity.COUPON_PERIOD);
		column_id_list.add(FSecurity.FACE_VALUE);
		column_id_list.add(FSecurity.PUT_CALL);
		column_id_list.add(FSecurity.OPT_TYPE);
		column_id_list.add(FSecurity.LOT_VOLUME);
		column_id_list.add(FSecurity.BGO_BUY);
		Map<Integer, MsgID> column_id_to_header = new HashMap<>();
		column_id_to_header.put(FSecurity.SECID, TQMessages.SEC_ID);
		column_id_to_header.put(FSecurity.SECCODE, TQMessages.SEC_CODE);
		column_id_to_header.put(FSecurity.MARKETID, TQMessages.MARKET_ID);
		column_id_to_header.put(FSecurity.ACTIVE, TQMessages.ACTIVE);
		column_id_to_header.put(FSecurity.SECCLASS, TQMessages.SEC_CLASS);
		column_id_to_header.put(FSecurity.DEFAULT_BOARDCODE, TQMessages.DEFAULT_BOARD);
		column_id_to_header.put(FSecurity.SHORT_NAME, TQMessages.SHORT_NAME);
		column_id_to_header.put(FSecurity.DECIMALS, TQMessages.DECIMALS);
		column_id_to_header.put(FSecurity.MINSTEP, TQMessages.MIN_STEP);
		column_id_to_header.put(FSecurity.LOTSIZE, TQMessages.LOT_SIZE);
		column_id_to_header.put(FSecurity.POINT_COST, TQMessages.POINT_COST);
		column_id_to_header.put(FSecurity.OPMASK, TQMessages.OPMASK);
		column_id_to_header.put(FSecurity.SECTYPE, TQMessages.SEC_TYPE);
		column_id_to_header.put(FSecurity.SECTZ, TQMessages.SEC_TZ);
		column_id_to_header.put(FSecurity.QUOTESTYPE, TQMessages.QUOTES_TYPE);
		column_id_to_header.put(FSecurity.SECNAME, TQMessages.SEC_NAME);
		column_id_to_header.put(FSecurity.PNAME, TQMessages.PNAME);
		column_id_to_header.put(FSecurity.MAT_DATE, TQMessages.MAT_DATE);
		column_id_to_header.put(FSecurity.CLEARING_PRICE, TQMessages.CLEARING_PRICE);
		column_id_to_header.put(FSecurity.MINPRICE, TQMessages.MIN_PRICE);
		column_id_to_header.put(FSecurity.MAXPRICE, TQMessages.MAX_PRICE);
		column_id_to_header.put(FSecurity.BUY_DEPOSIT, TQMessages.BUY_DEPOSIT);
		column_id_to_header.put(FSecurity.SELL_DEPOSIT, TQMessages.SELL_DEPOSIT);
		column_id_to_header.put(FSecurity.BGO_C, TQMessages.BGO_C);
		column_id_to_header.put(FSecurity.BGO_NC, TQMessages.BGO_NC);
		column_id_to_header.put(FSecurity.ACCRUED_INT, TQMessages.ACCRUED_INT);
		column_id_to_header.put(FSecurity.COUPON_VALUE, TQMessages.COUPON_VALUE);
		column_id_to_header.put(FSecurity.COUPON_DATE, TQMessages.COUPON_DATE);
		column_id_to_header.put(FSecurity.COUPON_PERIOD, TQMessages.COUPON_PERIOD);
		column_id_to_header.put(FSecurity.FACE_VALUE, TQMessages.FACE_VALUE);
		column_id_to_header.put(FSecurity.PUT_CALL, TQMessages.PUT_CALL);
		column_id_to_header.put(FSecurity.OPT_TYPE, TQMessages.OPT_TYPE);
		column_id_to_header.put(FSecurity.LOT_VOLUME, TQMessages.LOT_VOLUME);
		column_id_to_header.put(FSecurity.BGO_BUY, TQMessages.BGO_BUY);
		return new OSCRepositoryTableModel<>(messages, repository, column_id_list, column_id_to_header);
	}
	
	public OSCRepositoryTableModel<SecurityBoardParams>
		createSecurityBoardParamsTableModel(OSCRepository<TSymbol, SecurityBoardParams> repository)
	{
		List<Integer> column_id_list = new ArrayList<>();
		column_id_list.add(FSecurityBoard.SECCODE);
		column_id_list.add(FSecurityBoard.BOARD);
		column_id_list.add(FSecurityBoard.MARKET);
		column_id_list.add(FSecurityBoard.DECIMALS);
		column_id_list.add(FSecurityBoard.MINSTEP);
		column_id_list.add(FSecurityBoard.LOTSIZE);
		column_id_list.add(FSecurityBoard.POINT_COST);
		Map<Integer, MsgID> column_id_to_header = new HashMap<>();
		column_id_to_header.put(FSecurityBoard.SECCODE, TQMessages.SEC_CODE);
		column_id_to_header.put(FSecurityBoard.BOARD, TQMessages.BOARD);
		column_id_to_header.put(FSecurityBoard.MARKET, TQMessages.MARKET_ID);
		column_id_to_header.put(FSecurityBoard.DECIMALS, TQMessages.DECIMALS);
		column_id_to_header.put(FSecurityBoard.MINSTEP, TQMessages.MIN_STEP);
		column_id_to_header.put(FSecurityBoard.LOTSIZE, TQMessages.LOT_SIZE);
		column_id_to_header.put(FSecurityBoard.POINT_COST, TQMessages.POINT_COST);
		return new OSCRepositoryTableModel<>(messages, repository, column_id_list, column_id_to_header);
	}
	
	public OSCRepositoryTableModel<SecurityQuotations>
		createSecurityQuotationsTableModel(OSCRepository<TSymbol, SecurityQuotations> repository)
	{
		List<Integer> column_id_list = new ArrayList<>();
		column_id_list.add(FQuotation.SECID);
		column_id_list.add(FQuotation.SECCODE);
		column_id_list.add(FQuotation.BOARD);
		column_id_list.add(FQuotation.POINT_COST);
		column_id_list.add(FQuotation.ACCRUED_INT_VALUE);
		column_id_list.add(FQuotation.OPEN);
		column_id_list.add(FQuotation.WA_PRICE);
		column_id_list.add(FQuotation.BID_DEPTH);
		column_id_list.add(FQuotation.BID_DEPTH_T);
		column_id_list.add(FQuotation.NUM_BIDS);
		column_id_list.add(FQuotation.OFFER_DEPTH);
		column_id_list.add(FQuotation.OFFER_DEPTH_T);
		column_id_list.add(FQuotation.BID);
		column_id_list.add(FQuotation.OFFER);
		column_id_list.add(FQuotation.NUM_OFFERS);
		column_id_list.add(FQuotation.NUM_TRADES);
		column_id_list.add(FQuotation.VOL_TODAY);
		column_id_list.add(FQuotation.OPEN_POSITIONS);
		column_id_list.add(FQuotation.DELTA_POSITIONS);
		column_id_list.add(FQuotation.LAST);
		column_id_list.add(FQuotation.QUANTITY);
		column_id_list.add(FQuotation.TIME);
		column_id_list.add(FQuotation.CHANGE);
		column_id_list.add(FQuotation.PRICE_MINUS_PREV_WA_PRICE);
		column_id_list.add(FQuotation.VAL_TODAY);
		column_id_list.add(FQuotation.YIELD);
		column_id_list.add(FQuotation.YIELD_AT_WA_PRICE);
		column_id_list.add(FQuotation.MARKET_PRICE_TODAY);
		column_id_list.add(FQuotation.HIGH_BID);
		column_id_list.add(FQuotation.LOW_OFFER);
		column_id_list.add(FQuotation.HIGH);
		column_id_list.add(FQuotation.LOW);
		column_id_list.add(FQuotation.CLOSE_PRICE);
		column_id_list.add(FQuotation.CLOSE_YIELD);
		column_id_list.add(FQuotation.STATUS);
		column_id_list.add(FQuotation.TRADING_STATUS);
		column_id_list.add(FQuotation.BUY_DEPOSIT);
		column_id_list.add(FQuotation.SELL_DEPOSIT);
		column_id_list.add(FQuotation.VOLATILITY);
		column_id_list.add(FQuotation.THEORETICAL_PRICE);
		column_id_list.add(FQuotation.BGO_BUY);
		column_id_list.add(FQuotation.L_CURRENT_PRICE);
		Map<Integer, MsgID> column_id_to_header = new HashMap<>();
		column_id_to_header.put(FQuotation.SECID, TQMessages.SEC_ID);
		column_id_to_header.put(FQuotation.SECCODE, TQMessages.SEC_CODE);
		column_id_to_header.put(FQuotation.BOARD, TQMessages.BOARD);
		column_id_to_header.put(FQuotation.POINT_COST, TQMessages.POINT_COST);
		column_id_to_header.put(FQuotation.ACCRUED_INT_VALUE, TQMessages.ACCRUED_INT);
		column_id_to_header.put(FQuotation.OPEN, SecurityMsg.OPEN_PRICE);
		column_id_to_header.put(FQuotation.WA_PRICE, TQMessages.WA_PRICE);
		column_id_to_header.put(FQuotation.BID_DEPTH, SecurityMsg.BID_SIZE);
		column_id_to_header.put(FQuotation.BID_DEPTH_T, TQMessages.BID_DEPTH_T);
		column_id_to_header.put(FQuotation.NUM_BIDS, TQMessages.NUM_BIDS);
		column_id_to_header.put(FQuotation.OFFER_DEPTH, SecurityMsg.ASK_SIZE);
		column_id_to_header.put(FQuotation.OFFER_DEPTH_T, TQMessages.OFFER_DEPTH_T);
		column_id_to_header.put(FQuotation.BID, SecurityMsg.BID_PRICE);
		column_id_to_header.put(FQuotation.OFFER, SecurityMsg.ASK_PRICE);
		column_id_to_header.put(FQuotation.NUM_OFFERS, TQMessages.NUM_OFFERS);
		column_id_to_header.put(FQuotation.NUM_TRADES, TQMessages.NUM_TRADES);
		column_id_to_header.put(FQuotation.VOL_TODAY, TQMessages.VOL_TODAY);
		column_id_to_header.put(FQuotation.OPEN_POSITIONS, TQMessages.OPEN_POSITIONS);
		column_id_to_header.put(FQuotation.DELTA_POSITIONS, TQMessages.DELTA_POSITIONS);
		column_id_to_header.put(FQuotation.LAST, SecurityMsg.LAST_PRICE);
		column_id_to_header.put(FQuotation.QUANTITY, SecurityMsg.LAST_SIZE);
		column_id_to_header.put(FQuotation.TIME, CommonMsg.TIME);
		column_id_to_header.put(FQuotation.CHANGE, TQMessages.CHANGE);
		column_id_to_header.put(FQuotation.PRICE_MINUS_PREV_WA_PRICE, TQMessages.PRICE_MINUS_PREV_WA_PRICE);
		column_id_to_header.put(FQuotation.VAL_TODAY, TQMessages.VAL_TODAY);
		column_id_to_header.put(FQuotation.YIELD, TQMessages.YIELD);
		column_id_to_header.put(FQuotation.YIELD_AT_WA_PRICE, TQMessages.YIELD_AT_WA_PRICE);
		column_id_to_header.put(FQuotation.MARKET_PRICE_TODAY, TQMessages.MARKET_PRICE_TODAY);
		column_id_to_header.put(FQuotation.HIGH_BID, TQMessages.HIGH_BID);
		column_id_to_header.put(FQuotation.LOW_OFFER, TQMessages.LOW_OFFER);
		column_id_to_header.put(FQuotation.HIGH, SecurityMsg.HIGH_PRICE);
		column_id_to_header.put(FQuotation.LOW, SecurityMsg.LOW_PRICE);
		column_id_to_header.put(FQuotation.CLOSE_PRICE, TQMessages.CLOSE_PRICE);
		column_id_to_header.put(FQuotation.CLOSE_YIELD, TQMessages.CLOSE_YIELD);
		column_id_to_header.put(FQuotation.STATUS, CommonMsg.STATUS);
		column_id_to_header.put(FQuotation.TRADING_STATUS, TQMessages.TRADING_STATUS);
		column_id_to_header.put(FQuotation.BUY_DEPOSIT, TQMessages.BUY_DEPOSIT);
		column_id_to_header.put(FQuotation.SELL_DEPOSIT, TQMessages.SELL_DEPOSIT);
		column_id_to_header.put(FQuotation.VOLATILITY, TQMessages.VOLATILITY);
		column_id_to_header.put(FQuotation.THEORETICAL_PRICE, TQMessages.THEORETICAL_PRICE);
		column_id_to_header.put(FQuotation.BGO_BUY, TQMessages.BGO_BUY);
		column_id_to_header.put(FQuotation.L_CURRENT_PRICE, CommonMsg.CURR_PR);
		return new OSCRepositoryTableModel<>(messages, repository, column_id_list, column_id_to_header);
	}
	
	public OSCRepositoryTableModel<Client>
		createClientTableModel(OSCRepository<String, Client> repository)
	{
		List<Integer> column_id_list = new ArrayList<>();
		column_id_list.add(FClient.ID);
		column_id_list.add(FClient.REMOVE);
		column_id_list.add(FClient.TYPE);
		column_id_list.add(FClient.CURRENCY);
		column_id_list.add(FClient.MARKET_ID);
		column_id_list.add(FClient.UNION_CODE);
		column_id_list.add(FClient.FORTS_ACCOUNT);
		Map<Integer, MsgID> column_id_to_header = new HashMap<>();
		column_id_to_header.put(FClient.ID, TQMessages.CLIENT_ID);
		column_id_to_header.put(FClient.REMOVE, TQMessages.CLIENT_REMOVE);
		column_id_to_header.put(FClient.TYPE, TQMessages.CLIENT_TYPE);
		column_id_to_header.put(FClient.CURRENCY, TQMessages.CLIENT_CURRENCY);
		column_id_to_header.put(FClient.MARKET_ID, TQMessages.MARKET_ID);
		column_id_to_header.put(FClient.UNION_CODE, TQMessages.CLIENT_UNION_CODE);
		column_id_to_header.put(FClient.FORTS_ACCOUNT, TQMessages.CLIENT_FORTS_ACCOUNT);
		return new OSCRepositoryTableModel<>(messages, repository, column_id_list, column_id_to_header);
	}
	
	public OSCRepositoryTableModel<MoneyPosition>
		createMoneyPositionTableModel(OSCRepository<ID.MP, MoneyPosition> repository)
	{
		List<Integer> column_id_list = new ArrayList<>();
		column_id_list.add(FMoneyPosition.CLIENT_ID);
		column_id_list.add(FMoneyPosition.ASSET);
		column_id_list.add(FMoneyPosition.REGISTER);
		column_id_list.add(FMoneyPosition.UNION_CODE);
		column_id_list.add(FMoneyPosition.MARKETS);
		column_id_list.add(FMoneyPosition.SHORT_NAME);
		column_id_list.add(FMoneyPosition.SALDO_IN);
		column_id_list.add(FMoneyPosition.BOUGHT);
		column_id_list.add(FMoneyPosition.SOLD);
		column_id_list.add(FMoneyPosition.SALDO);
		column_id_list.add(FMoneyPosition.ORD_BUY);
		column_id_list.add(FMoneyPosition.ORD_BUY_COND);
		column_id_list.add(FMoneyPosition.COMISSION);
		Map<Integer, MsgID> column_id_to_header = new HashMap<>();
		column_id_to_header.put(FMoneyPosition.CLIENT_ID, TQMessages.CLIENT_ID);
		column_id_to_header.put(FMoneyPosition.ASSET, TQMessages.ASSET);
		column_id_to_header.put(FMoneyPosition.REGISTER, TQMessages.REGISTER);
		column_id_to_header.put(FMoneyPosition.UNION_CODE, TQMessages.CLIENT_UNION_CODE);
		column_id_to_header.put(FMoneyPosition.MARKETS, TQMessages.MARKET_ID);
		column_id_to_header.put(FMoneyPosition.SHORT_NAME, TQMessages.SHORT_NAME);
		column_id_to_header.put(FMoneyPosition.SALDO_IN, TQMessages.SALDO_IN);
		column_id_to_header.put(FMoneyPosition.BOUGHT, TQMessages.BOUGHT);
		column_id_to_header.put(FMoneyPosition.SOLD, TQMessages.SOLD);
		column_id_to_header.put(FMoneyPosition.SALDO, TQMessages.SALDO);
		column_id_to_header.put(FMoneyPosition.ORD_BUY, TQMessages.ORD_BUY);
		column_id_to_header.put(FMoneyPosition.ORD_BUY_COND, TQMessages.ORD_BUY_COND);
		column_id_to_header.put(FMoneyPosition.COMISSION, TQMessages.COMISSION);
		return new OSCRepositoryTableModel<>(messages, repository, column_id_list, column_id_to_header);
	}
	
	public OSCRepositoryTableModel<SecPosition>
		createSecPositionTableModel(OSCRepository<ID.SP, SecPosition> repository)
	{
		List<Integer> column_id_list = new ArrayList<>();
		column_id_list.add(FSecPosition.SEC_ID);
		column_id_list.add(FSecPosition.MARKET_ID);
		column_id_list.add(FSecPosition.SEC_CODE);
		column_id_list.add(FSecPosition.REGISTER);
		column_id_list.add(FSecPosition.CLIENT_ID);
		column_id_list.add(FSecPosition.UNION_CODE);
		column_id_list.add(FSecPosition.SHORT_NAME);
		column_id_list.add(FSecPosition.SALDO_IN);
		column_id_list.add(FSecPosition.SALDO_MIN);
		column_id_list.add(FSecPosition.BOUGHT);
		column_id_list.add(FSecPosition.SOLD);
		column_id_list.add(FSecPosition.SALDO);
		column_id_list.add(FSecPosition.ORD_BUY);
		column_id_list.add(FSecPosition.ORD_SELL);
		column_id_list.add(FSecPosition.AMOUNT);
		column_id_list.add(FSecPosition.EQUITY);
		Map<Integer, MsgID> column_id_to_header = new HashMap<>();
		column_id_to_header.put(FSecPosition.SEC_ID, TQMessages.SEC_ID);
		column_id_to_header.put(FSecPosition.MARKET_ID, TQMessages.MARKET_ID);
		column_id_to_header.put(FSecPosition.SEC_CODE, TQMessages.SEC_CODE);
		column_id_to_header.put(FSecPosition.REGISTER, TQMessages.REGISTER);
		column_id_to_header.put(FSecPosition.CLIENT_ID, TQMessages.CLIENT_ID);
		column_id_to_header.put(FSecPosition.UNION_CODE, TQMessages.CLIENT_UNION_CODE);
		column_id_to_header.put(FSecPosition.SHORT_NAME, TQMessages.SHORT_NAME);
		column_id_to_header.put(FSecPosition.SALDO_IN, TQMessages.SALDO_IN);
		column_id_to_header.put(FSecPosition.SALDO_MIN, TQMessages.SALDO_MIN);
		column_id_to_header.put(FSecPosition.BOUGHT, TQMessages.BOUGHT);
		column_id_to_header.put(FSecPosition.SOLD, TQMessages.SOLD);
		column_id_to_header.put(FSecPosition.SALDO, TQMessages.SALDO);
		column_id_to_header.put(FSecPosition.ORD_BUY, TQMessages.ORD_BUY);
		column_id_to_header.put(FSecPosition.ORD_SELL, TQMessages.ORD_SELL);
		column_id_to_header.put(FSecPosition.AMOUNT, TQMessages.AMOUNT);
		column_id_to_header.put(FSecPosition.EQUITY, CommonMsg.EQUITY);
		return new OSCRepositoryTableModel<>(messages, repository, column_id_list, column_id_to_header);
	}
	
	public OSCRepositoryTableModel<FortsMoney>
		createFortsMoneyTableModel(OSCRepository<ID.FM, FortsMoney> repository)
	{
		List<Integer> column_id_list = new ArrayList<>();
		column_id_list.add(FFortsMoney.CLIENT_ID);
		column_id_list.add(FFortsMoney.UNION_CODE);
		column_id_list.add(FFortsMoney.MARKETS);
		column_id_list.add(FFortsMoney.SHORT_NAME);
		column_id_list.add(FFortsMoney.CURRENT);
		column_id_list.add(FFortsMoney.BLOCKED);
		column_id_list.add(FFortsMoney.FREE);
		column_id_list.add(FFortsMoney.VAR_MARGIN);
		Map<Integer, MsgID> column_id_to_header = new HashMap<>();
		column_id_to_header.put(FFortsMoney.CLIENT_ID, TQMessages.CLIENT_ID);
		column_id_to_header.put(FFortsMoney.UNION_CODE, TQMessages.CLIENT_UNION_CODE);
		column_id_to_header.put(FFortsMoney.MARKETS, TQMessages.MARKET_ID);
		column_id_to_header.put(FFortsMoney.SHORT_NAME, TQMessages.SHORT_NAME);
		column_id_to_header.put(FFortsMoney.CURRENT, TQMessages.FORTS_MONEY_CURRENT);
		column_id_to_header.put(FFortsMoney.BLOCKED, TQMessages.FORTS_MONEY_BLOCKED);
		column_id_to_header.put(FFortsMoney.FREE, TQMessages.FORTS_MONEY_FREE);
		column_id_to_header.put(FFortsMoney.VAR_MARGIN, CommonMsg.VMARGIN);
		return new OSCRepositoryTableModel<>(messages, repository, column_id_list, column_id_to_header);
	}

	public OSCRepositoryTableModel<FortsPosition>
		createFortsPositionTableModel(OSCRepository<ID.FP, FortsPosition> repository)
	{
		List<Integer> column_id_list = new ArrayList<>();
		column_id_list.add(FFortsPosition.SEC_ID);
		column_id_list.add(FFortsPosition.MARKETS);
		column_id_list.add(FFortsPosition.SEC_CODE);
		column_id_list.add(FFortsPosition.CLIENT_ID);
		column_id_list.add(FFortsPosition.UNION_CODE);
		column_id_list.add(FFortsPosition.START_NET);
		column_id_list.add(FFortsPosition.OPEN_BUYS);
		column_id_list.add(FFortsPosition.OPEN_SELLS);
		column_id_list.add(FFortsPosition.TOTAL_NET);
		column_id_list.add(FFortsPosition.TODAY_BUY);
		column_id_list.add(FFortsPosition.TODAY_SELL);
		column_id_list.add(FFortsPosition.OPT_MARGIN);
		column_id_list.add(FFortsPosition.VAR_MARGIN);
		column_id_list.add(FFortsPosition.EXPIRATION_POS);
		column_id_list.add(FFortsPosition.USED_SELL_SPOT_LIMIT);
		column_id_list.add(FFortsPosition.SELL_SPOT_LIMIT);
		column_id_list.add(FFortsPosition.NETTO);
		column_id_list.add(FFortsPosition.KGO);
		Map<Integer, MsgID> column_id_to_header = new HashMap<>();
		column_id_to_header.put(FFortsPosition.SEC_ID, TQMessages.SEC_ID);
		column_id_to_header.put(FFortsPosition.MARKETS, TQMessages.MARKET_ID);
		column_id_to_header.put(FFortsPosition.SEC_CODE, TQMessages.SEC_CODE);
		column_id_to_header.put(FFortsPosition.CLIENT_ID, TQMessages.CLIENT_ID);
		column_id_to_header.put(FFortsPosition.UNION_CODE, TQMessages.CLIENT_UNION_CODE);
		column_id_to_header.put(FFortsPosition.START_NET, TQMessages.FP_START_NET);
		column_id_to_header.put(FFortsPosition.OPEN_BUYS, TQMessages.FP_OPEN_BUYS);
		column_id_to_header.put(FFortsPosition.OPEN_SELLS, TQMessages.FP_OPEN_SELLS);
		column_id_to_header.put(FFortsPosition.TOTAL_NET, TQMessages.FP_TOTAL_NET);
		column_id_to_header.put(FFortsPosition.TODAY_BUY, TQMessages.FP_TODAY_BUY);
		column_id_to_header.put(FFortsPosition.TODAY_SELL, TQMessages.FP_TODAY_SELL);
		column_id_to_header.put(FFortsPosition.OPT_MARGIN, TQMessages.FP_OPT_MARGIN);
		column_id_to_header.put(FFortsPosition.VAR_MARGIN, CommonMsg.VMARGIN);
		column_id_to_header.put(FFortsPosition.EXPIRATION_POS, TQMessages.FP_EXPIRATION_POS);
		column_id_to_header.put(FFortsPosition.USED_SELL_SPOT_LIMIT, TQMessages.FP_USED_SELL_SPOT_LIMIT);
		column_id_to_header.put(FFortsPosition.SELL_SPOT_LIMIT, TQMessages.FP_SELL_SPOT_LIMIT);
		column_id_to_header.put(FFortsPosition.NETTO, TQMessages.FP_NETTO);
		column_id_to_header.put(FFortsPosition.KGO, TQMessages.FP_KGO);
		return new OSCRepositoryTableModel<>(messages, repository, column_id_list, column_id_to_header);
	}
	
	public OSCRepositoryTableModel<FortsCollaterals>
		createFortsCollateralsTableModel(OSCRepository<ID.FC, FortsCollaterals> repository)
	{
		List<Integer> column_id_list = new ArrayList<>();
		column_id_list.add(FFortsCollaterals.CLIENT_ID);
		column_id_list.add(FFortsCollaterals.UNION_CODE);
		column_id_list.add(FFortsCollaterals.MARKETS);
		column_id_list.add(FFortsCollaterals.SHORT_NAME);
		column_id_list.add(FFortsCollaterals.CURRENT);
		column_id_list.add(FFortsCollaterals.BLOCKED);
		column_id_list.add(FFortsCollaterals.FREE);
		Map<Integer, MsgID> column_id_to_header = new HashMap<>();
		column_id_to_header.put(FFortsCollaterals.CLIENT_ID, TQMessages.CLIENT_ID);
		column_id_to_header.put(FFortsCollaterals.UNION_CODE, TQMessages.CLIENT_UNION_CODE);
		column_id_to_header.put(FFortsCollaterals.MARKETS, TQMessages.MARKET_ID);
		column_id_to_header.put(FFortsCollaterals.SHORT_NAME, TQMessages.SHORT_NAME);
		column_id_to_header.put(FFortsCollaterals.CURRENT, TQMessages.FC_CURRENT);
		column_id_to_header.put(FFortsCollaterals.BLOCKED, TQMessages.FC_BLOCKED);
		column_id_to_header.put(FFortsCollaterals.FREE, TQMessages.FC_FREE);
		return new OSCRepositoryTableModel<>(messages, repository, column_id_list, column_id_to_header);
	}
	
	public OSCRepositoryTableModel<SpotLimits>
		createSpotLimitsTableModel(OSCRepository<ID.SL, SpotLimits> repository)
	{
		List<Integer> column_id_list = new ArrayList<>();
		column_id_list.add(FSpotLimits.CLIENT_ID);
		column_id_list.add(FSpotLimits.UNION_CODE);
		column_id_list.add(FSpotLimits.MARKETS);
		column_id_list.add(FSpotLimits.SHORT_NAME);
		column_id_list.add(FSpotLimits.BUY_LIMIT);
		column_id_list.add(FSpotLimits.BUY_LIMIT_USED);
		Map<Integer, MsgID> column_id_to_header = new HashMap<>();
		column_id_to_header.put(FSpotLimits.CLIENT_ID, TQMessages.CLIENT_ID);
		column_id_to_header.put(FSpotLimits.UNION_CODE, TQMessages.CLIENT_UNION_CODE);
		column_id_to_header.put(FSpotLimits.MARKETS, TQMessages.MARKET_ID);
		column_id_to_header.put(FSpotLimits.SHORT_NAME, TQMessages.SHORT_NAME);
		column_id_to_header.put(FSpotLimits.BUY_LIMIT, TQMessages.SL_BUY_LIMIT);
		column_id_to_header.put(FSpotLimits.BUY_LIMIT_USED, TQMessages.SL_BUY_LIMIT_USED);
		return new OSCRepositoryTableModel<>(messages, repository, column_id_list, column_id_to_header);
	}
	
	public OSCRepositoryTableModel<UnitedLimits>
		createUnitedLimitsTableModel(OSCRepository<ID.UL, UnitedLimits> repository)
	{
		List<Integer> column_id_list = new ArrayList<>();
		column_id_list.add(FUnitedLimits.UNION_CODE);
		column_id_list.add(FUnitedLimits.OPEN_EQUITY);
		column_id_list.add(FUnitedLimits.EQUITY);
		column_id_list.add(FUnitedLimits.REQUIREMENTS);
		column_id_list.add(FUnitedLimits.FREE);
		column_id_list.add(FUnitedLimits.VAR_MARGIN);
		column_id_list.add(FUnitedLimits.FIN_RES);
		column_id_list.add(FUnitedLimits.GO);
		Map<Integer, MsgID> column_id_to_header = new HashMap<>();
		column_id_to_header.put(FUnitedLimits.UNION_CODE, TQMessages.CLIENT_UNION_CODE);
		column_id_to_header.put(FUnitedLimits.OPEN_EQUITY, TQMessages.UL_OPEN_EQUITY);
		column_id_to_header.put(FUnitedLimits.EQUITY, CommonMsg.EQUITY);
		column_id_to_header.put(FUnitedLimits.REQUIREMENTS, TQMessages.UL_REQUIREMENTS);
		column_id_to_header.put(FUnitedLimits.FREE, TQMessages.UL_FREE);
		column_id_to_header.put(FUnitedLimits.VAR_MARGIN, CommonMsg.VMARGIN);
		column_id_to_header.put(FUnitedLimits.FIN_RES, TQMessages.UL_FIN_RES);
		column_id_to_header.put(FUnitedLimits.GO, TQMessages.UL_GO);
		return new OSCRepositoryTableModel<>(messages, repository, column_id_list, column_id_to_header);
	}

	private JTable createTable(ITableModel table_model) {
		JTable table = new JTable(table_model);
		table.setShowGrid(true);
		table.setRowSorter(new TableRowSorter<>(table_model));
		return table;	
	}
	
	public JTable createMarketTable(OSCRepository<Integer, Market> repository) {
		return createTable(createMarketTableModel(repository));
	}
	
	public JTable createBoardTable(OSCRepository<String, Board> repository) {
		return createTable(createBoardTableModel(repository));
	}
	
	public JTable createCKindTable(OSCRepository<Integer, CKind> repository) {
		return createTable(createCKindTableModel(repository));
	}
	
	public JTable createSecParamsTable(OSCRepository<GSymbol, SecurityParams> repository) {
		return createTable(createSecurityParamsTableModel(repository));
	}
	
	public JTable createSecBrdParamsTable(OSCRepository<TSymbol, SecurityBoardParams> repository) {
		return createTable(createSecurityBoardParamsTableModel(repository));
	}
	
	public JTable createSecQuotationsTable(OSCRepository<TSymbol, SecurityQuotations> repository) {
		return createTable(createSecurityQuotationsTableModel(repository));
	}
	
	public JTable createClientsTable(OSCRepository<String, Client> repository) {
		return createTable(createClientTableModel(repository));
	}
	
	public JTable createMoneyPositionsTable(OSCRepository<ID.MP, MoneyPosition> repository) {
		return createTable(createMoneyPositionTableModel(repository));
	}
	
	public JTable createSecPositionsTable(OSCRepository<ID.SP, SecPosition> repository) {
		return createTable(createSecPositionTableModel(repository));
	}
	
	public JTable createFortsMoneyTable(OSCRepository<ID.FM, FortsMoney> repository) {
		return createTable(createFortsMoneyTableModel(repository));
	}
	
	public JTable createFortsPositionsTable(OSCRepository<ID.FP, FortsPosition> repository) {
		return createTable(createFortsPositionTableModel(repository));
	}
	
	public JTable createFortsCollateralsTable(OSCRepository<ID.FC, FortsCollaterals> repository) {
		return createTable(createFortsCollateralsTableModel(repository));
	}
	
	public JTable createSpotLimitsTable(OSCRepository<ID.SL, SpotLimits> repository) {
		return createTable(createSpotLimitsTableModel(repository));
	}
	
	public JTable createUnitedLimitsTable(OSCRepository<ID.UL, UnitedLimits> repository) {
		return createTable(createUnitedLimitsTableModel(repository));
	}
	
	private JDialog createTableDialog(JTable table, MsgID title_msg_id, Dimension initial_size, boolean auto_resize_all) {
		JDialog dialog = new JDialog(frame);
		JPanel panel = new JPanel(new BorderLayout());
		JScrollPane table_scroll_panel = new JScrollPane(table,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
			);
		table.setAutoResizeMode(auto_resize_all ? JTable.AUTO_RESIZE_ALL_COLUMNS : JTable.AUTO_RESIZE_OFF);
		panel.add(table_scroll_panel, BorderLayout.CENTER);
		dialog.getContentPane().add(panel);
		dialog.setTitle(messages.get(title_msg_id));
		dialog.setPreferredSize(initial_size);
		new TableModelController((ITableModel) table.getModel(), dialog);
		dialog.pack();
		return dialog;		
	}
	
	private JDialog createTableDialog(JTable table, MsgID title_msg_id, Dimension initial_size) {
		return createTableDialog(table, title_msg_id, initial_size, false);
	}
	
	public JDialog createMarketsDialog() {
		return createTableDialog(
				createMarketTable(directory.getMarketRepository()),
				TQMessages.DIALOG_TITLE_MARKETS,
				new Dimension(300, 300),
				true
			);
	}
	
	public JDialog createBoardsDialog() {
		return createTableDialog(
				createBoardTable(directory.getBoardRepository()),
				TQMessages.DIALOG_TITLE_BOARDS,
				new Dimension(600, 500), 
				true
			);
	}
	
	public JDialog createCKindsDialog() {
		return createTableDialog(
				createCKindTable(directory.getCKindRepository()),
				TQMessages.DIALOG_TITLE_CKINDS,
				new Dimension(300, 400),
				true
			);
	}
	
	public JDialog createSecParamsDialog() {
		return createTableDialog(
				createSecParamsTable(directory.getSecurityParamsRepository()),
				TQMessages.DIALOG_TITLE_SEC_PARAMS,
				new Dimension(1000, 400)
			);
	}
	
	public JDialog createSecBrdParamsDialog() {
		return createTableDialog(
				createSecBrdParamsTable(directory.getSecurityBoardParamsRepository()),
				TQMessages.DIALOG_TITLE_SEC_BRD_PARAMS,
				new Dimension(600, 400),
				true
			);
	}
	
	public JDialog createSecQuotationsDialog() {
		return createTableDialog(
				createSecQuotationsTable(directory.getSecurityQuotationsRepository()),
				TQMessages.DIALOG_TITLE_SEC_QUOTATIONS,
				new Dimension(1000, 400)
			);
	}
	
	public JDialog createClientsDialog() {
		return createTableDialog(
				createClientsTable(directory.getClientRepository()),
				TQMessages.DIALOG_TITLE_CLIENTS,
				new Dimension(800, 300),
				true
			);
	}
	
	public JDialog createMoneyPositionsDialog() {
		return createTableDialog(
				createMoneyPositionsTable(directory.getMoneyPositionRepository()),
				TQMessages.DIALOG_TITLE_POS_MONEY_POSITIONS,
				new Dimension(700, 300),
				true
			);
	}
	
	public JDialog createSecPositionsDialog() {
		return createTableDialog(
				createSecPositionsTable(directory.getSecPositionRepository()),
				TQMessages.DIALOG_TITLE_POS_SEC_POSITIONS,
				new Dimension(700, 300),
				true
			);
	}
	
	public JDialog createFortsMoneyDialog() {
		return createTableDialog(
				createFortsMoneyTable(directory.getFortsMoneyRepository()),
				TQMessages.DIALOG_TITLE_POS_FORTS_MONEY,
				new Dimension(700, 300),
				true
			);
	}
	
	public JDialog createFortsPositionsDialog() {
		return createTableDialog(
				createFortsPositionsTable(directory.getFortsPositionRepository()),
				TQMessages.DIALOG_TITLE_POS_FORTS_POSITIONS,
				new Dimension(700, 300),
				true
			);
	}
	
	public JDialog createFortsCollateralsDialog() {
		return createTableDialog(
				createFortsCollateralsTable(directory.getFortsCollateralsRepository()),
				TQMessages.DIALOG_TITLE_POS_FORTS_COLLATERALS,
				new Dimension(700, 300),
				true
			);
	}
	
	public JDialog createSpotLimitsDialog() {
		return createTableDialog(
				createSpotLimitsTable(directory.getSpotLimitsRepository()),
				TQMessages.DIALOG_TITLE_POS_SPOT_LIMITS,
				new Dimension(600, 300),
				true
			);
	}
	
	public JDialog createUnitedLimitsDialog() {
		return createTableDialog(
				createUnitedLimitsTable(directory.getUnitedLimitsRepository()),
				TQMessages.DIALOG_TITLE_POS_UNITED_LIMITS,
				new Dimension(600, 300),
				true
			);
	}
	
	private JMenuItem addMenuItem(JMenu menu, MsgID msg_id, String action_command, DialogFactory factory) {
		JMenuItem item = null;
		menu.add(item = new JMenuItem(messages.get(msg_id)));
		item.setActionCommand(action_command);
		item.addActionListener(this);
		actionCmdToDialogMap.put(action_command, new LazyDialogInitializer(factory));
		return item;
	}
	
	public JMenu create() {
		JMenu menu = new JMenu(messages.get(TQMessages.SERVICE_MENU));
		addMenuItem(menu, TQMessages.SHOW_CKINDS, ITEM_SHOW_CKINDS, this::createCKindsDialog);
		addMenuItem(menu, TQMessages.SHOW_MARKETS, ITEM_SHOW_MARKETS, this::createMarketsDialog);
		addMenuItem(menu, TQMessages.SHOW_BOARDS, ITEM_SHOW_BOARDS, this::createBoardsDialog);
		addMenuItem(menu, TQMessages.SHOW_SEC_PARAMS, ITEM_SHOW_SEC_PARAMS, this::createSecParamsDialog);
		addMenuItem(menu, TQMessages.SHOW_SEC_BRD_PARAMS, ITEM_SHOW_SEC_BRD_PARAMS, this::createSecBrdParamsDialog);
		addMenuItem(menu, TQMessages.SHOW_SEC_QUOTATIONS, ITEM_SHOW_SEC_QUOTATIONS, this::createSecQuotationsDialog);
		addMenuItem(menu, TQMessages.SHOW_CLIENTS, ITEM_SHOW_CLIENTS, this::createClientsDialog);
		addMenuItem(menu, TQMessages.SHOW_POS_MONEY_POSITIONS, ITEM_SHOW_POS_MONEY_POSITIONS, this::createMoneyPositionsDialog);
		addMenuItem(menu, TQMessages.SHOW_POS_SEC_POSITIONS, ITEM_SHOW_POS_SEC_POSITIONS, this::createSecPositionsDialog);
		addMenuItem(menu, TQMessages.SHOW_POS_FORTS_MONEY, ITEM_SHOW_POS_FORTS_MONEY, this::createFortsMoneyDialog);
		addMenuItem(menu, TQMessages.SHOW_POS_FORTS_POSITIONS, ITEM_SHOW_POS_FORTS_POSITIONS, this::createFortsPositionsDialog);
		addMenuItem(menu, TQMessages.SHOW_POS_FORTS_COLLATERALS, ITEM_SHOW_POS_FORTS_COLLATERALS, this::createFortsCollateralsDialog);
		addMenuItem(menu, TQMessages.SHOW_POS_SPOT_LIMITS, ITEM_SHOW_POS_SPOT_LIMITS, this::createSpotLimitsDialog);
		addMenuItem(menu, TQMessages.SHOW_POS_UNITED_LIMITS, ITEM_SHOW_POS_UNITED_LIMITS, this::createUnitedLimitsDialog);
		return menu;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		DialogFactory factory = actionCmdToDialogMap.get(e.getActionCommand());
		if ( factory != null ) {
			factory.produce().setVisible(true);
		}
	}
	
	void infoBox(String message, String title) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	void infoBox(String message) {
		infoBox(message, "Information");
	}

}

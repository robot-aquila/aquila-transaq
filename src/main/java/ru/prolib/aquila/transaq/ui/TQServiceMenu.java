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
import ru.prolib.aquila.transaq.remote.MessageFields.FBoard;
import ru.prolib.aquila.transaq.remote.MessageFields.FCKind;
import ru.prolib.aquila.transaq.remote.MessageFields.FClient;
import ru.prolib.aquila.transaq.remote.MessageFields.FMarket;
import ru.prolib.aquila.transaq.remote.MessageFields.FQuotation;
import ru.prolib.aquila.transaq.remote.MessageFields.FSecurity;
import ru.prolib.aquila.transaq.remote.MessageFields.FSecurityBoard;
import ru.prolib.aquila.transaq.remote.entity.Client;
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
	
	private final IMessages messages;
	private final JFrame frame;
	private final TQDirectory directory;
	private JDialog marketsDialog, boardsDialog, ckindsDialog, secParamsDialog, secBrdParamsDialog,
		secQuotationsDialog, clientsDialog;
	
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
	
	private JDialog createTableDialog(JTable table, MsgID title_msg_id, Dimension initial_size) {
		JDialog dialog = new JDialog(frame);
		JPanel panel = new JPanel(new BorderLayout());
		JScrollPane table_scroll_panel = new JScrollPane(table,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
			);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		panel.add(table_scroll_panel, BorderLayout.CENTER);
		dialog.getContentPane().add(panel);
		dialog.setTitle(messages.get(title_msg_id));
		dialog.setPreferredSize(initial_size);
		new TableModelController((ITableModel) table.getModel(), dialog);
		dialog.pack();
		return dialog;		
	}
	
	public JDialog createMarketsDialog(OSCRepository<Integer, Market> repository) {
		return createTableDialog(createMarketTable(repository), TQMessages.DIALOG_TITLE_MARKETS, new Dimension(200, 300));
	}
	
	public JDialog createBoardsDialog(OSCRepository<String, Board> repository) {
		return createTableDialog(createBoardTable(repository), TQMessages.DIALOG_TITLE_BOARDS, new Dimension(350, 500));
	}
	
	public JDialog createCKindsDialog(OSCRepository<Integer, CKind> repository) {
		return createTableDialog(createCKindTable(repository), TQMessages.DIALOG_TITLE_CKINDS, new Dimension(200, 400));
	}
	
	public JDialog createSecParamsDialog(OSCRepository<GSymbol, SecurityParams> repository) {
		return createTableDialog(
				createSecParamsTable(repository),
				TQMessages.DIALOG_TITLE_SEC_PARAMS,
				new Dimension(1000, 400)
			);
	}
	
	public JDialog createSecBrdParamsDialog(OSCRepository<TSymbol, SecurityBoardParams> repository) {
		return createTableDialog(
				createSecBrdParamsTable(repository),
				TQMessages.DIALOG_TITLE_SEC_BRD_PARAMS,
				new Dimension(600, 400)
			);
	}
	
	public JDialog createSecQuotationsDialog(OSCRepository<TSymbol, SecurityQuotations> repository) {
		return createTableDialog(
				createSecQuotationsTable(repository),
				TQMessages.DIALOG_TITLE_SEC_QUOTATIONS,
				new Dimension(1000, 400)
			);
	}
	
	public JDialog createClientsDialog(OSCRepository<String, Client> repository) {
		return createTableDialog(
				createClientsTable(repository),
				TQMessages.DIALOG_TITLE_CLIENTS,
				new Dimension(800, 160)
			);
	}
	
	public JMenu create() {
		JMenuItem item;
		JMenu menu = new JMenu(messages.get(TQMessages.SERVICE_MENU));
		
		menu.add(item = new JMenuItem(messages.get(TQMessages.SHOW_CKINDS)));
		item.setActionCommand(ITEM_SHOW_CKINDS);
		item.addActionListener(this);
		
		menu.add(item = new JMenuItem(messages.get(TQMessages.SHOW_MARKETS)));
		item.setActionCommand(ITEM_SHOW_MARKETS);
		item.addActionListener(this);
		
		menu.add(item = new JMenuItem(messages.get(TQMessages.SHOW_BOARDS)));
		item.setActionCommand(ITEM_SHOW_BOARDS);
		item.addActionListener(this);
		
		menu.add(item = new JMenuItem(messages.get(TQMessages.SHOW_SEC_PARAMS)));
		item.setActionCommand(ITEM_SHOW_SEC_PARAMS);
		item.addActionListener(this);
		
		menu.add(item = new JMenuItem(messages.get(TQMessages.SHOW_SEC_BRD_PARAMS)));
		item.setActionCommand(ITEM_SHOW_SEC_BRD_PARAMS);
		item.addActionListener(this);
		
		menu.add(item = new JMenuItem(messages.get(TQMessages.SHOW_SEC_QUOTATIONS)));
		item.setActionCommand(ITEM_SHOW_SEC_QUOTATIONS);
		item.addActionListener(this);
		
		menu.add(item = new JMenuItem(messages.get(TQMessages.SHOW_CLIENTS)));
		item.setActionCommand(ITEM_SHOW_CLIENTS);
		item.addActionListener(this);
		
		return menu;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch ( e.getActionCommand() ) {
		case ITEM_SHOW_MARKETS:
			if ( marketsDialog == null ) {
				marketsDialog = createMarketsDialog(directory.getMarketRepository());
			}
			marketsDialog.setVisible(true);
			break;
		case ITEM_SHOW_BOARDS:
			if ( boardsDialog == null ) {
				boardsDialog = createBoardsDialog(directory.getBoardRepository());
			}
			boardsDialog.setVisible(true);
			break;
		case ITEM_SHOW_CKINDS:
			if ( ckindsDialog == null ) {
				ckindsDialog = createCKindsDialog(directory.getCKindRepository());
			}
			ckindsDialog.setVisible(true);
			break;
		case ITEM_SHOW_SEC_PARAMS:
			if ( secParamsDialog == null ) {
				secParamsDialog = createSecParamsDialog(directory.getSecurityParamsRepository());
			}
			secParamsDialog.setVisible(true);
			break;
		case ITEM_SHOW_SEC_BRD_PARAMS:
			if ( secBrdParamsDialog == null ) {
				secBrdParamsDialog = createSecBrdParamsDialog(directory.getSecurityBoardParamsRepository());
			}
			secBrdParamsDialog.setVisible(true);
			break;
		case ITEM_SHOW_SEC_QUOTATIONS:
			if ( secQuotationsDialog == null ) {
				secQuotationsDialog = createSecQuotationsDialog(directory.getSecurityQuotationsRepository());
			}
			secQuotationsDialog.setVisible(true);
			break;
		case ITEM_SHOW_CLIENTS:
			if ( clientsDialog == null ) {
				clientsDialog = createClientsDialog(directory.getClientRepository());
			}
			clientsDialog.setVisible(true);
			break;
		}
	}
	
	void infoBox(String message, String title) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	void infoBox(String message) {
		infoBox(message, "Information");
	}


}

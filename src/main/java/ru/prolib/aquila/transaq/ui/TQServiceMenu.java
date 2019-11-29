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
import ru.prolib.aquila.transaq.engine.sds.SymbolGID;
import ru.prolib.aquila.transaq.engine.sds.SymbolTID;
import ru.prolib.aquila.transaq.entity.Board;
import ru.prolib.aquila.transaq.entity.CKind;
import ru.prolib.aquila.transaq.entity.Market;
import ru.prolib.aquila.transaq.entity.SecurityBoardParams;
import ru.prolib.aquila.transaq.entity.SecurityParams;
import ru.prolib.aquila.transaq.impl.TQDirectory;
import ru.prolib.aquila.transaq.impl.TQField.FBoard;
import ru.prolib.aquila.transaq.impl.TQField.FCKind;
import ru.prolib.aquila.transaq.impl.TQField.FMarket;
import ru.prolib.aquila.transaq.impl.TQField.FSecurity;
import ru.prolib.aquila.transaq.impl.TQField.FSecurityBoard;
import ru.prolib.aquila.ui.ITableModel;
import ru.prolib.aquila.ui.TableModelController;
import ru.prolib.aquila.ui.form.OSCRepositoryTableModel;
import ru.prolib.aquila.ui.msg.CommonMsg;

public class TQServiceMenu implements ActionListener {
	public static final String ITEM_SHOW_MARKETS = "SHOW_MARKETS";
	public static final String ITEM_SHOW_BOARDS = "SHOW_BOARDS";
	public static final String ITEM_SHOW_CKINDS = "SHOW_CKINDS";
	public static final String ITEM_SHOW_SEC_PARAMS = "SHOW_SEC_PARAMS";
	public static final String ITEM_SHOW_SEC_BRD_PARAMS = "SHOW_SEC_BRD_PARAMS";
	
	private final IMessages messages;
	private final JFrame frame;
	private final TQDirectory directory;
	private JDialog marketsDialog, boardsDialog, ckindsDialog, secParamsDialog, secBrdParamsDialog;
	
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
		createSecurityParamsTableModel(OSCRepository<SymbolGID, SecurityParams> repository)
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
		createSecurityBoardParamsTableModel(OSCRepository<SymbolTID, SecurityBoardParams> repository)
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
	
	public JTable createSecParamsTable(OSCRepository<SymbolGID, SecurityParams> repository) {
		return createTable(createSecurityParamsTableModel(repository));
	}
	
	public JTable createSecBrdParamsTable(OSCRepository<SymbolTID, SecurityBoardParams> repository) {
		return createTable(createSecurityBoardParamsTableModel(repository));
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
	
	public JDialog createSecParamsDialog(OSCRepository<SymbolGID, SecurityParams> repository) {
		return createTableDialog(
				createSecParamsTable(repository),
				TQMessages.DIALOG_TITLE_SEC_PARAMS,
				new Dimension(1000, 400)
			);
	}
	
	public JDialog createSecBrdParamsDialog(OSCRepository<SymbolTID, SecurityBoardParams> repository) {
		return createTableDialog(
				createSecBrdParamsTable(repository),
				TQMessages.DIALOG_TITLE_SEC_BRD_PARAMS,
				new Dimension(600, 400)
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
		}
	}
	
	void infoBox(String message, String title) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	void infoBox(String message) {
		infoBox(message, "Information");
	}


}

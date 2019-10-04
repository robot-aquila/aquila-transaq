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
import ru.prolib.aquila.transaq.entity.Board;
import ru.prolib.aquila.transaq.entity.CKind;
import ru.prolib.aquila.transaq.entity.Market;
import ru.prolib.aquila.transaq.impl.TQBoardField;
import ru.prolib.aquila.transaq.impl.TQCKindField;
import ru.prolib.aquila.transaq.impl.TQDirectory;
import ru.prolib.aquila.transaq.impl.TQMarketField;
import ru.prolib.aquila.ui.ITableModel;
import ru.prolib.aquila.ui.TableModelController;
import ru.prolib.aquila.ui.form.OSCRepositoryTableModel;
import ru.prolib.aquila.ui.msg.CommonMsg;

public class TQServiceMenu implements ActionListener {
	public static final String ITEM_SHOW_MARKETS = "SHOW_MARKETS";
	public static final String ITEM_SHOW_BOARDS = "SHOW_BOARDS";
	public static final String ITEM_SHOW_CKINDS = "SHOW_CKINDS";
	
	private final IMessages messages;
	private final JFrame frame;
	private final TQDirectory directory;
	private JDialog marketsDialog, boardsDialog, ckindsDialog;
	
	public TQServiceMenu(IMessages messages, JFrame frame, TQDirectory directory) {
		this.messages = messages;
		this.frame = frame;
		this.directory = directory;
	}
	
	public OSCRepositoryTableModel<Market> createMarketTableModel(OSCRepository<Integer, Market> repository) {
		List<Integer> column_id_list = new ArrayList<>();
		column_id_list.add(TQMarketField.ID);
		column_id_list.add(TQMarketField.NAME);
		Map<Integer, MsgID> column_id_to_header = new HashMap<>();
		column_id_to_header.put(TQMarketField.ID, CommonMsg.ID);
		column_id_to_header.put(TQMarketField.NAME, CommonMsg.NAME);
		return new OSCRepositoryTableModel<>(messages, repository, column_id_list, column_id_to_header);
	}
	
	public OSCRepositoryTableModel<Board> createBoardTableModel(OSCRepository<String, Board> repository) {
		List<Integer> column_id_list = new ArrayList<>();
		column_id_list.add(TQBoardField.CODE);
		column_id_list.add(TQBoardField.MARKET_ID);
		column_id_list.add(TQBoardField.NAME);
		column_id_list.add(TQBoardField.TYPE);
		Map<Integer, MsgID> column_id_to_header = new HashMap<>();
		column_id_to_header.put(TQBoardField.CODE, CommonMsg.CODE);
		column_id_to_header.put(TQBoardField.MARKET_ID, TQMessages.MARKET_ID);
		column_id_to_header.put(TQBoardField.NAME, CommonMsg.NAME);
		column_id_to_header.put(TQBoardField.TYPE, TQMessages.TYPE_ID);
		return new OSCRepositoryTableModel<>(messages, repository, column_id_list, column_id_to_header);
	}
	
	public OSCRepositoryTableModel<CKind> createCKindTableModel(OSCRepository<Integer, CKind> repository) {
		List<Integer> column_id_list = new ArrayList<>();
		column_id_list.add(TQCKindField.CKIND_ID);
		column_id_list.add(TQCKindField.CKIND_PERIOD);
		column_id_list.add(TQCKindField.CKIND_NAME);
		Map<Integer, MsgID> column_id_to_header = new HashMap<>();
		column_id_to_header.put(TQCKindField.CKIND_ID, CommonMsg.ID);
		column_id_to_header.put(TQCKindField.CKIND_PERIOD, CommonMsg.PERIOD);
		column_id_to_header.put(TQCKindField.CKIND_NAME, CommonMsg.NAME);
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
	
	private JDialog createTableDialog(JTable table, MsgID title_msg_id, Dimension initial_size) {
		JDialog dialog = new JDialog(frame);
		JPanel panel = new JPanel(new BorderLayout());
		JScrollPane table_scroll_panel = new JScrollPane(table);
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
		}
	}
	
	void infoBox(String message, String title) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	void infoBox(String message) {
		infoBox(message, "Information");
	}


}

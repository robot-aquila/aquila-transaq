package ru.prolib.aquila.transaq.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
import ru.prolib.aquila.transaq.entity.Board;
import ru.prolib.aquila.transaq.entity.SecurityBoardParams;
import ru.prolib.aquila.transaq.impl.TQDirectory;
import ru.prolib.aquila.transaq.remote.ISecIDT;
import ru.prolib.aquila.transaq.remote.TQSecIDT;
import ru.prolib.aquila.ui.form.OSCRepositoryTableModel;

/**
 * Панель группового выбора списка символов из набора доступных через текущее подключение TRANSAQ. Позволяет
 * фильтровать по борде, маркету или типу символа и формировать текстовый список полных идентификаторов, разделенный
 * пробелами или символом перевода строки. 
 */
public class TQSymbolListPanel extends JPanel implements ListSelectionListener {
	private static final Logger logger = LoggerFactory.getLogger(TQSymbolListPanel.class);
	private static final long serialVersionUID = 1L;
	private static final List<SymbolType> secTypeList = Arrays.asList(
			SymbolType.UNKNOWN,
			SymbolType.STOCK,
			SymbolType.BOND,
			SymbolType.FUTURES,
			SymbolType.OPTION,
			SymbolType.CURRENCY
		);
	private final JTable boardsTableComp;
	private final JList<String> secTypeListComp;
	private final JTextArea resultTextComp;
	private final TQDirectory directory;
	
	public TQSymbolListPanel(JTable boards_table, TQDirectory directory) {
		setLayout(new BorderLayout());
		boardsTableComp = boards_table;
		this.directory = directory;
		
		JScrollPane scroll_pane;
		
		JPanel left = new JPanel(new GridLayout(1, 1)),
				center = new JPanel(new GridLayout(1, 1)),
				right = new JPanel(new GridLayout(1, 1));
		left.setMinimumSize(new Dimension(70, 200));
		left.setPreferredSize(new Dimension(150, 600));
		center.setMinimumSize(new Dimension(400, 200));
		center.setPreferredSize(new Dimension(400, 600));
		right.setMinimumSize(new Dimension(500, 200));
		right.setPreferredSize(new Dimension(500, 600));

		DefaultListModel<String> list_model = new DefaultListModel<>();
		for ( SymbolType type : secTypeList ) {
			list_model.addElement(type.getName());
		}
		secTypeListComp = new JList<>(list_model);
		secTypeListComp.addListSelectionListener(this);
		scroll_pane = new JScrollPane(secTypeListComp,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		left.add(scroll_pane);
		this.add(left, BorderLayout.LINE_START);

		boards_table.getSelectionModel().addListSelectionListener(this);
		scroll_pane = new JScrollPane(boards_table,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		center.add(scroll_pane);
		this.add(center, BorderLayout.CENTER);

		resultTextComp = new JTextArea(20, 40);
		resultTextComp.setFont(resultTextComp.getFont().deriveFont(12f));
		resultTextComp.setLineWrap(true);
		scroll_pane = new JScrollPane(resultTextComp,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		right.add(scroll_pane);
		this.add(right, BorderLayout.LINE_END);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void valueChanged(ListSelectionEvent event) {
		if ( event.getValueIsAdjusting() ) {
			return;
		}
		final Set<SymbolType> selected_types = new HashSet<>();
		final Set<String> selected_boards = new HashSet<>();
		for ( int i : secTypeListComp.getSelectedIndices() ) {
			selected_types.add(secTypeList.get(i));
		}
		OSCRepositoryTableModel<Board> tableModel = (OSCRepositoryTableModel<Board>) boardsTableComp.getModel();
		for ( int i : boardsTableComp.getSelectedRows() ) {
			selected_boards.add(tableModel.getEntity(boardsTableComp.convertRowIndexToModel(i)).getCode());
		}
		
		if ( selected_types.size() + selected_boards.size() == 0 ) {
			resultTextComp.setText("");
		} else {
			CompletableFuture.runAsync(() -> {
				long start_time = System.currentTimeMillis(), total_passed = 0;
				boolean check_type = selected_types.size() > 0, check_board = selected_boards.size() > 0;
				List<Symbol> result_list = new ArrayList<>();
				for ( SecurityBoardParams p : directory.getSecurityBoardParamsRepository().getEntities() ) {
					total_passed ++;
					ISecIDT sec_id = new TQSecIDT(p.getSecCode(), p.getBoardCode());
					Symbol symbol = directory.toSymbol(directory.toSymbolTID(sec_id));
					//if ( total_passed < 25 ) {
					//	System.out.println("Test symbol: " + symbol + " board: " + symbol.getExchangeID());
					//	System.out.println("Boards [" + selected_boards + "] contains board ["
					//			+ symbol.getExchangeID() + "]? "
					//			+ selected_boards.contains(symbol.getExchangeID()));
					//}
					if ( check_type && selected_types.contains(symbol.getType()) != true ) {
						continue;
					}
					if ( check_board && selected_boards.contains(symbol.getExchangeID()) != true ) {
						continue;
					}
					result_list.add(symbol);
				}
				String result = StringUtils.join(result_list, " "); 
				logger.debug("Types: " + selected_types);
				logger.debug("Boards: " + selected_boards);
				logger.debug("New symbol list ({} pcs) built: " + result, result_list.size());
				logger.debug("Total passed: {} pcs", total_passed);
				logger.debug("Time used: {} ms", System.currentTimeMillis() - start_time);
				SwingUtilities.invokeLater(() -> {
					resultTextComp.setText(result);
				}); 
			});
		}
	}

}

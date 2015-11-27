package gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;

import parsing.ParsedFile;

public class ButtonEditor extends DefaultCellEditor {

	protected JButton button;
	private ButtonListener bListener = new ButtonListener();

	public ButtonEditor(JCheckBox checkBox) {
		super(checkBox);
		button = new JButton();
		button.setOpaque(true);
		button.addActionListener(bListener);
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
		bListener.setRow(row);
		bListener.setColumn(column);
		bListener.setTable(table);
		button.setText( (value ==null) ? "" : value.toString() );
		return button;
	}

	class ButtonListener implements ActionListener {

		private int column, row;
		private JTable table;
		private JButton button;

		public void setColumn(int col){this.column = col;}
		public void setRow(int row){this.row = row;}
		public void setTable(JTable table){this.table = table;}
		public JButton getButton(){return this.button;}

		public void actionPerformed(ActionEvent event) {
			if (table.getValueAt(row, column-3) instanceof ParsedFile) {
				ParsedFile f = (ParsedFile)table.getValueAt(row, column-3);
				new DetailsDialog(null, f.getName(), true, f);
			}
		}
	}
}
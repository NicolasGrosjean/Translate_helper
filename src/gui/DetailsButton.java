package gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;

import parsing.Language;
import translator.TranslatorParsedFile;

public class DetailsButton extends DefaultCellEditor {

	protected JButton button;
	private ButtonListener bListener;

	public DetailsButton(JCheckBox checkBox, Language sourceLanguage, 
			boolean hasDestinationLanguage, Language destinationLanguage,
			boolean automaticDeepLCall, boolean automaticGoogleCall, Window window) {
		super(checkBox);
		button = new JButton();
		button.setOpaque(true);
		bListener = new ButtonListener(sourceLanguage, hasDestinationLanguage,
				destinationLanguage, automaticDeepLCall, automaticGoogleCall, window);
		button.addActionListener(bListener);
	}

	/**
	 * Method to have button behavior in a Table
	 */
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
		bListener.setRow(row);
		bListener.setColumn(column);
		bListener.setTable(table);
		button.setText( (value ==null) ? "" : value.toString() );
		return button;
	}

	/**
	 * Action of the details button
	 * @author NicolasGrosjean
	 *
	 */
	class ButtonListener implements ActionListener {

		private int column, row;
		private JTable table;
		private JButton button;
		
		private Language sourceLanguage;
		private boolean hasDestinationLanguage;
		private Language destinationLanguage;
		private boolean automaticDeepLCall;
		private boolean automaticGoogleCall;
		private Window window;

		ButtonListener(Language sourceLanguage, boolean hasDestinationLanguage,
				Language destinationLanguage,
				boolean automaticDeepLCall,
				boolean automaticGoogleCall,
				Window window) {
			this.sourceLanguage = sourceLanguage;
			this.hasDestinationLanguage = hasDestinationLanguage;
			this.destinationLanguage = destinationLanguage;
			this.automaticDeepLCall = automaticDeepLCall;
			this.automaticGoogleCall = automaticGoogleCall;
			this.window = window;
		}

		public void setColumn(int col){this.column = col;}
		public void setRow(int row){this.row = row;}
		public void setTable(JTable table){this.table = table;}
		public JButton getButton(){return this.button;}

		public void actionPerformed(ActionEvent event) {
			if (table.getValueAt(row, column-3) instanceof TranslatorParsedFile) {
				TranslatorParsedFile f = (TranslatorParsedFile)table.getValueAt(row, column-3);
				new DetailsDialog(null, f.getName(), true, f, sourceLanguage,
						hasDestinationLanguage, destinationLanguage,
						automaticDeepLCall, automaticGoogleCall, window);
			}
		}
	}
}
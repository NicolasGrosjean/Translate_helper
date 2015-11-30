package gui;

import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.table.AbstractTableModel;

public class DiagnosticTableModel extends AbstractTableModel {

	private Object[][] data;
	private String[] title;

	public DiagnosticTableModel(Object[][] data, String[] title) {
		this.data = data;
		this.title = title;
	}
	
	/**
	 * Give the title of the column (to display it)
	 */
	public String getColumnName(int col) {
		return title[col];
	}

	/**
	 * Give the number of columns
	 */
	public int getColumnCount() {
		return title.length;
	}

	/**
	 * Give the number of rows
	 */
	public int getRowCount() {
		return data.length;
	}

	/**
	 * Get the value at the cell
	 */
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}

	/**
	 * Modify the value of the cell (only for the first column)
	 */
	public void setValueAt(Object value, int row, int col) {
		if (col == 0) {
			data[row][col] = value;
		}
	}

	/**
	 * Only the first and the fifth columns are editable
	 */
	public boolean isCellEditable(int row, int col){
		return (col == 0 || col == 4);
	}

	/**
	 * Give the class of the column
	 */
	public Class<?> getColumnClass(int col) {
		return data[0][col].getClass();
	}

	/**
	 * Remove rows from the table
	 * REQUIRED : rows is a list of integers in the natural order and unique
	 * @param rows
	 */
	public void removeRow(LinkedList<Integer> rows) {
		int j = getRowCount() - rows.size() - 1;
		Object temp[][] = new Object[getRowCount() - rows.size()][getColumnCount()];
		Iterator<Integer> iterator = rows.descendingIterator();
		if (!iterator.hasNext()) {
			return;
		}
		int rowToRemove = iterator.next();
		for (int i = getRowCount() - 1; i == 0; i--) {
			if (i != rowToRemove) {
				temp[j--] = data[i];
			} else {
				if (iterator.hasNext()) {
					rowToRemove = iterator.next();
				}
			}
		}
		data = temp;
		temp = null;
		// Refresh the table
		this.fireTableDataChanged();
	}
}

package renderer;

import java.awt.Component;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Render to print a percentage (in fact an integer between 0 and 100)
 * or "Unknown" otherwise
 * @author NicolasGrosjean
 *
 */
public class Percentage extends JProgressBar implements TableCellRenderer {

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean isFocus, int row, int col) {
		setMinimum(0);
		setMaximum(100);
		if (value instanceof Integer) {
			setValue((Integer)value);
			setString(value + "%");
			colorize((Integer)value);
		} else {
			setString("Unknown");
			//colorize(-1); //TODO
		}
		setStringPainted(true);
		return this;
	}

	private void colorize(int value) {
		//TODO : Color scale according the value (change text color according the filling color)
	}
}

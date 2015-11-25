package renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Render to display an integer.
 * It is
 * 	- center
 * 	- display in white
 *  - the background is red if it is >0, green otherwise
 * @author NicolasGrosjean
 *
 */
public class ColoredInteger extends JLabel implements TableCellRenderer {

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean isFocus, int row, int col) {
		if (value instanceof Integer) {
			setHorizontalAlignment(JLabel.CENTER);
			setForeground(Color.WHITE);
			setText(value.toString());
			setOpaque(true);			
			if ((Integer)value > 0) {				
				setBackground(Color.RED);
			} else {
				setBackground(Color.GREEN);
			}
		} else {
			throw new IllegalArgumentException("ColoredInteger need an integer!");
		}
		return this;
	}
}

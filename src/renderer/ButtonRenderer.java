package renderer;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Renderer for a JButton with its text (or not)
 * @author NicolasGrosjean
 *
 */
public class ButtonRenderer extends JButton implements TableCellRenderer {

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean isFocus, int row, int col) {
		//Write in the button its value
		setText((value != null) ? value.toString() : "");
		return this;
	}
}
package renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.Painter;
import javax.swing.UIDefaults;
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
			color((Integer)value);
		} else {
			setString("Unknown");
			color(-1);
		}
		setStringPainted(true);
		return this;
	}

	private void color(int value) {
		if (value == 100) {
			color(Color.GREEN);
		} else if (value >= 50) {
			color(Color.ORANGE);
		} else if (value >= 0) {
			color(Color.RED);
		} else {
			color(Color.BLUE);
		}
		setOpaque(true);
		
		if (value > 60) {
			setForeground(Color.WHITE);
		} else {
			setForeground(Color.BLACK);
		}
	}

	/**
	 * Color a ProgressBar with the "Nimbus" look and feel
	 * @param c
	 */
	private void color(Color c) {
		UIDefaults defaults = new UIDefaults();
		defaults.put("ProgressBar[Enabled].foregroundPainter", new MyPainterNimbus(c));
		defaults.put("ProgressBar[Enabled+Finished].foregroundPainter", new MyPainterNimbus(c));
		putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.TRUE);
		putClientProperty("Nimbus.Overrides", defaults);
	}

	/**
	 * Paint a ProgressBar for "Nimbus" look and feel
	 * @author NicolasGrosjean
	 *
	 */
	class MyPainterNimbus implements Painter<JProgressBar> {

	    private final Color color;

	    public MyPainterNimbus(Color c1) {
	        this.color = c1;
	    }
	    @Override
	    public void paint(Graphics2D gd, JProgressBar t, int width, int height) {
	        gd.setColor(color);
	        // The "Nimbus" look and feel has a border of size 2
	        // So to respect it, the origin is move to (2,2)
	        // The width and height are reduced of 4
	        // (2 border for them)
	        gd.fillRect(2, 2, width - 4, height - 4);
	    }
	}
}

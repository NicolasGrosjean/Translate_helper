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
			colorize((Integer)value);
		} else {
			setString("Unknown");
			colorize(-1);
		}
		setStringPainted(true);
		return this;
	}

	private void colorize(int value) {
		if (value == 100) {
			colorize(Color.GREEN);
		} else if (value >= 50) {
			colorize(Color.ORANGE);
		} else if (value >= 0) {
			colorize(Color.RED);
		} else {
			colorize(Color.BLUE);
		}
		setOpaque(true);
		
		if (value > 60) {
			setForeground(Color.WHITE);
		} else {
			setForeground(Color.BLACK);
		}
	}
	
	private void colorize(Color c) {
		UIDefaults defaults = new UIDefaults();
		defaults.put("ProgressBar[Enabled].foregroundPainter", new MyPainter(c));
		defaults.put("ProgressBar[Enabled+Finished].foregroundPainter", new MyPainter(c));
		putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.TRUE);
		putClientProperty("Nimbus.Overrides", defaults);
	}

	class MyPainter implements Painter<JProgressBar> {

	    private final Color color;

	    public MyPainter(Color c1) {
	        this.color = c1;
	    }
	    @Override
	    public void paint(Graphics2D gd, JProgressBar t, int width, int height) {
	        gd.setColor(color);
	        gd.fillRect(2, 2, width, height - 4);
	    }
	}
}

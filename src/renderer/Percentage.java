package renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.util.Comparator;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.Painter;
import javax.swing.UIDefaults;
import javax.swing.table.TableCellRenderer;

/**
 * Render to print a Integer1/Integer2 with a progress bar
 * or "Unknown" otherwise
 * @author NicolasGrosjean
 *
 */
public class Percentage extends JProgressBar implements TableCellRenderer {

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean isFocus, int row, int col) {
		setMinimum(0);
		setMaximum(100);
		if (value instanceof String) {
			String s = (String)value;
			int i = stringToValue(s);
			setValue(i);
			setString(s);
			color(i);
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
		
		if (value > 70) {
			setForeground(Color.WHITE);
		} else if (value > 0) {
			setForeground(Color.BLACK);
		} else {
			setForeground(Color.RED);
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
	 * Give the percentage value associated to this string representation
	 * @param s
	 * @return
	 */
	public static int stringToValue(String s) {
		String s1 = s.split("/")[0];
		String s2 = s.split("/")[1];
		// If there is no elements we put i to 0 to avoid division by zero
		return (Integer.valueOf(s2) == 0)? 0 : Integer.valueOf(s1) * 100 / Integer.valueOf(s2);
	}

	/**
	 * Comparator for Integer1/Integer2
	 */
	public static Comparator<String> comparator = new Comparator<String>() {
		public int compare(String s1, String s2) {
			return Integer.compare(stringToValue(s1), stringToValue(s2));
		}
	};

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

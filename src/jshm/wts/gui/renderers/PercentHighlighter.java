package jshm.wts.gui.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;

public class PercentHighlighter extends AbstractHighlighter {		
	static final Color
	ORANGE = new Color(0xF48400),
	RED = new Color(0xEE0000),
	GREEN_FG = new Color(0x009900),
	GREEN_BG = new Color(0xcff6dd);
	
	@Override
	protected Component doHighlight(Component component,
			ComponentAdapter adapter) {
		
		int percent = (Integer) adapter.getFilteredValueAt(adapter.row, 6);
		
		if (adapter.column == 6) {
			Color fg =
			percent == 100
			? GREEN_FG :
			percent >= 80
			? ORANGE : RED;
				
			component.setForeground(fg);
			component.setFont(component.getFont().deriveFont(Font.BOLD));
		}
		
		if (100 == percent && !adapter.isSelected()) {
			component.setBackground(GREEN_BG);
		}
		
		return component;
	}
}

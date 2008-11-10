package jshm.wts.gui.renderers;

import java.awt.Color;
import java.awt.Component;


import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

public class NewScoreHighlighter extends AbstractHighlighter {
	public NewScoreHighlighter() {
		super(new HighlightPredicate() {
			@Override
			public boolean isHighlighted(Component renderer,
					ComponentAdapter adapter) {	
				return !adapter.isSelected() &&
					new Integer(0).equals(
						adapter.getFilteredValueAt(adapter.row, 4));
			}
		});
	}
	
	static final Color BG = new Color(0x9fe4f1);
	
	@Override
	protected Component doHighlight(Component component,
			ComponentAdapter adapter) {
		component.setBackground(BG);
		return component;
	}
}

package jshm.wts.gui.editors;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JComboBox.KeySelectionManager;
import javax.swing.table.TableCellEditor;

import jshm.gh.GhScore;

public class RatingEditor extends AbstractCellEditor implements
		TableCellEditor {

	JComboBox combo = new JComboBox(new Object[] {
		GhScore.getRatingIcon(0),
		GhScore.getRatingIcon(5),
		GhScore.getRatingIcon(4),
		GhScore.getRatingIcon(3)
	});
	
	public RatingEditor() {
		combo.setKeySelectionManager( new KeySelectionManager() {
			public int selectionForKey(char key, ComboBoxModel model) {
				switch (key) {
					case '0': return 0;
					case '5': return 1;
					case '4': return 2;
					case '3': return 3;
				}
				
				return -1;
			}
		});
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		int i = 0;
		
		if (value.equals(5))
			i = 1;
		else if (value.equals(4))
			i = 2;
		else if (value.equals(3))
			i = 3;
		
		combo.setSelectedIndex(i);
		return combo;
	}

	@Override
	public Object getCellEditorValue() {
		Object selected = combo.getSelectedItem();
		
		if (GhScore.getRatingIcon(5) == selected)
			return 5;
		if (GhScore.getRatingIcon(4) == selected)
			return 4;
		if (GhScore.getRatingIcon(3) == selected)
			return 3;
		
		return 0;
	}

}

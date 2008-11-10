package jshm.wts.gui.editors;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import jshm.Platform;

public class PlatformEditor extends AbstractCellEditor implements
		TableCellEditor {

	JComboBox combo = new JComboBox(new Object[] {Platform.PS2, Platform.XBOX360, Platform.PS3, Platform.WII});
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		combo.setSelectedItem(value);
		return combo;
	}

	@Override
	public Object getCellEditorValue() {
		return combo.getSelectedItem();
	}

}

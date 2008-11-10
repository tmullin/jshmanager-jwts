package jshm.wts.gui.editors;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import jshm.wts.Instrument;

public class InstrumentEditor extends AbstractCellEditor implements
		TableCellEditor {

	JComboBox combo = new JComboBox(Instrument.values());
	
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

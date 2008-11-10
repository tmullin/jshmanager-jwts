package jshm.wts.gui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;

import jshm.gui.EditPopupMenu;

import org.jdesktop.swingx.JXTable;

/**
 * This extends JXTable to allow certain KeyStrokes to not
 * trigger the cell editor (for menu accelerators) as well
 * as selecting all the text of the default cell editors
 * upon starting editing.
 * @author Tim Mullin
 *
 */
public class MyJXTable extends JXTable {
	List<KeyStroke> startEditorIgnoreKeyStrokes = null;
	
	public MyJXTable() {
		super();
		putClientProperty("JTable.autoStartsEdit", false);
		
		GenericEditor ed = new GenericEditor();
		setDefaultEditor(Object.class, ed);
		setDefaultEditor(Number.class, ed);
	}
	
	public void setStartEditorIgnoreKeyStrokes(List<KeyStroke> list) {
		startEditorIgnoreKeyStrokes = list;
	}
	
    protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
			int condition, boolean pressed) {
		// don't start if the keystroke is already in use
    	putClientProperty("JTable.autoStartsEdit",
    		null == startEditorIgnoreKeyStrokes ||
			!startEditorIgnoreKeyStrokes.contains(ks));

		return super.processKeyBinding(ks, e, condition, pressed);
	}
    
    // customized from JTable
    static class GenericEditor extends DefaultCellEditor {
		Class<?>[]							argTypes	= new Class[] { String.class };
		java.lang.reflect.Constructor<?>	constructor;
		Object								value;

		public GenericEditor() {
			super(new JTextField());
			getComponent().setName("Table.editor");
		}

		public boolean stopCellEditing() {
			EditPopupMenu.remove((JTextField) getComponent());
    		
			String s = (String) super.getCellEditorValue();
			// Here we are dealing with the case where a user
			// has deleted the string value in a cell, possibly
			// after a failed validation. Return null, so that
			// they have the option to replace the value with
			// null or use escape to restore the original.
			// For Strings, return "" for backward compatibility.
			if ("".equals(s)) {
				if (constructor.getDeclaringClass() == String.class) {
					value = s;
				}
				super.stopCellEditing();
			}

			try {
				value = constructor.newInstance(new Object[] { s });
			} catch (Exception e) {
				((JComponent) getComponent()).setBorder(new LineBorder(
						Color.red));
				return false;
			}
			
			return super.stopCellEditing();
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			this.value = null;
			((JComponent) getComponent())
					.setBorder(new LineBorder(Color.black));
			try {
				Class<?> type = table.getColumnClass(column);
				// Since our obligation is to produce a value which is
				// assignable for the required type it is OK to use the
				// String constructor for columns which are declared
				// to contain Objects. A String is an Object.
				if (type == Object.class) {
					type = String.class;
				}
				constructor = type.getConstructor(argTypes);
			} catch (Exception e) {
				return null;
			}
			
    		JTextField comp = (JTextField) super.getTableCellEditorComponent(
				table, value,
				isSelected, row, column);
			comp.selectAll();

			EditPopupMenu.add(comp);
			
			return comp;
		}

		public Object getCellEditorValue() {
			return value;
		}
	}
}

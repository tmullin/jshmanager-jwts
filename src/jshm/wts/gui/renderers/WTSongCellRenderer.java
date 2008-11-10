package jshm.wts.gui.renderers;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import jshm.wts.WTSong;

public class WTSongCellRenderer extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
    	value = ((WTSong) value).title;
    	return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}

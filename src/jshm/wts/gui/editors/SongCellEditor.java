package jshm.wts.gui.editors;

import java.awt.Component;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellEditor;

import jshm.wts.WTGame;
import jshm.wts.WTSong;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.autocomplete.ObjectToStringConverter;

public class SongCellEditor extends AbstractCellEditor implements TableCellEditor {
	static SongCellEditor instance = null;
	
	public static SongCellEditor getInstance(WTGame game) {
		if (null == instance)
			instance = new SongCellEditor(game);
		return instance;
	}
	
	public static void updateSongs(WTGame game) {
		if (null == game) return;
		createSongCombo(getInstance(game).combo, WTSong.getList(game));
	}
	
	public static final String SELECT_A_SONG = "Type a song name...";
	static final ListCellRenderer SONG_COMBO_RENDERER = new DefaultListCellRenderer() {
	    public Component getListCellRendererComponent(
	            JList list,
	            Object value,
	            int index,
	            boolean isSelected,
	            boolean cellHasFocus) {

	    	if (value instanceof WTSong)
	    		value = ((WTSong) value).title;
	    	
	    	return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	    }
	};
		
		
	static final ObjectToStringConverter SONG_COMBO_CONVERTER = new ObjectToStringConverter() {
		@Override
		public String getPreferredStringForItem(Object item) {
			if (null == item) return null;
			if (item instanceof WTSong)
				return ((WTSong) item).title;
			return item.toString();
		}
	};
	
	static void createSongCombo(JComboBox cb, List<WTSong> songs) {
		cb.setRenderer(SONG_COMBO_RENDERER);
		DefaultComboBoxModel model = (DefaultComboBoxModel) cb.getModel();
		model.removeAllElements();
		
//		model.addElement(SELECT_A_SONG);
		for (WTSong s : songs)
			model.addElement(s);
		
		AutoCompleteDecorator.decorate(cb, SONG_COMBO_CONVERTER);
	}
	
	
	JComboBox combo = new JComboBox();
	
	private SongCellEditor(WTGame game) {
		if (null == game) return;
		createSongCombo(combo, WTSong.getList(game));
	}
	
	@Override
	public Object getCellEditorValue() {
		return combo.getSelectedItem();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		combo.setSelectedItem(value);
		return combo;
	}
}

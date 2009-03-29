package jshm.wts.gui.models;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import java.util.*;

import jshm.Difficulty;
import jshm.Platform;
import jshm.wts.gui.GUI;
import jshm.wts.gui.editors.*;
import jshm.wts.gui.renderers.*;
import jshm.wts.Instrument;
import jshm.wts.WTScore;
import jshm.wts.WTSong;

public class WTScoreTableModel extends AbstractTableModel {
	GUI gui = null;
	JXTable parent = null;
	List<WTScore> scores = new ArrayList<WTScore>();
	
	public void setParent(GUI gui, JXTable parent) {
		this.parent = parent;
		SongCellEditor.updateSongs(gui.getCurrentGame());
		
		parent.setHighlighters(
			HighlighterFactory.createSimpleStriping(),
			new PercentHighlighter(),
			new NewScoreHighlighter());
		TableColumnModel cols = parent.getColumnModel();
		
		cols.getColumn(0).setCellEditor(new PlatformEditor());
		cols.getColumn(1).setCellEditor(new InstrumentEditor());
		cols.getColumn(2).setCellEditor(new DifficultyEditor());
		cols.getColumn(3).setCellRenderer(new WTSongCellRenderer());
		cols.getColumn(3).setCellEditor(SongCellEditor.getInstance(gui.getCurrentGame()));
		cols.getColumn(5).setCellRenderer(new RatingRenderer());
		cols.getColumn(5).setCellEditor(new RatingEditor());
	}
	
	public void addScore(WTScore score) {
		scores.add(score);
		fireTableDataChanged();
		if (null != parent) {
			parent.packAll();
			parent.scrollRowToVisible(getRowCount() - 1);
		}
	}
	
	public void addScores(List<WTScore> scores) {
		this.scores.addAll(scores);
		fireTableDataChanged();
		if (null != parent) {
			parent.packAll();
		}
	}
	
	public void setScores(List<WTScore> scores) {
		this.scores = scores;
		fireTableDataChanged();
		if (null != parent) parent.packAll();
	}
	
	public void removeScores(int ... indicies) {
		Arrays.sort(indicies);
		
		for (int i = indicies.length - 1; i >= 0; i--) {
			scores.remove(indicies[i]);
		}
		
		fireTableDataChanged();
		if (null != parent) parent.packAll();
	}
	
	public List<WTScore> getScores() {
		return scores;
	}
	
	private static final String[] HEADERS = {
		"Platform", "Instrument", "Difficulty",
		"Song", "Score", "Rating", "Percent", "Streak",
		"Comment", "Image URL", "Video URL"
	};
	
	@Override
	public String getColumnName(int column) {
		return HEADERS[column];
	}
	
	@Override
	public int getColumnCount() {
		return HEADERS.length;
	}
	
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
			case 0: return Platform.class;
			case 1: return Instrument.class;
			case 2: return Difficulty.class;
			case 3: return WTSong.class;
			case 4:
			case 5:
			case 6:
			case 7: return Integer.class;
			default: return String.class;
		}
	}

	@Override
	public int getRowCount() {
		return scores.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		WTScore s = scores.get(rowIndex);
		
		switch (columnIndex) {
			case 0: return s.getPlatform();
			case 1: return s.getInstrument();
			case 2: return s.getDifficulty();
			case 3: return s.getSong();
			case 4: return s.getScore();
			case 5: return s.getRating();
			case 6: return s.getPercent();
			case 7: return s.getStreak();
			case 8: return s.getComment();
			case 9: return s.getImageUrl();
			case 10: return s.getVideoUrl();
		}
		
		assert false;
		return null;
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		try {
//			System.out.println("setting " + rowIndex + "," + columnIndex + " to " + value);
			
			WTScore s = scores.get(rowIndex);
			
			switch (columnIndex) {
				case 0: s.setPlatform((Platform) value); break;
				case 1: s.setInstrument((Instrument) value); break;
				case 2: s.setDifficulty((Difficulty) value); break;
				case 3: s.setSong((WTSong) value); break;
				case 4: s.setScore((Integer) value); break;
				case 5: s.setRating((Integer) value); break;
				case 6: s.setPercent((Integer) value); break;
				case 7: s.setStreak((Integer) value); break;
				case 8: s.setComment((String) value); break;
				case 9: s.setImageUrl((String) value); break;
				case 10: s.setVideoUrl((String) value); break;
				
				default: assert false;
			}
			
			if (null != parent) parent.packAll();
		} catch (RuntimeException e) {
			JOptionPane.showMessageDialog(null,
				e instanceof NullPointerException
				? "Please enter a value"
				: e.getMessage(),
				"Invalid Value", JOptionPane.WARNING_MESSAGE);
		}
	}
}

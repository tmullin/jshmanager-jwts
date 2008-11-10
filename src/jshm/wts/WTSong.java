package jshm.wts;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WTSong implements Comparable<WTSong>, Serializable {
	static final Logger LOG = Logger.getLogger(WTSong.class.getName());
	
	public final int scoreHeroId;
	public final String title;
		
	public WTSong(int scoreHeroId, String title) {
		this.scoreHeroId = scoreHeroId;
		this.title = title;
		
		add(this);
	}
	
	@Override
	public String toString() {
		return scoreHeroId + "|" + title;
	}
	
	@Override
	public int compareTo(WTSong o) {
		return this.title.compareTo(o.title);
	}
	
	
	// cache stuff
	
//	private static final Object LOCK = "";
	
	public static final File CACHE_FILE = new File("data/wts/songs.cache");
	static Map<Integer, WTSong> map = new HashMap<Integer, WTSong>(100);
	static List<WTSong> list = new ArrayList<WTSong>(100);
	
	private static void add(WTSong song) {
		map.put(song.scoreHeroId, song);
		list.add(song);
		Collections.sort(list);
	}
	
	public static WTSong getById(int scoreHeroId) {
		return map.get(scoreHeroId);
	}
	
	/**
	 * 
	 * @return A {@link List} of all WTSongs sorted by title
	 */
	public static List<WTSong> getList() {
		return list;
	}
	
	public static void clear() {
		map.clear();
		list.clear();
	}
	
	public static void save() {
		LOG.fine("Saving songs to " + CACHE_FILE.getName());
		
		BufferedWriter out = null;
		
		try {
			out = new BufferedWriter(new FileWriter(CACHE_FILE));
		
			for (WTSong s : list) {
				out.write(s.toString());
				out.newLine();
			}
		} catch (Throwable t) {
			LOG.log(Level.WARNING, "Unable to save songs", t);
		} finally {
			if (null != out)
				try {
					out.close();
				} catch (IOException e) {}
		}
	}
	
	public static void load() throws IOException {
		map.clear();
		list.clear();
		
		BufferedReader in = new BufferedReader(new FileReader(CACHE_FILE));
		
		String line = null;
		String[] parts = null;
		
		while (null != (line = in.readLine())) {
			parts = line.split("\\|", 2);
			if (parts.length != 2) continue;
			
			new WTSong(Integer.parseInt(parts[0]), parts[1]);
		}
		
		in.close();
	}
	
	
	// partial matching 
	
	public static List<WTSong> findByTitle(String title) {
		title = title.toLowerCase();
		
		List<WTSong> songs = new ArrayList<WTSong>();
		
		// XXX exceedingly inefficient but it's small scale
		for (WTSong s : list) {
			if (s.title.toLowerCase().contains(title))
				songs.add(s);
		}
		
		return songs;
	}
}

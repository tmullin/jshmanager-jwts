package jshm.wts;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WTSong implements Comparable<WTSong>, Serializable {
	static final Logger LOG = Logger.getLogger(WTSong.class.getName());
	
	public final WTGame game;
	public final int scoreHeroId;
	public final String title;
		
	public WTSong(WTGame game, int scoreHeroId, String title) {
		this.game = game;
		this.scoreHeroId = scoreHeroId;
		this.title = title;
		
		add(this);
	}
	
	@Override
	public String toString() {
		return game + "|" + scoreHeroId + "|" + title;
	}
	
	@Override
	public int compareTo(WTSong o) {
		return this.title.compareTo(o.title);
	}
	
	
	// cache stuff
	
//	private static final Object LOCK = "";
	
	public static final File CACHE_FILE = new File("data/wts/songs.cache");
	static Map<WTGame, Map<Integer, WTSong>> map =
		new HashMap<WTGame, Map<Integer, WTSong>>(2);
	static Map<WTGame, List<WTSong>> list =
		new HashMap<WTGame, List<WTSong>>(2);
	
	static {
		clear();
	}
	
	private static void add(WTSong song) {
		map.get(song.game)
			.put(song.scoreHeroId, song);
		list.get(song.game)
			.add(song);
		Collections.sort(
			list.get(song.game));
	}
	
	public static WTSong getById(WTGame game, int scoreHeroId) {
		return map.get(game).get(scoreHeroId);
	}
	
	/**
	 * 
	 * @return A {@link List} of all WTSongs sorted by title
	 */
	public static List<WTSong> getList(WTGame game) {
		return list.get(game);
	}
	
	public static void clear() {
		map.clear();
		list.clear();
		
		clear(WTGame.GH_WT);
		clear(WTGame.GH_M);
	}
	
	public static void clear(WTGame game) {
		map.put(game, new HashMap<Integer, WTSong>(100));
		list.put(game, new ArrayList<WTSong>(100));
	}
	
	public static void save() {
		LOG.fine("Saving songs to " + CACHE_FILE.getName());
		
		BufferedWriter out = null;
		
		try {
			out = new BufferedWriter(new FileWriter(CACHE_FILE));
		
			for (Object key : list.keySet()) {
				for (WTSong s : list.get(key)) {
					out.write(s.toString());
					out.newLine();
				}
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
		clear();
		
		BufferedReader in = new BufferedReader(new FileReader(CACHE_FILE));
		
		String line = null;
		String[] parts = null;
		
		while (null != (line = in.readLine())) {
			parts = line.split("\\|", 3);
			if (parts.length != 3) continue;
			
			new WTSong(
				WTGame.valueOf(parts[0]),
				Integer.parseInt(parts[1]),
				parts[2]);
		}
		
		in.close();
	}
	
	
	// partial matching 
	
	public static List<WTSong> findByTitle(WTGame game, String title) {
		title = title.toLowerCase();
		
		List<WTSong> songs = new ArrayList<WTSong>();
		
		// XXX exceedingly inefficient but it's small scale
		for (WTSong s : list.get(game)) {
			if (s.title.toLowerCase().contains(title))
				songs.add(s);
		}
		
		return songs;
	}
}

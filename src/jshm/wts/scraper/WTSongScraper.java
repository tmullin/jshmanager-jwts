package jshm.wts.scraper;

import java.util.*;
import java.util.logging.Logger;

import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import jshm.Difficulty;
import jshm.Platform;
import jshm.exceptions.ScraperException;
import jshm.scraper.*;
import jshm.scraper.TieredTabularDataExtractor.InvalidChildCountStrategy;
import jshm.sh.scraper.Formats;
import jshm.sh.scraper.GhDataTable;
import jshm.wts.*;

public class WTSongScraper {
	static final Logger LOG = Logger.getLogger(WTSongScraper.class.getName());
	
	static {
		Formats.init();
	}
	
	static final GhDataTable TOP_SCORES = new GhDataTable(2, 4, 8,
		"-|text~link=songid"
	);
	
	public static List<WTSong> scrape(final WTGame game, final Platform plat) throws ParserException, ScraperException {
		return scrape(game, plat, Instrument.GUITAR, Difficulty.EXPERT);
	}
	
	public static List<WTSong> scrape(final WTGame game, final Platform plat, final Instrument inst, final Difficulty diff)
			throws ParserException, ScraperException {
		List<WTSong> songs = new ArrayList<WTSong>();
		
		SongHandler handler = new SongHandler(game, songs);
		
		NodeList nodes = Scraper.scrape(
			URLs.getTopScoresUrl(game, plat, inst, diff), handler);
		
		
		LOG.finer("scrape() returned " + nodes.size() + " nodes");
		
		if (nodes.size() == 0)
			throw new ScraperException("nodes.size() == 0, invalid page format?");
		
//		System.out.println(nodes.toHtml());

		TieredTabularDataExtractor.extract(nodes, handler);
		
		LOG.fine("extract() returned " + songs.size() + " songs");
		
		return songs;
	}
	
	
	private static class SongHandler extends TieredTabularDataAdapter {
		WTGame game;
		List<WTSong> songs;
		
		public SongHandler(WTGame game, List<WTSong> songs) {
			this.game = game;
			this.songs = songs;
			
			// ignore due to GH_M dlc not being detected
			this.invalidChildCountStrategy = InvalidChildCountStrategy.IGNORE;
		}
		
		@Override
		public DataTable getDataTable() {
			return TOP_SCORES;
		}
		
		@Override
		public void handleDataRow(String[][] data) throws ScraperException {
			int id = Integer.parseInt(data[1][1]);
			String title = data[1][0];
			
			songs.add(
				new WTSong(game, id, title));
		}
	}
}

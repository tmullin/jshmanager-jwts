package jshm.wts.concepts;

import java.util.List;

import jshm.Difficulty;
import jshm.Platform;
import jshm.logging.Log;
import jshm.wts.Instrument;
import jshm.wts.WTSong;
import jshm.wts.scraper.WTSongScraper;

public class WTSongScraperTest {
	public static void main(String[] args) throws Exception {
		Log.configTestLogging();
		
		Platform plat = Platform.XBOX360;
		Instrument inst = Instrument.GUITAR;
		Difficulty diff = Difficulty.EXPERT;
		
		List<WTSong> songs = WTSongScraper.scrape(plat, inst, diff);
		
		for (WTSong s : songs)
			System.out.println(s);
		
		WTSong.save();
	}
}

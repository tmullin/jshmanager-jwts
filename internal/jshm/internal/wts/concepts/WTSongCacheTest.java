package jshm.internal.wts.concepts;

import jshm.wts.WTGame;
import jshm.wts.WTSong;

public class WTSongCacheTest {
	public static void main(String[] args) throws Exception {
		WTSong.load();
		WTGame game = WTGame.GH_M;
		
		for (WTSong s : WTSong.getList(game))
			System.out.println(s);
		
		String search = "te";
		
		System.out.println("\nSearch: " + search);
		
		for (WTSong s : WTSong.findByTitle(game, search))
			System.out.println(s);
	}
}

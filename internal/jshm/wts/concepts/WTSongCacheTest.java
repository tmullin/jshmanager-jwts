package jshm.wts.concepts;

import jshm.wts.WTSong;

public class WTSongCacheTest {
	public static void main(String[] args) throws Exception {
		WTSong.load();
		
		for (WTSong s : WTSong.getList())
			System.out.println(s);
		
		String search = "te";
		
		System.out.println("\nSearch: " + search);
		
		for (WTSong s : WTSong.findByTitle(search))
			System.out.println(s);
	}
}

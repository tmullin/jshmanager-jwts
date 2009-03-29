package jshm.internal.wts.concepts;

import jshm.Difficulty;
import jshm.Platform;
import jshm.gui.LoginDialog;
import jshm.logging.Log;
import jshm.wts.Instrument;
import jshm.wts.WTGame;
import jshm.wts.WTScore;
import jshm.wts.WTSong;
import jshm.wts.sh.Api;

public class ApiTest {
	public static void main(String[] args) throws Exception {
		Log.configTestLogging();
		WTSong.load();

		WTGame game = WTGame.GH_M;
		
		WTScore s = new WTScore(
			game,
			Platform.XBOX360, Difficulty.EASY,
			WTSong.getById(game, 229), Instrument.GUITAR,
			1234, 5, 42, 4242,
			"jwts test",
			"http://www.google.com",
			"");
		
		LoginDialog.showDialog();
		Api.submitWTScore(game, s);
	}
}

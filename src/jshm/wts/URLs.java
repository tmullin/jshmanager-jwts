package jshm.wts;

import jshm.Difficulty;
import jshm.Platform;
import jshm.sh.RbPlatform;

public class URLs {
	public static final String
	MANAGE_SCORES = jshm.sh.URLs.BASE +
		"/manage_scores.php?group=%s&game=%s&platform=%s&size=1&inst=%s&diff=%s&team=0",
	TOP_SCORES = jshm.sh.URLs.BASE + 
		"/top_scores.php?group=%s&game=%s&platform=%s&size=1&inst=%s&diff=%s&team=0",
	INSERT_SCORE = jshm.sh.URLs.BASE +
		"/insert_score.php?group=%s&game=%s&platform=%s&size=1&inst=%s&diff=%s&song=%s&team=0')"
	;
	
	public static String getManageScoresUrl(WTGame game, Platform plat, Instrument inst, Difficulty diff) {
		return String.format(MANAGE_SCORES,
			game.scoreHeroGroupId,
			game.getId(plat), RbPlatform.getId(plat), inst.scoreHeroId, diff.scoreHeroId);
	}
	
	public static String getTopScoresUrl(WTGame game, Platform plat, Instrument inst, Difficulty diff) {
		return String.format(TOP_SCORES,
			game.scoreHeroGroupId,
			game.getId(plat), RbPlatform.getId(plat), inst.scoreHeroId, diff.scoreHeroId);
	}
	
	public static String getInsertScoreUrl(WTGame game, WTScore score) {
		return getInsertScoreUrl(game, score.getPlatform(), score.getInstrument(), score.getDifficulty(), score.getSong());
	}
	
	public static String getInsertScoreUrl(WTGame game, Platform plat, Instrument inst, Difficulty diff, WTSong song) {
		return String.format(INSERT_SCORE,
			game.scoreHeroGroupId,
			game.getId(plat), RbPlatform.getId(plat), inst.scoreHeroId, diff.scoreHeroId, song.scoreHeroId);
	}
}

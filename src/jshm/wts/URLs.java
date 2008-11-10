package jshm.wts;

import jshm.Difficulty;
import jshm.Platform;
import jshm.sh.RbPlatform;

public class URLs {
	public static final String
	MANAGE_SCORES = jshm.sh.URLs.BASE +
		"/manage_scores.php?group=7&game=%s&platform=%s&size=1&inst=%s&diff=%s&team=0",
	TOP_SCORES = jshm.sh.URLs.BASE + 
		"/top_scores.php?group=7&game=%s&platform=%s&size=1&inst=%s&diff=%s&team=0",
	INSERT_SCORE = jshm.sh.URLs.BASE +
		"/insert_score.php?group=7&game=%s&platform=%s&size=1&inst=%s&diff=%s&song=%s&team=0')"
	;
	
	public static String getManageScoresUrl(Platform plat, Instrument inst, Difficulty diff) {
		return String.format(MANAGE_SCORES,
			WTGame.getId(plat), RbPlatform.getId(plat), inst.scoreHeroId, diff.scoreHeroId);
	}
	
	public static String getTopScoresUrl(Platform plat, Instrument inst, Difficulty diff) {
		return String.format(TOP_SCORES,
			WTGame.getId(plat), RbPlatform.getId(plat), inst.scoreHeroId, diff.scoreHeroId);
	}
	
	public static String getInsertScoreUrl(WTScore score) {
		return getInsertScoreUrl(score.getPlatform(), score.getInstrument(), score.getDifficulty(), score.getSong());
	}
	
	public static String getInsertScoreUrl(Platform plat, Instrument inst, Difficulty diff, WTSong song) {
		return String.format(INSERT_SCORE,
			WTGame.getId(plat), RbPlatform.getId(plat), inst.scoreHeroId, diff.scoreHeroId, song.scoreHeroId);
	}
}

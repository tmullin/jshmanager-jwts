package jshm.wts;

public enum Instrument {
	GUITAR(1, "gtr"),
	BASS(2, "bass"),
	GHWT_DRUMS(3, "drums"),
	RB_DRUMS(4, "drums"),
	VOCALS(5, "vocals");
	
	private Instrument(int scoreHeroId, String formAbbr) {
		this.scoreHeroId = scoreHeroId;
		this.formAbbr = formAbbr;
	}
	
	public final int scoreHeroId;
	public final String formAbbr;
	
	public static Instrument smartValueOf(String value) {
		return smartValueOf(value, null);
	}
	
	public static Instrument smartValueOf(String value, Instrument defaultDrums) {
		String value2 = value.toUpperCase();
		
		if ("DRUMS".startsWith(value2) && null != defaultDrums)
			return defaultDrums;
		
		for (Instrument i : values()) {
			if (i.name().startsWith(value2))
				return i;
		}
		
		return null;
	}
}

package jshm.wts;

import java.io.Serializable;
import java.util.*;

import jshm.Platform;

public class WTGame implements Serializable {
	public static final WTGame
	GH_WT = new WTGame(7, "GH_WT"),
	GH_M = new WTGame(9, "GH_M"),
	GH_SH = new WTGame(11, "GH_SH");
	
	public static WTGame valueOf(String s) {
		s = s.toUpperCase();
		
		if (s.equals("GH_WT")) return GH_WT;
		else if (s.equals("GH_M")) return GH_M;
		else if (s.equals("GH_SH")) return GH_SH;
		
		throw new IllegalArgumentException("Invalid game name: " + s);
	}
	
	static {
		GH_WT.idMap.put(Platform.PS2, 16);
		GH_WT.idMap.put(Platform.XBOX360, 17);
		GH_WT.idMap.put(Platform.PS3, 18);
		GH_WT.idMap.put(Platform.WII, 19);

		GH_M.idMap.put(Platform.PS2, 21);
		GH_M.idMap.put(Platform.XBOX360, 22);
		GH_M.idMap.put(Platform.PS3, 23);
		GH_M.idMap.put(Platform.WII, 24);
		
		GH_SH.idMap.put(Platform.PS2, 26);
		GH_SH.idMap.put(Platform.XBOX360, 27);
		GH_SH.idMap.put(Platform.PS3, 28);
		GH_SH.idMap.put(Platform.WII, 29);
	}
	
	public final int scoreHeroGroupId;
	public final String name;
	
	public WTGame(final int scoreHeroGroupId, final String name) {
		this.scoreHeroGroupId = scoreHeroGroupId;
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
	
	final Map<Platform, Integer> idMap = new HashMap<Platform, Integer>(4);
	
	public int getId(Platform p) {
		return idMap.get(p);
	}
}

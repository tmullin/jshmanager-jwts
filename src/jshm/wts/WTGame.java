package jshm.wts;

import java.util.*;

import jshm.Platform;

public class WTGame {
	public static final int scoreHeroGroupId = 7;
	
	static final Map<Platform, Integer> idMap = new HashMap<Platform, Integer>(4);
	
	static {
		idMap.put(Platform.PS2, 16);
		idMap.put(Platform.XBOX360, 17);
		idMap.put(Platform.PS3, 18);
		idMap.put(Platform.WII, 19);
	}
	
	public static int getId(Platform p) {
		return idMap.get(p);
	}
}

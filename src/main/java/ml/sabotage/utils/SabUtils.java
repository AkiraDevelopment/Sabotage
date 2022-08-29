package ml.sabotage.utils;

import ml.zer0dasho.plumber.game.Timer;

import java.io.File;

public class SabUtils {
	
	public static final File SOURCE = new File("plugins/Sabotage/worlds");
	
    public static int calcSabs(int playerCount) {
        return (playerCount/5) + 1;
    }
    
    public static void cleanMap(File mapFolder) {
    	String[] delete = new String[] {"session.lock", "players", "playerdata", "stats", "advancements", "uid.dat"};
    	
    	for(String toDelete : delete) {
    		File file = new File(mapFolder, toDelete);
 
    		if(file.exists())
    			file.delete();
    	}
    }

	public static Timer makeTimer(int hours, int minutes, int seconds) {
		return new Timer(null, hours, minutes, seconds);
	}

}
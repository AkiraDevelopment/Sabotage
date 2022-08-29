package ml.sabotage;

import java.io.File;

import ml.sabotage.config.ItemSettings;
import ml.sabotage.game.managers.ConfigManager;
import ml.sabotage.utils.SabUtils;
import ml.zer0dasho.plumber.utils.Sprink;

public class Validate {
	
	public static boolean validateMaps() {
		
		if(!worldExists(ConfigManager.Setting.HUB_WORLD_NAME.getString())) {
			Main.disablePlugin("Hub doesn't exist!");
			return false;
		}

		ConfigManager.Setting.MAPS.getStringList().removeIf(map -> !validateMap(map));
		
		if(ConfigManager.Setting.MAPS.getStringList().size() == 0) {
			Main.disablePlugin("No maps exist!");
			return false;
		}
		
		return true;
	}

	public static boolean validate() {
		
		ItemSettings config = Main.config;
		
		if(config == null) {
			Main.disablePlugin("Failed to load items.yml!");
			return false;
		}
		
		return validateMaps();
	}
	
	public static boolean validateMap(String map) {
		
		if(!worldExists(map)) {
			Main.getInstance().getServer().broadcastMessage(
					Sprink.color(
							String.format("&cWarning: Map '%s' does not exist.", map)));
			return false;
		}
		
		return true;
	}
	
	private static boolean worldExists(String name) {
		return new File(SabUtils.SOURCE, name).exists();
	}
	
}
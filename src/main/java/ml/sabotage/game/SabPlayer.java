package ml.sabotage.game;

import ml.sabotage.Main;
import ml.sabotage.command.sabotage.CommandSabotage;
import ml.sabotage.game.managers.ConfigManager;
import ml.sabotage.game.managers.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import ml.sabotage.PlayerData;

public class SabPlayer {

    public final Player player;

    public SabPlayer(Player player) {
        this.player = player;
    }

    public void updateKarma() {
        DataManager dataManager = Main.getInstance().getManager(DataManager.class);
        PlayerData playerData = dataManager.getPlayerData(player.getUniqueId());
        if(playerData.getKarma() <= ConfigManager.Setting.KARMA_BAN_THRESHOLD.getInt()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    ConfigManager.Setting.KARMA_BAN_COMMAND.getString()
                            .replace("%player%", player.getName()));
            playerData.setKarma(ConfigManager.Setting.DEFAULT_KARMA.getInt());
            return;
        }
        player.setExp(0.0f);
        player.setLevel(playerData.getKarma());
    }

    public void addKarma(int karma) {
        DataManager dataManager = Main.getInstance().getManager(DataManager.class);
        PlayerData playerData = dataManager.getPlayerData(player.getUniqueId());
    	if(!CommandSabotage.TEST) {
            if(karma > 0)
                playerData.setLifetime(playerData.getKarmaTotal() + karma);
	        playerData.addKarma(karma);
            playerData.save();
    	}

        updateKarma();
    }

    public void resetKarma(){
        DataManager dataManager = Main.getInstance().getManager(DataManager.class);
        PlayerData playerData = dataManager.getPlayerData(player.getUniqueId());
        playerData.setKarma(ConfigManager.Setting.DEFAULT_KARMA.getInt());
        updateKarma();
    }
}
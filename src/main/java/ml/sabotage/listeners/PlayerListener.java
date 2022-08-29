package ml.sabotage.listeners;

import ml.sabotage.Main;
import ml.sabotage.game.managers.DataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Main.getInstance().getManager(DataManager.class).getPlayerData(e.getPlayer().getUniqueId(), (playerData) -> {});
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        DataManager dataManager = Main.getInstance().getManager(DataManager.class);
        dataManager.getPlayerData(player.getUniqueId()).save();
        dataManager.unloadPlayerData(player.getUniqueId());
    }

}

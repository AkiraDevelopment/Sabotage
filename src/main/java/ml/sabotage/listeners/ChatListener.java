package ml.sabotage.listeners;

import ml.zer0dasho.plumber.utils.Sprink;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void chatHandler(AsyncPlayerChatEvent e) {
        e.setFormat(Sprink.color(e.getPlayer().getDisplayName() + " &8» &r" + e.getMessage()));
    }

}

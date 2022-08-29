package ml.sabotage.listeners;

import dev.rosewood.rosegarden.utils.HexUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void chatHandler(AsyncPlayerChatEvent e) {
        e.setFormat(HexUtils.colorify(e.getPlayer().getDisplayName() + " &8» &r" + e.getMessage()));
    }
}

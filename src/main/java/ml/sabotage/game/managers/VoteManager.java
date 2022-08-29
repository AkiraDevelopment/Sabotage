package ml.sabotage.game.managers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import ml.sabotage.Main;
import ml.sabotage.Validate;
import ml.sabotage.game.stages.Sabotage;
import ml.zer0dasho.plumber.utils.Sprink;
import ml.zer0dasho.plumber.utils.Trycat;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class VoteManager extends Manager {

    public final Map<UUID, String> VOTES = Maps.newHashMap();
    public final List<String> SELECTION = Lists.newArrayList();

    public VoteManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    public void listVotes(CommandSender sender) {
        sender.sendMessage(Sprink.color("&c&m--------&r &eVotes &c&m---------"));
        SELECTION.forEach((map) -> {
            int numVotes = Collections.frequency(VOTES.values(), map);
            String cleanedName = map.replaceAll("_", " ");
            sender.sendMessage(Sprink.color("    &3" + cleanedName + ": " + numVotes));
        });

        sender.sendMessage(Sprink.color("&c&m-----------------------"));
    }

    public void reset(CommandSender sender) {
        if(inLobby(sender)) {
            resetMapSelection();
            listVotes(sender);
        }
    }

    private static boolean inLobby(CommandSender sender) {
        LocaleManager locale = Main.getInstance().getManager(LocaleManager.class);
        if(Main.sabotage.getCurrent_state() != Sabotage.LOBBY) {
            locale.sendMessage(sender, "only-in-lobby");
            return false;
        }

        return true;
    }

    public void resetMapSelection() {
        Validate.validateMaps();

        List<String> maps = new ArrayList<>(ConfigManager.Setting.MAPS.getStringList());
        SELECTION.clear();
        VOTES.clear();

        while(maps.size() > 0 && SELECTION.size() < 3)
            SELECTION.add(Sprink.randomElement(maps, true));
    }

    public void vote(CommandSender sender, Integer vote)  {
        if(inLobby(sender)) {
            String selectedMap = SELECTION.get(vote - 1);
            UUID playerId = Trycat.Get(() -> ((Player)sender).getUniqueId(), UUID.randomUUID());

            VOTES.put(playerId, selectedMap);
            listVotes(sender);
        }
    }

    @Override
    public void reload() {
        resetMapSelection();
    }

    @Override
    public void disable() {

    }
}

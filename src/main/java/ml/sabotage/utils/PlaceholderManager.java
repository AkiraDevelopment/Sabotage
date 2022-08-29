package ml.sabotage.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import ml.sabotage.Main;
import ml.sabotage.PlayerData;
import ml.sabotage.game.managers.DataManager;
import ml.sabotage.game.managers.PlayerManager;
import ml.sabotage.game.roles.Detective;
import ml.sabotage.game.roles.IngamePlayer;
import ml.sabotage.game.roles.Innocent;
import ml.sabotage.game.roles.Saboteur;
import ml.sabotage.game.stages.Sabotage;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderManager extends PlaceholderExpansion implements Relational {

    public final Main plugin;

    public PlaceholderManager(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return plugin.getDescription().getName().toLowerCase();
    }

    @Override
    public @NotNull String getAuthor() {
        return "AkiraDev";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }



    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if(player == null)
            return null;
        DataManager dataManager = plugin.getManager(DataManager.class);
        PlayerData playerData = dataManager.getPlayerData(player.getUniqueId());
        if (playerData == null) {
            return null;
        }
        if(params.equalsIgnoreCase("karma")){
            return String.valueOf(playerData.getKarma());
        }
        if(params.equalsIgnoreCase("karma_total")){
            return String.valueOf(playerData.getKarmaTotal());
        }
        if(params.equalsIgnoreCase("wins")){
            return String.valueOf(playerData.getWins());
        }
        if(params.equalsIgnoreCase("losses")){
            return String.valueOf(playerData.getLosses());
        }
        if(params.equalsIgnoreCase("kills")){
            return String.valueOf(playerData.getKills());
        }
        if(params.equalsIgnoreCase("deaths")){
            return String.valueOf(playerData.getDeaths());
        }
        if(params.equalsIgnoreCase("kills_correct")){
            return String.valueOf(playerData.getCorrectKills());
        }
        if(params.equalsIgnoreCase("kills_wrong")){
            return String.valueOf(playerData.getIncorrectKills());
        }
        if(params.equalsIgnoreCase("map")){
            return Main.CurrentMap;
        }
        return null; // Placeholder is unknown by the Expansion
    }

    @Override
    public String onPlaceholderRequest(Player one, Player two, String identifier) {
        if(one == null || two == null)
            return null;

        PlayerManager playerManager = plugin.getManager(PlayerManager.class);
        if(Main.sabotage.getCurrent_state() == Sabotage.INGAME) {
            IngamePlayer Sender = playerManager.getRole(one.getUniqueId());
            IngamePlayer Reader = playerManager.getRole(two.getUniqueId());
            if (Sender == null || Reader == null)
                return null;
            if (identifier.equalsIgnoreCase("role")) {
                if (Reader instanceof Saboteur) {
                    if (Sender instanceof Saboteur) {
                        return "&c";
                    } else if(Sender instanceof Detective) {
                        return "&9";
                    }else{
                        return "&a";
                    }
                }else if (Reader instanceof Innocent || Reader instanceof Detective) {
                    if (Sender instanceof Detective) {
                        return "&9";
                    }else{
                        return "&e";
                    }
                }
            }
        }
        return null;
    }
}
package ml.sabotage.game.managers;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.manager.AbstractDataManager;
import ml.sabotage.PlayerData;
import ml.sabotage.database.migrations._1_Create_Tables;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.function.Consumer;

public class DataManager extends AbstractDataManager {
    private final Map<UUID, PlayerData> playerData;

    public DataManager(RosePlugin rosePlugin) {
        super(rosePlugin);
        this.playerData = new HashMap<>();
    }

    @Override
    public List<Class<? extends DataMigration>> getDataMigrations() {
        return Collections.singletonList(
                _1_Create_Tables.class
        );
    }

    public PlayerData getPlayerData(UUID uuid){
        return this.playerData.get(uuid);
    }

    public void updatePlayerData(PlayerData playerData){
        this.async(() -> this.databaseConnector.connect(connection -> {
            boolean create;

            String checkQuery = "SELECT 1 FROM " + this.getTablePrefix() + "player_data WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(checkQuery)) {
                statement.setString(1, playerData.getUUID().toString());
                ResultSet result = statement.executeQuery();
                create = !result.next();
            }

            if(create){
                String insertQuery = "INSERT INTO " + this.getTablePrefix() + "player_data (uuid, karma, lifetime_karma, wins, losses, sabpasses, kills, correct_kills, incorrect_kills, deaths) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                    statement.setString(1, playerData.getUUID().toString());
                    statement.setInt(2, playerData.getKarma());
                    statement.setInt(3, playerData.getKarmaTotal());
                    statement.setInt(4, playerData.getWins());
                    statement.setInt(5, playerData.getLosses());
                    statement.setInt(6, playerData.getSabpasses());
                    statement.setInt(7, playerData.getKills());
                    statement.setInt(8, playerData.getCorrectKills());
                    statement.setInt(9, playerData.getIncorrectKills());
                    statement.setInt(10, playerData.getDeaths());

                    statement.executeUpdate();
                }
            }else{
                String updateQuery = "UPDATE " + this.getTablePrefix() + "player_data SET karma = ?, lifetime_karma = ?, wins = ?, losses = ?, sabpasses = ?, kills = ?, correct_kills = ?, incorrect_kills = ?, deaths = ? WHERE uuid = ?";
                try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                    statement.setInt(1, playerData.getKarma());
                    statement.setInt(2, playerData.getKarmaTotal());
                    statement.setInt(3, playerData.getWins());
                    statement.setInt(4, playerData.getLosses());
                    statement.setInt(5, playerData.getSabpasses());
                    statement.setInt(6, playerData.getKills());
                    statement.setInt(7, playerData.getCorrectKills());
                    statement.setInt(8, playerData.getIncorrectKills());
                    statement.setInt(9, playerData.getDeaths());
                    statement.setString(10, playerData.getUUID().toString());
                    statement.executeUpdate();
                }
            }
        }));
    }

    public void getPlayerData(UUID uuid, Consumer<PlayerData> callback) {
        if(this.playerData.containsKey(uuid)) {
            callback.accept(this.playerData.get(uuid));
            return;
        }

        this.async(() -> this.databaseConnector.connect(connection -> {
            PlayerData playerData;
            String dataQuery = "SELECT * FROM " + this.getTablePrefix() + "player_data WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(dataQuery)) {
                statement.setString(1, uuid.toString());
                ResultSet resultSet = statement.executeQuery();

                if(resultSet.next()){
                    int karma = resultSet.getInt("karma");
                    int lifetimeKarma = resultSet.getInt("lifetime_karma");
                    int wins = resultSet.getInt("wins");
                    int losses = resultSet.getInt("losses");
                    int sabpasses = resultSet.getInt("sabpasses");
                    int kills = resultSet.getInt("kills");
                    int correctKills = resultSet.getInt("correct_kills");
                    int incorrectKills = resultSet.getInt("incorrect_kills");
                    int deaths = resultSet.getInt("deaths");

                    playerData = new PlayerData(uuid);
                    playerData.setData(karma, lifetimeKarma, wins, losses, sabpasses, kills, correctKills, incorrectKills, deaths);

                    this.playerData.put(uuid, playerData);
                    Bukkit.getPlayer(uuid).setLevel(playerData.getKarma());
                }else{
                    playerData = new PlayerData(uuid);
                    this.playerData.put(uuid, playerData);
                    Bukkit.getPlayer(uuid).setLevel(playerData.getKarma());
                    callback.accept(playerData);
                }
            }
        }));
    }

    public void unloadPlayerData(UUID uuid) {
        this.playerData.remove(uuid);
    }

    private void async(Runnable asyncCallback) {
        Bukkit.getScheduler().runTaskAsynchronously(this.rosePlugin, asyncCallback);
    }

    private void sync(Runnable syncCallback) {
        Bukkit.getScheduler().runTask(this.rosePlugin, syncCallback);
    }

}


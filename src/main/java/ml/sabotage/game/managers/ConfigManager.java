package ml.sabotage.game.managers;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.config.RoseSetting;
import dev.rosewood.rosegarden.manager.AbstractConfigurationManager;
import ml.sabotage.Main;

import java.util.List;

public class ConfigManager extends AbstractConfigurationManager {

    public enum Setting implements RoseSetting {
        HUB_WORLD_NAME("hub", "hub"),
        MAPS("maps", List.of("sab1", "sab2", "sab3")),
        CHAT_FORMAT("chat-format", "%rel_sabotage_role%%player% &8» &r%message%"),
        DETECTIVE("detective", null),
        DETECTIVE_DETECTIVE("detective.detective", 0),
        DETECTIVE_INNOCENT("detective.innocent", -50),
        DETECTIVE_SABOTEUR("detective.saboteur", 40),
        DETECTIVE_DEATH("detective.death", 0),
        INNOCENT("innocent", null),
        INNOCENT_DETECTIVE("innocent.detective", -75),
        INNOCENT_INNOCENT("innocent.innocent", -50),
        INNOCENT_SABOTEUR("innocent.saboteur", 40),
        INNOCENT_DEATH("innocent.death", 0),
        SABOTEUR("saboteur", null),
        SABOTEUR_DETECTIVE("saboteur.detective", 40),
        SABOTEUR_INNOCENT("saboteur.innocent", 25),
        SABOTEUR_SABOTEUR("saboteur.saboteur", -75),
        SABOTEUR_DEATH("saboteur.death", -20),
        LOBBY("lobby", null),
        LOBBY_HOURS("lobby.hours", 0),
        LOBBY_MINUTES("lobby.minutes", 1),
        LOBBY_SECONDS("lobby.seconds", 30),
        LOBBY_RELOAD("lobby.reload", 0),
        TESTER("tester", null),
        TESTER_HOURS("tester.hours", 0),
        TESTER_MINUTES("tester.minutes", 0),
        TESTER_SECONDS("tester.seconds", 15),
        TESTER_RELOAD("tester.reload", 3),
        REFILL("refill", null),
        REFILL_HOURS("refill.hours", 0),
        REFILL_MINUTES("refill.minutes", 5),
        REFILL_SECONDS("refill.seconds", 0),
        REFILL_RELOAD("refill.reload", 0),
        INGAME("ingame", null),
        INGAME_HOURS("ingame.hours", 0),
        INGAME_MINUTES("ingame.minutes", 10),
        INGAME_SECONDS("ingame.seconds", 0),
        INGAME_RELOAD("ingame.reload", 0),
        PANIC_LIFE("panic_life", null),
        PANIC_LIFE_HOURS("panic_life.hours", 0),
        PANIC_LIFE_MINUTES("panic_life.minutes", 0),
        PANIC_LIFE_SECONDS("panic_life.seconds", 3),
        PANIC_LIFE_RELOAD("panic_life.reload", 0),
        COLLECTION("collection", null),
        COLLECTION_HOURS("collection.hours", 0),
        COLLECTION_MINUTES("collection.minutes", 0),
        COLLECTION_SECONDS("collection.seconds", 15),
        COLLECTION_RELOAD("collection.reload", 0),
        CORPSE_TESTER("corpse_tester", null),
        CORPSE_TESTER_HOURS("corpse_tester.hours", 0),
        CORPSE_TESTER_MINUTES("corpse_tester.minutes", 0),
        CORPSE_TESTER_SECONDS("corpse_tester.seconds", 10),
        CORPSE_TESTER_RELOAD("corpse_tester.reload", 0),
        MIRROR_ILLUSION("mirror_illusion", null),
        MIRROR_ILLUSION_HOURS("mirror_illusion.hours", 0),
        MIRROR_ILLUSION_MINUTES("mirror_illusion.minutes", 0),
        MIRROR_ILLUSION_SECONDS("mirror_illusion.seconds", 30),
        MIRROR_ILLUSION_RELOAD("mirror_illusion.reload", 0),
        TEST_CORPSE_RANGE("test_corpse_range", 10, "range in blocks player has to be while testing the corpse"),
        MAX_PANIC_BLOCKS("max_panic_blocks", 5),
        DEFAULT_KARMA("default_karma", 200),
        MAX_KARMA("max_karma", 10000),
        CHESTS_PER_PLAYER("chests_per_player", 25, "the amount of kept on the map for each player"),
        AUTO_JOIN("auto_join", true, "Should players automatically join the game when they join the server"),
        KARMA_BAN_THRESHOLD("karma_ban_threshold", 0, "Minimum karma before player is banned"),
        KARMA_BAN_COMMAND("karma_ban_command", "ban %player% Karma threshold reached 1w", "command to run when player reaches karma ban threshold");

        private final String key;
        private final Object defaultValue;
        private final String[] comments;
        private Object value = null;

        Setting(String key, Object defaultValue, String... comments) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.comments = comments != null ? comments : new String[0];
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public Object getDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public String[] getComments() {
            return this.comments;
        }

        @Override
        public Object getCachedValue() {
            return this.value;
        }

        @Override
        public void setCachedValue(Object value) {
            this.value = value;
        }

        @Override
        public CommentedFileConfiguration getBaseConfig() {
            return Main.getInstance().getManager(ConfigManager.class).getConfig();
        }
    }

    public ConfigManager(RosePlugin rosePlugin) {
        super(rosePlugin, Setting.class);
    }
    @Override
    protected String[] getHeader() {
        return new String[0];
    }
}

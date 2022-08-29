package ml.sabotage.locale;

import dev.rosewood.rosegarden.locale.Locale;

import java.util.LinkedHashMap;
import java.util.Map;

public class EnglishLocale implements Locale {
    @Override
    public String getLocaleName() {
        return "en_US";
    }

    @Override
    public String getTranslatorName() {
        return "AkiraDev";
    }

    @Override
    public Map<String, Object> getDefaultLocaleValues() {
        return new LinkedHashMap<>() {{
            this.put("#0", "Plugin Message Prefix");
            this.put("prefix", "&3[SabotageX] &7- ");

            this.put("#1", "Blood messages");
            this.put("blood-0", "&bThis player is clean");
            this.put("blood-1", "&bYou notice a few drops of blood, but nothing to be suspicious about");
            this.put("blood-2", "&bYou notice blood on their sleeve. Maybe you should keep an eye out...");
            this.put("blood-3", "&bThis player is covered in blood - Did they just kill someone?");
            this.put("blood-4", "&bThey're drenched in blood - How many people have they killed?");

            this.put("#2", "Command messages");
            this.put("must-be-player", "&cYou must be a player to use this command");
            this.put("only-in-lobby", "&cYou must be in the lobby to do this!");
            this.put("not-in-lobby", "&cThis cannot be done while in the lobby!");
            this.put("must-be-alive", "&cYou must be alive to do this!");

            this.put("#3", "Command Descriptions");
            this.put("command-help-description", "Shows this message");
            this.put("command-reload-description", "Reloads the plugin");
            this.put("command-karma-description", "Karma Command");
            this.put("command-karma-add-description", "Add karma to a player");
            this.put("command-karma-set-description", "Set a players karma to a specific amount");
            this.put("command-karma-get-description", "Get a players karma");
            this.put("command-karma-reset-description", "Reset a players karma to the configured default");
            this.put("command-sabotage-start-description", "Start a Sabotage game");
            this.put("command-sabotage-stop-description", "Stop the Sabotage game");
            this.put("command-sabotage-refill-description", "Refill the chests in the Sabotage game");
            this.put("command-sabotage-test-description", "Enable/Disable test mode");
            this.put("command-sabotage-pause-description", "Pause the Sabotage game");
            this.put("command-sabotage-resurrect-description", "Resurrect a player");
            this.put("command-sabotage-build-description", "Toggle building mode");
            this.put("command-shop-description", "Shop Command");
            this.put("command-map-vote-description", "Vote for a map");

            this.put("#4", "Sabotage command messages");
            this.put("dev-on", "&aDeveloper mode enabled");
            this.put("dev-off", "&cDeveloper mode disabled.");
            this.put("build-on", "&aBuilder mode enabled.");
            this.put("build-off", "&cBuilder mode disabled.");
            this.put("paused", "&cPaused.");
            this.put("unpaused", "&aUnpaused.");
            this.put("game-ended", "&aGame ended.");
            this.put("chest-refilled", "&aChests refilled.");
            this.put("invalid-player", "&cInvalid player!");
            this.put("invalid-map", "&cInvalid Map!");
            this.put("already-alive", "&cPlayer is already alive!");
            this.put("already-in-lobby", "&cYou're already in the lobby!");


            this.put("#5", "Karma command messages");
            this.put("your-karma", "You have %karma%");
            this.put("karma-added", "%player% has had %karma% added");
            this.put("karma-set", "%player% has had their karma set to %karma%");
            this.put("karma-get", "%player%'s karma is %karma%");
            this.put("karma-reset", "%player%'s karma has been reset");

            this.put("#6", "Base command messages");
            this.put("base-command-help", "&3/%cmd% help &7- &aFor a list of commands");
            this.put("base-command-color", "&e");

        }};
    }
}

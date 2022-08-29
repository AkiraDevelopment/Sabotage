package ml.sabotage.command.karma;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.command.BaseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import ml.sabotage.Main;
import ml.sabotage.game.managers.ConfigManager;
import ml.sabotage.game.managers.DataManager;
import ml.sabotage.game.managers.LocaleManager;
import org.bukkit.entity.Player;

public class CommandKarma extends BaseCommand {

    public CommandKarma(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context){
        final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);

        if (!(context.getSender() instanceof Player)) {
            locale.sendMessage(context.getSender(), "must-be-player");
            return;
        }
        locale.sendCommandMessage(context.getSender(), "your-karma", StringPlaceholders.builder("player", context.getSender().getName()).addPlaceholder("karma", Main.getInstance().getManager(DataManager.class).getPlayerData(((Player) context.getSender()).getPlayer().getUniqueId()).getKarma()).build());
    }

    public static class CommandAdd extends RoseCommand {


        public CommandAdd(RosePlugin rosePlugin, RoseCommandWrapper parent) {
            super(rosePlugin, parent);
        }

        @RoseExecutable
        public void execute(CommandContext context, Player player, int karma){
            final DataManager dataManager = Main.getInstance().getManager(DataManager.class);
            final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
            dataManager.getPlayerData(player.getUniqueId()).addKarma(karma);
            locale.sendCommandMessage(context.getSender(), "karma-added", StringPlaceholders.builder("player", player.getName()).addPlaceholder("karma", karma).build());
        }

        @Override
        protected String getDefaultName() {
            return "add";
        }

        @Override
        public String getDescriptionKey() {
            return "command-karma-add-description";
        }

        @Override
        public String getRequiredPermission(){
            return "zerodasho.sabotage.add";
        }
    }

    public static class CommandSet extends RoseCommand {

        public CommandSet(RosePlugin rosePlugin, RoseCommandWrapper parent) {
            super(rosePlugin, parent);
        }

        @RoseExecutable
        public void execute(CommandContext context, Player player, int karma){
            final DataManager dataManager = Main.getInstance().getManager(DataManager.class);
            final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
            dataManager.getPlayerData(player.getUniqueId()).setKarma(karma);
            locale.sendCommandMessage(context.getSender(), "karma-set", StringPlaceholders.builder("player", player.getName()).addPlaceholder("karma", karma).build());
        }
        @Override
        protected String getDefaultName() {
            return "set";
        }

        @Override
        public String getDescriptionKey() {
            return "command-karma-set-description";
        }

        @Override
        public String getRequiredPermission(){
            return "zerodasho.sabotage.set";
        }
    }

    public static class CommandGet extends RoseCommand {

        public CommandGet(RosePlugin rosePlugin, RoseCommandWrapper parent) {
            super(rosePlugin, parent);
        }
        @RoseExecutable
        public void execute(CommandContext context, Player player){
            final DataManager dataManager = Main.getInstance().getManager(DataManager.class);
            final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
            locale.sendCommandMessage(context.getSender(), "karma-get", StringPlaceholders.builder("player", player.getName()).addPlaceholder("karma", dataManager.getPlayerData(player.getUniqueId()).getKarma()).build());
        }
        @Override
        protected String getDefaultName() {
            return "get";
        }

        @Override
        public String getDescriptionKey() {
            return "command-karma-get-description";
        }

        @Override
        public String getRequiredPermission(){
            return "zerodasho.sabotage.get";
        }
    }

    public static class CommandReset extends RoseCommand {

            public CommandReset(RosePlugin rosePlugin, RoseCommandWrapper parent) {
                super(rosePlugin, parent);
            }
            @RoseExecutable
            public void execute(CommandContext context, Player player){
                final DataManager dataManager = Main.getInstance().getManager(DataManager.class);
                final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
                dataManager.getPlayerData(player.getUniqueId()).setKarma(ConfigManager.Setting.DEFAULT_KARMA.getInt());
                locale.sendCommandMessage(context.getSender(), "karma-reset", StringPlaceholders.builder("player", player.getName()).build());
            }
            @Override
            protected String getDefaultName() {
                return "reset";
            }

        @Override
        public String getDescriptionKey() {
            return "command-karma-reset-description";
        }

        @Override
            public String getRequiredPermission(){
                return "zerodasho.sabotage.reset";
            }
    }

    @Override
    public String getDescriptionKey() {
        return "command-karma-description";
    }

    @Override
    public String getRequiredPermission() {
        return "zerodasho.sabotage.karma";
    }
}

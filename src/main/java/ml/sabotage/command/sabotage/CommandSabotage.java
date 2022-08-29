package ml.sabotage.command.sabotage;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.Optional;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import ml.sabotage.Main;
import ml.sabotage.game.managers.*;
import ml.sabotage.game.stages.Sabotage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CommandSabotage {

    public static boolean TEST, PAUSE;

    public static class CommandStart extends RoseCommand {

        public CommandStart(RosePlugin rosePlugin, RoseCommandWrapper parent) {
            super(rosePlugin, parent);
        }

        @RoseExecutable
        public void execute(CommandContext context, @Optional String selectedMap){
            VoteManager voteManager = this.rosePlugin.getManager(VoteManager.class);
            LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
            String option = selectedMap == null ?
                    MapManager.getMap() :
                    ConfigManager.Setting.MAPS.getStringList().stream().filter(map -> map.startsWith(selectedMap)).findFirst().get();
            if(option != null) {
                voteManager.VOTES.clear();
                voteManager.VOTES.put(UUID.randomUUID(), option);
                Main.sabotage.endLobby();
            }

            else
                locale.sendCommandMessage(context.getSender(), "invalid-map");
        }

        @Override
        protected String getDefaultName() {
            return "start";
        }

        @Override
        public String getDescriptionKey() {
            return "command-sabotage-start-description";
        }

        @Override
        public String getRequiredPermission(){
            return "zerodasho.sabotage.generic.start";
        }
    }
    public static class CommandStop extends RoseCommand {

        public CommandStop(RosePlugin rosePlugin, RoseCommandWrapper parent) {
            super(rosePlugin, parent);
        }

        @RoseExecutable
        public void execute(CommandContext context){
            LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
            Sabotage sabotage = Main.sabotage;
            if(sabotage.getCurrent_state() == Sabotage.LOBBY) {
                locale.sendCommandMessage(context.getSender(), "already-in-lobby");
                return;
            }
            if(sabotage.getCurrent_state() == Sabotage.COLLECTION)
                sabotage.endCollection();

            if(sabotage.getCurrent_state() == Sabotage.INGAME)
                sabotage.endIngame();

            locale.sendCommandMessage(context.getSender(), "game-ended");
        }

        @Override
        protected String getDefaultName() {
            return "Stop";
        }

        @Override
        public String getDescriptionKey() {
            return "command-sabotage-stop-description";
        }

        @Override
        public String getRequiredPermission() {
            return "zerodasho.sabotage.generic.stop";
        }
    }
    public static class CommandRefill extends RoseCommand {

        public CommandRefill(RosePlugin rosePlugin, RoseCommandWrapper parent) {
            super(rosePlugin, parent);
        }

        @RoseExecutable
        public void execute(CommandContext context){
            LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
            Sabotage sabotage = Main.sabotage;
            if(sabotage.getCurrent_state() != Sabotage.LOBBY) {
                sabotage.getCollection().getMapManager().refill();
                locale.sendCommandMessage(context.getSender(), "chest-refilled");
            }
            else
                locale.sendCommandMessage(context.getSender(), "not-in-lobby");
        }

        @Override
        protected String getDefaultName() {
            return "refill";
        }

        @Override
        public String getDescriptionKey() {
            return "command-sabotage-refill-description";
        }

        @Override
        public String getRequiredPermission() {
            return "zerodasho.sabotage.generic.test";
        }
    }
    public static class CommandTest extends RoseCommand {

        public CommandTest(RosePlugin rosePlugin, RoseCommandWrapper parent) {
            super(rosePlugin, parent);
        }
        @RoseExecutable
        public void execute(CommandContext context, @Optional Boolean test){
            LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
            TEST = Objects.requireNonNullElseGet(test, () -> !TEST);
            locale.sendCommandMessage(context.getSender(), TEST ? "dev-on" : "dev-off");
        }
        @Override
        protected String getDefaultName() {
            return "test";
        }
        @Override
        public String getDescriptionKey() {
            return "command-sabotage-test-description";
        }
        @Override
        public String getRequiredPermission() {
            return "zerodasho.sabotage.generic.test";
        }
    }
    public static class CommandPause extends RoseCommand {

        public CommandPause(RosePlugin rosePlugin, RoseCommandWrapper parent) {
            super(rosePlugin, parent);
        }
        @RoseExecutable
        public void execute(CommandContext context, @Optional Boolean pause){
            LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
            PAUSE = Objects.requireNonNullElseGet(pause, () -> !PAUSE);
            locale.sendCommandMessage(context.getSender(), PAUSE ? "paused" : "unpaused");
        }
        @Override
        protected String getDefaultName() {
            return "pause";
        }
        @Override
        public String getDescriptionKey() {
            return "command-sabotage-pause-description";
        }
        @Override
        public String getRequiredPermission() {
            return "zerodasho.sabotage.generic.pause";
        }
    }
    public static class CommandResurrect extends RoseCommand {

        public CommandResurrect(RosePlugin rosePlugin, RoseCommandWrapper parent) {
            super(rosePlugin, parent);
        }

        @RoseExecutable
        public void execute(CommandContext context, Player player){

            PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
            LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
            if(Main.sabotage.getCurrent_state() != Sabotage.INGAME) {
                locale.sendCommandMessage(context.getSender(), "not-in-lobby");
                return;
            }

            if(playerManager.isAlive(player.getUniqueId())) {
                locale.sendCommandMessage(context.getSender(), "already-alive");
                return;
            }

            playerManager.resurrect(player, false);
        }

        @Override
        protected String getDefaultName() {
            return "resurrect";
        }

        @Override
        public String getDescriptionKey() {
            return "command-sabotage-resurrect-description";
        }

        @Override
        public String getRequiredPermission() {
            return "zerodasho.sabotage.generic.resurrect";
        }
    }
    public static class CommandBuild extends RoseCommand{

        public CommandBuild(RosePlugin rosePlugin, RoseCommandWrapper parent) {
            super(rosePlugin, parent);
        }

        @RoseExecutable
        public void execute(CommandContext context, @Optional Boolean build){
            List<UUID> players = Main.getInstance().canBuildPlayers;
            LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
            if(build == null){
                if(players.contains(Bukkit.getPlayer(context.getSender().getName()).getUniqueId())) {
                    players.remove(Bukkit.getPlayer(context.getSender().getName()).getUniqueId());
                    locale.sendCommandMessage(context.getSender(), "build-off");
                }else{
                    players.add(Bukkit.getPlayer(context.getSender().getName()).getUniqueId());
                    locale.sendCommandMessage(context.getSender(), "build-on");
                }
            }

            if(build) {
                players.add(Bukkit.getPlayer(context.getSender().getName()).getUniqueId());
                locale.sendCommandMessage(context.getSender(), "build-on");
            }else{
                players.remove(Bukkit.getPlayer(context.getSender().getName()).getUniqueId());
                locale.sendCommandMessage(context.getSender(), "build-off");
            }

        }

        @Override
        protected String getDefaultName() {
            return "build";
        }

        @Override
        public String getDescriptionKey() {
            return "command-sabotage-build-description";
        }

        @Override
        public String getRequiredPermission() {
            return "zerodasho.sabotage.attributes.build";
        }
    }
    public static class CommandBruh extends RoseCommand {

        public CommandBruh(RosePlugin rosePlugin, RoseCommandWrapper parent) {
            super(rosePlugin, parent);
        }

        @RoseExecutable
        public void execute(CommandContext context){
            PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
            System.out.println(playerManager.getRole((Bukkit.getPlayer(context.getSender().getName()).getUniqueId())).getRole());
        }

        @Override
        protected String getDefaultName() {
            return "Bruh";
        }

        @Override
        public String getDescriptionKey() {
            return "Bruh";
        }

        @Override
        public String getRequiredPermission() {
            return null;
        }
    }
}

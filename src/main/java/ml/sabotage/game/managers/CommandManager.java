package ml.sabotage.game.managers;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.manager.AbstractCommandManager;
import ml.sabotage.command.wrappers.KarmaCommandWrapper;
import ml.sabotage.command.wrappers.MapCommandWrapper;
import ml.sabotage.command.wrappers.SabotageCommandWrapper;
import ml.sabotage.command.wrappers.ShopCommandWrapper;

import java.util.List;

public class CommandManager extends AbstractCommandManager {

    public CommandManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public List<Class<? extends RoseCommandWrapper>> getRootCommands() {
        return List.of(SabotageCommandWrapper.class, KarmaCommandWrapper.class, MapCommandWrapper.class, ShopCommandWrapper.class);
    }

    @Override
    public List<String> getArgumentHandlerPackages() {
        return List.of("ml.sabotage.command.argument");
    }
}

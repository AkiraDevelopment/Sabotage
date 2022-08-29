package ml.sabotage.command.wrappers;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;

import java.util.List;

public class MapCommandWrapper extends RoseCommandWrapper {

    public MapCommandWrapper(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public String getDefaultName() {
        return "map";
    }

    @Override
    public List<String> getDefaultAliases() {
        return List.of();
    }

    @Override
    public List<String> getCommandPackages() {
        return List.of("ml.sabotage.command.map");
    }

    @Override
    public boolean includeBaseCommand() {
        return true;
    }

    @Override
    public boolean includeHelpCommand() {
        return true;
    }

    @Override
    public boolean includeReloadCommand() {
        return false;
    }
}

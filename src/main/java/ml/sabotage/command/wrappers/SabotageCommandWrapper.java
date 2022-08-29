package ml.sabotage.command.wrappers;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;

import java.util.List;

public class SabotageCommandWrapper extends RoseCommandWrapper {

    public SabotageCommandWrapper(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public String getDefaultName() {
        return "sabotage";
    }

    @Override
    public List<String> getDefaultAliases() {
        return List.of("sabotage", "sab");
    }

    @Override
    public List<String> getCommandPackages() {
        return List.of("ml.sabotage.command.sabotage");
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
        return true;
    }
}

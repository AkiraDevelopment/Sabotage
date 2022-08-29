package ml.sabotage.command.sabotage;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import ml.sabotage.game.managers.LocaleManager;

public class CommandSabImport extends RoseCommand {

    public CommandSabImport(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context) {
        final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);
    }

    @Override
    protected String getDefaultName() {
        return "import";
    }

    @Override
    public String getDescriptionKey() {
        return "command-sabotage-import-description";
    }

    @Override
    public String getRequiredPermission() {
        return "zerodasho.sabotage.generic.test";
    }
}

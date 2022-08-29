package ml.sabotage.command.map;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.Optional;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import ml.sabotage.game.managers.VoteManager;
import org.bukkit.entity.Player;

public class CommandVote extends RoseCommand {

    public CommandVote(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, @Optional Integer vote) {
        VoteManager voteManager = this.rosePlugin.getManager(VoteManager.class);
        if(vote == null || !(context.getSender() instanceof Player player)) {
            voteManager.listVotes(context.getSender());
        }else{
            voteManager.vote(player, vote);
        }


    }

    @Override
    protected String getDefaultName() {
        return "vote";
    }

    @Override
    public String getDescriptionKey() {
        return "command-map-vote-description";
    }

    @Override
    public String getRequiredPermission() {
        return null;
    }
}

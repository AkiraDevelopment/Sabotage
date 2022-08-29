package ml.sabotage.command.shop;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.command.BaseCommand;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.Optional;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import ml.sabotage.Main;
import ml.sabotage.game.managers.LocaleManager;
import ml.sabotage.game.managers.PlayerManager;
import ml.sabotage.game.roles.IngamePlayer;
import ml.sabotage.game.stages.Sabotage;
import ml.zer0dasho.plumber.utils.Sprink;
import org.bukkit.entity.Player;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CommandShop extends BaseCommand {

    public CommandShop(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, @Optional Integer Item) throws Exception {
        final LocaleManager locale = this.rosePlugin.getManager(LocaleManager.class);

        if (!(context.getSender() instanceof Player player)) {
            locale.sendMessage(context.getSender(), "must-be-player");
            return;
        }
        IngamePlayer ingamePlayer = inIngame(player);
        if(ingamePlayer == null) {
            locale.sendMessage(context.getSender(), "must-be-ingame");
            return;
        }
        if(Item != null) {
            List<Method> shop = getAnnotatedMethods(ingamePlayer.getClass());
            shop.get(Item - 1).invoke(ingamePlayer);
        }else{
            List<Method> shop = getAnnotatedMethods(ingamePlayer.getClass());

            player.sendMessage(Sprink.color("&c&m--------&r &eShop &c&m---------"));
            shop.forEach(method -> {
                int index = shop.indexOf(method) + 1;
                String name = method.getName().replaceAll("_", " ");
                player.sendMessage(Sprink.color("&3    ") + index + ". " + name);
            });
            player.sendMessage(Sprink.color("&c&m----------------------"));
        }
    }

    @Override
    public String getDescriptionKey() {
        return "command-shop-description";
    }

    @Override
    public String getRequiredPermission() {
        return "zerodasho.sabotage.shop";
    }

    private static List<Method> getAnnotatedMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();

        for(Method method : clazz.getMethods()) {
            Annotation annotation1 = method.getAnnotation((Class<? extends Annotation>) ml.sabotage.game.roles.SHOP.class);
            if(annotation1 != null) methods.add(method);
        }

        return methods;
    }

    private static IngamePlayer inIngame(Player player) {
        LocaleManager locale = Main.getInstance().getManager(LocaleManager.class);
        PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
        if(Main.sabotage.getCurrent_state() != Sabotage.INGAME) {
            locale.sendMessage(player, "not-in-lobby");
            return null;
        }

        IngamePlayer alive = playerManager.getRole(player.getUniqueId());
        if(alive == null)
            locale.sendMessage(player, "must-be-alive");

        return alive;
    }

}


package ml.sabotage.game.roles;

import java.util.Set;

import ml.sabotage.Main;
import ml.sabotage.command.sabotage.CommandSabotage;
import ml.sabotage.game.managers.DataManager;
import ml.sabotage.game.managers.LocaleManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Sets;

import ml.sabotage.PlayerData;
import ml.sabotage.game.SabPlayer;
import ml.sabotage.game.tasks.Panic;
import ml.zer0dasho.plumber.utils.Sprink;

public abstract class IngamePlayer {

    public double blood;
    public boolean timeout;
    
    private final Set<Panic> panics;

    public final Player player;
    public final SabPlayer sabPlayer;

    public IngamePlayer(SabPlayer sabPlayer) {
        this.sabPlayer = sabPlayer;
        this.player = sabPlayer.player;
        this.panics = Sets.newHashSet();
    }

    public String getBloodMessage() {
        LocaleManager localeManager = Main.getInstance().getManager(LocaleManager.class);
    	String result = switch ((int) blood) {
            case 0, 1, 2, 3 -> localeManager.getLocaleMessage("blood-" + (int)blood);
            default -> localeManager.getLocaleMessage("blood-4");
        };

        return Sprink.color(result);
    }

    public void kill(IngamePlayer victim) {
        DataManager dataManager = Main.getInstance().getManager(DataManager.class);
    	if(victim == null) return;
    	
        int delta = determineKarma(victim);
        this.sabPlayer.addKarma(delta);
        
        PlayerData data = dataManager.getPlayerData(victim.player.getUniqueId());
        if(delta < 0){
            data.addIncorrectKill();
        }else{
            data.addCorrectKill();
        }
        data.addKill();
        data.save();  
    }

    public void die(IngamePlayer killer) {
        DataManager dataManager = Main.getInstance().getManager(DataManager.class);
    	PlayerData data = dataManager.getPlayerData(this.player.getUniqueId());
        if(killer != null) {
            killer.determineKarma(this);
            data.addDeath();
        }
        sabPlayer.addKarma(karmaOnDeath());
        data.save();
    }
    
    public abstract String getRole();
    public abstract void sendRoleMessage(Detective detective);

    public abstract int karmaOnDeath();
    public abstract int determineKarma(IngamePlayer ingamePlayer);
    
    public boolean hasKarma(int karma) {
        DataManager dataManager = Main.getInstance().getManager(DataManager.class);
        PlayerData data = dataManager.getPlayerData(player.getUniqueId());
    	if(CommandSabotage.TEST || data.getKarma() >= karma)
    		return true;
    	
        sabPlayer.player.sendMessage(Sprink.color("&cYou don't have enough karma..."));
        return false;
    }
    
    @SHOP
    public void Compass_() {
        if (hasKarma(20)) {
            player.getInventory().addItem(new ItemStack(Material.COMPASS));
            player.sendMessage(Sprink.color("&aYou just bought a Compass!"));
            sabPlayer.addKarma(-20);
        }
    }

    @SHOP
    public void Panic_Kit() {
        if (hasKarma(40)) {
            player.getInventory().addItem(new ItemStack(Material.OAK_LEAVES, 5));
            player.sendMessage(Sprink.color("&aYou just bought a Panic Kit!"));
            sabPlayer.addKarma(-40);
        }
    }
    
    /* Getters */
    
    public int getPanics() {
    	return this.panics.size();
    }
    
    public void removePanic(Panic panic) {
    	this.panics.remove(panic);
    }
    
    public void addPanic(Panic panic) {
    	this.panics.add(panic);
    }   
}

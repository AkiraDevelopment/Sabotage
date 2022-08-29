package ml.sabotage.game.tasks;

import ml.sabotage.game.managers.ConfigManager;
import ml.sabotage.utils.SabUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import ml.sabotage.game.roles.IngamePlayer;
import ml.zer0dasho.plumber.game.Timer;

public class Panic extends BukkitRunnable {
	
    public final Timer life;
    public final IngamePlayer ingamePlayer;
    
    private final Location location;

    public Panic(IngamePlayer ingamePlayer, Location location) {
    	
    	this.life = SabUtils.makeTimer(ConfigManager.Setting.PANIC_LIFE_HOURS.getInt(), ConfigManager.Setting.PANIC_LIFE_MINUTES.getInt(), ConfigManager.Setting.PANIC_LIFE_SECONDS.getInt());
        this.ingamePlayer = ingamePlayer;
        this.location = location;
        
        location.getBlock().setType(Material.OAK_LEAVES);
        
        if(ingamePlayer.getPanics() >= ConfigManager.Setting.MAX_PANIC_BLOCKS.getInt())
			ingamePlayer.timeout = true;
        
        ingamePlayer.addPanic(this);
    }

    @Override
    public void run() {
        if(life.tick())
            this.stop();
    }
    
    public void stop() {
    	ingamePlayer.removePanic(this);
        location.getBlock().setType(Material.AIR);
        this.cancel();
    }
}
package ml.sabotage.game.tasks;

import ml.sabotage.game.managers.ConfigManager;
import ml.sabotage.game.managers.PlayerManager;
import ml.sabotage.utils.SabUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import ml.sabotage.Main;
import ml.sabotage.game.events.TestCorpseEvent;
import ml.sabotage.game.roles.Detective;
import ml.sabotage.game.roles.IngamePlayer;
import ml.sabotage.game.stages.Ingame;
import ml.zer0dasho.plumber.game.Timer;
import ml.zer0dasho.plumber.utils.Sprink;

public class TestCorpse extends BukkitRunnable {

	public Ingame ingame;
	public IngamePlayer corpse;
	public Location location;
	public Timer timer;
	
	public Detective detective;
	
	public TestCorpse(Ingame ingame, IngamePlayer corpse, Location location) {
		PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
		this.ingame = ingame;
		this.corpse = corpse;
		this.location = location;
		this.timer = SabUtils.makeTimer(ConfigManager.Setting.CORPSE_TESTER_HOURS.getInt(), ConfigManager.Setting.CORPSE_TESTER_MINUTES.getInt(), ConfigManager.Setting.CORPSE_TESTER_SECONDS.getInt());
		this.detective = playerManager.getDetective();
		
		Main.sabotage.broadcastAll(Sprink.color("&9" + detective.player.getName() + " &eis now testing the body of &7" + corpse.player.getName()));
	}

	@Override
	public void run() {
		Player player = detective.player;
		
		if(ConfigManager.Setting.TEST_CORPSE_RANGE.getInt() != -1 && player.getLocation().distance(location) > ConfigManager.Setting.TEST_CORPSE_RANGE.getInt()) {
			player.sendMessage(Sprink.color("&cYou need to stand by the corpse for " + ConfigManager.Setting.CORPSE_TESTER_SECONDS.getInt() + " seconds!"));
			Bukkit.getPluginManager().callEvent(new TestCorpseEvent(this));
			this.cancel();
		}
		
		else if(timer.tick()) {
			Main.sabotage.broadcastAll(Sprink.color("&7" + corpse.player.getName() + " was " + corpse.getRole()));
			Bukkit.getPluginManager().callEvent(new TestCorpseEvent(this));
			this.cancel();
		}
	}

	
	
}

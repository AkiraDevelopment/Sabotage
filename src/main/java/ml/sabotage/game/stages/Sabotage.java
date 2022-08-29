package ml.sabotage.game.stages;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import ml.sabotage.game.managers.ConfigManager;
import ml.sabotage.game.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.google.common.collect.Sets;

import ml.sabotage.Main;
import ml.sabotage.game.SabPlayer;
import ml.zer0dasho.plumber.utils.Sprink;
import ml.zer0dasho.plumber.utils.Trycat;

public class Sabotage extends BukkitRunnable implements Listener {
	
	Set<UUID> players;
	Lobby lobby;
	Collection collection;
	Ingame ingame;
	
	private int current_state;

	public Sabotage() {
		this.players = Sets.newHashSet();
		
		this.lobby = new Lobby(this);
		Trycat.Try(() -> lobby.start(), Throwable::printStackTrace);
		
		this.runTaskTimer(Main.getInstance(), 0L, 20L);
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
	}
	
	@Override
	public void run() {
		switch (this.current_state) {
			case LOBBY -> lobby.run();
			case COLLECTION -> collection.run();
			case INGAME -> ingame.run();
		}
	}	
	
	public void broadcastAll(String msg) {
		String colorMsg = Sprink.color(msg);
		players.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(player -> player.sendMessage(colorMsg));
	}
	
	public void add(Player player) {
		PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
		if(players.contains(player.getUniqueId()))
			return;
		
		players.add(player.getUniqueId());

		switch (current_state) {
			case LOBBY -> lobby.add(player);
			case COLLECTION -> collection.add(player);
			case INGAME -> playerManager.smite(player);
		}
	}
	
	public void remove(Player player) {
		PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
		players.remove(player.getUniqueId());

		switch (current_state) {
			case LOBBY -> lobby.remove(player);
			case COLLECTION -> collection.remove(player);
			case INGAME -> playerManager.smite(player);
		}
		
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
	}
	
	public void endLobby() {
		if(this.current_state == LOBBY) {
			this.lobby.stop();
			this.current_state = COLLECTION;
			
			this.collection = new Collection(this);
			Trycat.Try(() -> collection.start(), Throwable::printStackTrace);
		}
	}
	
	public void endCollection() {
		if(this.current_state == COLLECTION) {
			this.collection.stop();
			this.current_state = INGAME;
			
			this.ingame = new Ingame(this);
			Trycat.Try(() -> ingame.start(), Throwable::printStackTrace);
		}
	}
	
	public void endIngame() {
		if(this.current_state == INGAME) {
			this.ingame.stop();
			this.current_state = LOBBY;
			
			this.lobby = new Lobby(this);
			Trycat.Try(() -> lobby.start(), Throwable::printStackTrace);
		}
	}
	
	/* Events */
		
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		SabPlayer sabPlayer = new SabPlayer(e.getPlayer());
		Main.SAB_PLAYERS.put(e.getPlayer().getUniqueId(), sabPlayer);
		
		if(ConfigManager.Setting.AUTO_JOIN.getBoolean())
			add(e.getPlayer());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Main.SAB_PLAYERS.remove(e.getPlayer().getUniqueId());
		
		if(players.contains(e.getPlayer().getUniqueId()))
			remove(e.getPlayer());
	}
	
	@EventHandler
	public void onHunger(FoodLevelChangeEvent e) {
		if(!players.contains(e.getEntity().getUniqueId()))
			return;
		
		if(e.getEntity().hasPermission("zerodasho.sabotage.attributes.nohunger"))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent e) {
		if(!players.contains(e.getWhoClicked().getUniqueId()))
			return;
		
		if(!e.getWhoClicked().hasPermission("zerodasho.sabotage.attributes.craft"))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onBuild(BlockBreakEvent e) {
		if(!players.contains(e.getPlayer().getUniqueId()))
			return;
		
		if(!e.getPlayer().hasPermission("zerodasho.sabotage.attributes.build") || !Main.getInstance().canBuildPlayers.contains(e.getPlayer().getUniqueId()))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onFish(PlayerFishEvent e) {
		if(!players.contains(e.getPlayer().getUniqueId()))
			return;
		
		if(e.getCaught() == null) return;
		
		Location location = e.getPlayer().getLocation();
		if(location.distance(e.getCaught().getLocation()) < 12.0)
			pullEntityToLocation(e.getCaught(), location);	
	}
	
	private void pullEntityToLocation(Entity e, Location loc) {
        Location entityLoc = e.getLocation();
        entityLoc.setY(entityLoc.getY() + 0.5);
        e.teleport(entityLoc);
        double g = -0.08;
        double t = loc.distance(entityLoc);
        double v_x = (1.0 + 0.07 * t) * (loc.getX() - entityLoc.getX()) / t;
        double v_y = (1.0 + 0.03 * t) * (loc.getY() - entityLoc.getY()) / t - 0.5 * g * t;
        double v_z = (1.0 + 0.07 * t) * (loc.getZ() - entityLoc.getZ()) / t;
        Vector v = e.getVelocity();
        v.setX(v_x);
        v.setY(v_y);
        v.setZ(v_z);
        e.setVelocity(v);
    }
	
	/* Getters */
	
	public Set<UUID> getPlayers() {
		return Collections.unmodifiableSet(this.players);
	}
	
	public int getCurrent_state() {
		return this.current_state;
	}
	
	public Lobby getLobby() {
		return this.lobby;
	}

	public Collection getCollection() {
		return this.collection;
	}

	public Ingame getIngame() {
		return this.ingame;
	}
	
	public static final int LOBBY = 0, COLLECTION = 1, INGAME = 2;
}
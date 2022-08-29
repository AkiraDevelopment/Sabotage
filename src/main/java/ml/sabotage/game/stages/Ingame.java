package ml.sabotage.game.stages;

import java.util.List;

import dev.rosewood.rosegarden.utils.HexUtils;
import github.scarsz.discordsrv.DiscordSRV;
import me.clip.placeholderapi.PlaceholderAPI;
import ml.sabotage.command.sabotage.CommandSabotage;
import ml.sabotage.game.managers.ConfigManager;
import ml.sabotage.game.managers.DataManager;
import ml.sabotage.utils.SabUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import ml.sabotage.Main;
import ml.sabotage.PlayerData;
import ml.sabotage.game.SharedListener;
import ml.sabotage.game.events.ResurrectionEvent;
import ml.sabotage.game.events.SmiteEvent;
import ml.sabotage.game.events.TestCorpseEvent;
import ml.sabotage.game.events.TesterEvent;
import ml.sabotage.game.gui.IngameGui;
import ml.sabotage.game.managers.MapManager;
import ml.sabotage.game.managers.PlayerManager;
import ml.sabotage.game.roles.Detective;
import ml.sabotage.game.roles.IngamePlayer;
import ml.sabotage.game.roles.Saboteur;
import ml.sabotage.game.tasks.Panic;
import ml.sabotage.game.tasks.TestCorpse;
import ml.sabotage.game.tasks.Tester;
import ml.zer0dasho.corpseimmortal.CorpseImmortal;
import ml.zer0dasho.corpseimmortal.events.CorpseOpenInventoryEvent;
import ml.zer0dasho.plumber.game.Timer;
import ml.zer0dasho.plumber.utils.Sprink;

public class Ingame implements Listener {

    Timer timer, refill;
    Tester tester;
    TestCorpse testCorpse;
    boolean rewarded;
    
    public final IngameGui GUI;
    
    private final Sabotage sabotage;
  
    public Ingame(Sabotage sabotage) {
    	this.sabotage = sabotage;
    	this.timer = SabUtils.makeTimer(ConfigManager.Setting.INGAME_HOURS.getInt(), ConfigManager.Setting.INGAME_MINUTES.getInt(), ConfigManager.Setting.INGAME_SECONDS.getInt());
    	this.refill = SabUtils.makeTimer(ConfigManager.Setting.REFILL_HOURS.getInt(), ConfigManager.Setting.REFILL_MINUTES.getInt(), ConfigManager.Setting.REFILL_SECONDS.getInt());
    	this.GUI = new IngameGui(timer);

    	refill.onFinish = () -> sabotage.collection.mapManager.refill();
    }
    
    void start() {
		PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
		playerManager.addPlayers(sabotage.collection.players);
    	this.rewarded = false;

    	this.initScoreboard();
    	
    	Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    	this.timer.reset();
    	this.refill.reset();
    }
    
    void stop() {
    	HandlerList.unregisterAll(this);
    }
    
    boolean run() {
    	if(!CommandSabotage.PAUSE) {
    		if(checkDeath() || GUI.getTimer().tick()) {
    			sabotage.endIngame();
    			return true;
    		}
    		
        	refill.tick();
        	GUI.update();
    	}
    	
    	return false;
    }
    
    /**
     * Returns all innocents including detectives.
     *
     * @return List<IngamePlayer>
	 */
    public List<IngamePlayer> innocents() {
		PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
    	List<IngamePlayer> innocents = playerManager.innocents(true);
    	innocents.addAll(playerManager.detectives(true));
    	return innocents;
    }
    
    private void innocentsWin() {
		PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
    	rewardWinners(innocents(), "&aInnocents win!");
        punishLosers(playerManager.saboteurs(true));
    }
    
    private void saboteursWin() {
		PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
    	rewardWinners(playerManager.saboteurs(true), "&cSaboteurs win!");
    	punishLosers(innocents());
    }
    
    private void rewardWinners(List<IngamePlayer> winners, String message) {
        sabotage.broadcastAll(message);
		DataManager dataManager = Main.getInstance().getManager(DataManager.class);
        for(IngamePlayer ingamePlayer : winners) {
        	PlayerData player = dataManager.getPlayerData(ingamePlayer.player.getUniqueId());
			player.addWin();
			player.save();
        }
        
        rewarded = true;
    }

    private void punishLosers(List<IngamePlayer> losers) {
		DataManager dataManager = Main.getInstance().getManager(DataManager.class);
        for (IngamePlayer ingamePlayer : losers) {
        	PlayerData player = dataManager.getPlayerData(ingamePlayer.player.getUniqueId());
            player.addLoss();
            player.save();
        }
    }
    
    private void revealSaboteurs() {
        sabotage.broadcastAll(Sprink.color("&cThe saboteurs were ") + getSaboteurList());
    }
    
    private String getSaboteurList() {
		PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
    	StringBuilder result = new StringBuilder();
    	List<String> saboteurs = playerManager.saboteurs(false).stream().map(igp -> igp.player.getName()).toList();
    	
    	for(int i = 0; i < saboteurs.size(); i++) {
    		if(i == saboteurs.size() - 1) {
    			if(saboteurs.size() != 1) result.append("and ");
    			result.append(saboteurs.get(i)).append(".");
    		}
    		else result.append(saboteurs.get(i)).append(", ");
    	}
    	
    	return result.toString();
    }

    private boolean checkDeath() {
		PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
    	if(CommandSabotage.TEST)
    		return false;
 
    	List<IngamePlayer> innocents = innocents();
    	List<IngamePlayer> saboteurs = playerManager.saboteurs(true);
    
    	if((innocents.size() == 0 || saboteurs.size() == 0) && !rewarded) {
    		if(innocents.size() == 0)
    			this.saboteursWin();
    		else
    			this.innocentsWin();

    		revealSaboteurs();
    		rewarded = true;
    		return true;
    	}  
        
        return false;
    }

	private void initScoreboard() {
		PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
		playerManager.innocents(false).forEach(igp -> {
			GUI.addPlayer(igp);

			GUI.getInnocent().getTeam("Innocent").addPlayer(igp.player);
			GUI.getSaboteur().getTeam("Innocent").addPlayer(igp.player);
			GUI.getDetective().getTeam("Innocent").addPlayer(igp.player);
			GUI.getSpectator().getTeam("Else").addPlayer(igp.player);
		});

		playerManager.saboteurs(false).forEach(igp -> {
			GUI.addPlayer(igp);

			GUI.getInnocent().getTeam("Innocent").addPlayer(igp.player);
			GUI.getSaboteur().getTeam("Saboteur").addPlayer(igp.player);
			GUI.getDetective().getTeam("Innocent").addPlayer(igp.player);
			GUI.getSpectator().getTeam("Else").addPlayer(igp.player);
		});

		if(playerManager.getDetective() != null) {
			GUI.addPlayer(playerManager.getDetective());

			GUI.getInnocent().getTeam("Detective").addPlayer(playerManager.getDetective().player);
			GUI.getSaboteur().getTeam("Detective").addPlayer(playerManager.getDetective().player);
			GUI.getDetective().getTeam("Detective").addPlayer(playerManager.getDetective().player);
			GUI.getSpectator().getTeam("Detective").addPlayer(playerManager.getDetective().player);
		}
	}
    
    /* Events */
    
    @EventHandler
    public void onSmite(SmiteEvent event) {
    	Player player = event.getPlayer();
    	player.setHealth(20.0);
    	player.setGameMode(GameMode.SPECTATOR);
    	player.teleport(this.getWorld().getSpawnLocation());
    	GUI.addSpectator(player);
    }
    
    @EventHandler
    public void onResurrection(ResurrectionEvent event) {
    	IngamePlayer ingamePlayer = event.getPlayer();
    	Player player = ingamePlayer.player;

    	GUI.addPlayer(ingamePlayer);
    	player.setHealth(20.0);
    	player.setGameMode(GameMode.SURVIVAL);
    	player.teleport(sabotage.collection.map.getWorld().getSpawnLocation());
    }

    @EventHandler
    public void onTest(TesterEvent e) {
    	if(e.getTester() == this.tester)
    		this.tester = null;
    }
    
    @EventHandler
    public void onTestCorpse(TestCorpseEvent e) {
    	if(e.getTestCorpse() == this.testCorpse)
    		this.testCorpse = null;
    }
    
    @EventHandler
    public void droppedShears(PlayerDropItemEvent e) {
		PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
		if(!playerManager.getDetective().player.equals(e.getPlayer()))
			return;
		
    	SharedListener.droppedShears(e);
    }
    
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
		PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
    	if(!sabotage.players.contains(e.getEntity().getUniqueId()))
    		return;
    	
    	if(rewarded)
    		e.setCancelled(true);
    	
    	else if(e.getEntity() instanceof Player p && playerManager.isAlive(e.getEntity().getUniqueId())) {

			/* Player died */
    		if(p.getHealth() - e.getFinalDamage() <= 0) {
	    		p.setHealth(20.0);
	        	sabotage.broadcastAll(Sprink.color("&cA player has died... " + (playerManager.players(true).size() - 1) + " players remain."));
	        	kill2(p);
	    	}
    	}
    }

    @EventHandler
    public void onCorpseClick(CorpseOpenInventoryEvent e) {
		PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
    	if(!sabotage.players.contains(e.getClicker().getUniqueId()))
    		return;
    	
    	IngamePlayer clicker = playerManager.getRole(e.getClicker().getUniqueId());
    	IngamePlayer corpse  = playerManager.dead().get(e.getCorpse().getId());
    	
		if(clicker == null || !playerManager.isAlive(clicker.player.getUniqueId())) {
			e.setCancelled(true);
			return;
		}

    	if(clicker instanceof Detective && e.getClicker().getInventory().getItemInMainHand().getType().equals(Material.SHEARS) && corpse != null) {
    		if(testCorpse == null) {
    			this.testCorpse = new TestCorpse(this,corpse,e.getCorpse().getBody().getStoredLocation());
    			this.testCorpse.runTaskTimer(Main.getInstance(), 0L, 20L);
    		}
   
    		e.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onRightClickPlayer(PlayerInteractEntityEvent e) {
		PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
        IngamePlayer clicker = playerManager.getRole(e.getPlayer().getUniqueId());
        IngamePlayer clicked = playerManager.getRole(e.getRightClicked().getUniqueId());
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        
        if(clicker == null || clicked == null) return;
        if(item.getType().equals(Material.AIR) || !playerManager.isAlive(e.getPlayer().getUniqueId())) return;
        
        switch(item.getType()) {
	        case GLASS_BOTTLE:
	        	clicker.player.sendMessage(clicked.getBloodMessage());
	        	break;
	        	
	        case SHEARS:
	        	if(clicker instanceof Detective det) {

					if (det.insight) {
			            e.getPlayer().sendMessage(Sprink.color("&e" + clicked.player.getName() + " &eis ") + clicked.getRole());
			            det.insight = false;
		            }
		            
		            break;
	        	}
	            
        	default:
        		break;
        }
    }
    
    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent e) {
		PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
    	if(!sabotage.players.contains(e.getEntity().getUniqueId()) || !playerManager.isAlive(e.getEntity().getUniqueId()))
    		return;
    	
    	IngamePlayer damager = playerManager.getRole(e.getDamager().getUniqueId());

		if(damager != null && playerManager.isAlive(damager.player.getUniqueId())) {
		    double result = damager.blood < 2.0 ? 0.2 : 0.0;
	        damager.blood += result;
		}
    }
    
    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
		PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
		if(!sabotage.players.contains(e.getPlayer().getUniqueId()))
			return;
		
    	if(!playerManager.isAlive(e.getPlayer().getUniqueId())) {
    		e.setCancelled(true);
    		return;
    	}
    	
    	IngamePlayer placer = playerManager.getRole(e.getPlayer().getUniqueId());

        if(e.getBlock().getType().equals(Material.OAK_LEAVES)) {
	        	
	            if(placer.getPanics() == 0) 
	            	placer.timeout = false;
	            
	            if(placer.timeout)  
	            	e.setCancelled(true);
	            
	            else 
	            	new Panic(placer, e.getBlock().getLocation()).runTaskTimer(Main.getInstance(), 0L, 20L);
        }
        
        else SharedListener.onBlockPlace(e);
    }

    
    @EventHandler
    public void onClick(PlayerInteractEvent e) {
		PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
		if(!sabotage.players.contains(e.getPlayer().getUniqueId()) || !playerManager.isAlive(e.getPlayer().getUniqueId()))
			return;
    	
    	if(e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.COMPASS)) 
    		doCompass(e);
    	
    	else if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
    		if(e.getClickedBlock().getState() instanceof Sign)
    			test(e);
    		else 
    			SharedListener.rightClickBlock(e);
    	}
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void Chat(AsyncPlayerChatEvent e) {
		PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
		if(!sabotage.players.contains(e.getPlayer().getUniqueId()))
			return;
		e.setCancelled(true);
    	IngamePlayer player = playerManager.getRole(e.getPlayer().getUniqueId());
    	//IF PLAYER IS DEAD
    	if(!playerManager.isAlive(e.getPlayer().getUniqueId())) {
    		List<Player> players = playerManager.dead().keySet().stream().map(Bukkit::getPlayer).toList();
    		players.forEach(p -> {
				p.sendMessage(Sprink.color("&4[Dead] &7" + e.getPlayer().getDisplayName() + " &f" + e.getMessage()));
			});

    	}
    	
    	//IF PLAYER IS ALIVE
    	else {
    		if(e.getMessage().startsWith("@")) {
    			if(player instanceof Saboteur) {
    				List<Player> saboteurs = playerManager.saboteurs(true).stream().map(igp -> igp.player).toList();
    				saboteurs.forEach(sab -> {
						sab.sendMessage(Sprink.color("&4[Saboteur] &c" + e.getPlayer().getName() + " &e" + e.getMessage().substring(1)));
					});
    			}else{
					e.getPlayer().sendMessage(Sprink.color("&4Only Saboteurs can use this :)"));
				}
    		}
    	}
		Player plyr = e.getPlayer();
		Bukkit.getOnlinePlayers().forEach(p -> {
			String chatFormat = PlaceholderAPI.setRelationalPlaceholders(plyr, p, ConfigManager.Setting.CHAT_FORMAT.getString());
			chatFormat = PlaceholderAPI.setPlaceholders(plyr, chatFormat);
			HexUtils.sendMessage(p, chatFormat.replace("%message%", e.getMessage()));
		});
		if(Main.getInstance().getServer().getPluginManager().isPluginEnabled("DiscordSRV")) {
			DiscordSRV.getPlugin().processChatMessage(e.getPlayer(), e.getMessage(), "global", false);
		}
	}

    public void doCompass(PlayerInteractEvent e) {
		PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
        IngamePlayer tracker = playerManager.getRole(e.getPlayer().getUniqueId());
        
        if(playerManager.isAlive(e.getPlayer().getUniqueId()))
        	return;
        
        Player target = null;
        
        for(IngamePlayer igp : playerManager.players(true)) {	
        	Player player = igp.player;
        	
            if(player.equals(e.getPlayer())) {
				return;
			}
            else if(target == null) 
            	target = player;
            else if(target.getLocation().distance(tracker.player.getLocation()) <= player.getPlayer().getLocation().distance(tracker.player.getLocation())) {
			return;
			}
            else 
            	target = player;
        }
        
        if(target != null) {
        	tracker.player.setCompassTarget(target.getPlayer().getLocation());
        	tracker.player.sendMessage(ChatColor.YELLOW + "You are currently tracking: " + target.getPlayer().getName());
        }
    }
    
    private void test(PlayerInteractEvent e) {
		PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
		if(!sabotage.players.contains(e.getPlayer().getUniqueId()))
			return;

    	IngamePlayer clicker = playerManager.getRole(e.getPlayer().getUniqueId());
    	if(clicker == null || !playerManager.isAlive(e.getPlayer().getUniqueId())) 
    		return;
    	
    	if(e.getClickedBlock().getState() instanceof Sign sign) {

			if(!sign.getLine(0).contains("[Test]") || this.tester != null)
            	return;
            
            this.tester = new Tester(sabotage.collection.mapManager, clicker);
            this.tester.runTaskTimer(Main.getInstance(), 0L, 20L);
        }
    }

    private void kill2(Player dead) {
		PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
		if(!sabotage.players.contains(dead.getUniqueId()))
			return;
		
        IngamePlayer victim = playerManager.getRole(dead.getUniqueId());
        IngamePlayer killer = dead.getKiller() == null ? null : playerManager.getRole(dead.getKiller().getUniqueId());
        
        if(victim != null) {
        	Location deathbed = victim.player.getLocation();
        	
        	victim.die(killer);
        	playerManager.smite(victim.player);
        	CorpseImmortal.API().spawnCorpse(victim.player.getName(), deathbed);
        	
        	if(killer != null && playerManager.isAlive(killer.player.getUniqueId())) {
            	killer.kill(victim);
            	killer.player.sendMessage(Sprink.color("&e" + victim.player.getName() + " was " + victim.getRole()));
            	
                if(killer.blood < 3.0)
                    killer.blood = 3.0;
                
                else killer.blood += (1d/3d);
        	}
        	
        	if(victim instanceof Saboteur && ((Saboteur)victim).martyr)
        		SharedListener.explode(deathbed.getBlock(), 20); 	
        }
    }
    
	/* Getters */
    
    public MapManager getMapManager() {
    	return this.sabotage.collection.mapManager;
    }
    
    public World getWorld() {
    	return this.sabotage.collection.map.getWorld();
    }

	public boolean isRewarded() {
		return rewarded;
	}

	public Timer getTimer() {
		return timer;
	}

	public Timer getRefill() {
		return refill;
	}
	
	public void setTimer(Timer timer) {
		if(refill != null)
			this.timer = timer;
	}
	
	public void setRefill(Timer refill) {
		if(refill != null)
			this.refill = refill;
	}
}
package ml.sabotage;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.*;
import ml.sabotage.game.managers.*;
import ml.sabotage.listeners.ChatListener;
import ml.sabotage.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.player.PlayerJoinEvent;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ml.sabotage.config.ItemSettings;
import ml.sabotage.game.SabPlayer;
import ml.sabotage.game.stages.Sabotage;
import ml.sabotage.utils.PlaceholderManager;
import ml.zer0dasho.plumber.game.arena.IArena;

public class Main extends RosePlugin {
	
	private static Main instance;
	public static Sabotage sabotage;
	public static ItemSettings config;
    public static String CurrentMap;
    public static final List<IArena> ACTIVE_ARENAS = Lists.newArrayList();
	public static final Map<UUID, SabPlayer> SAB_PLAYERS = Maps.newHashMap();
	public final List<UUID> canBuildPlayers = Lists.newArrayList();

	public Main() {
		super(-1, -1, ConfigManager.class, DataManager.class, LocaleManager.class, CommandManager.class);
		instance = this;
	}

	public static void disablePlugin(String reason) {
		getInstance().getLogger().log(Level.SEVERE, reason);
    	Bukkit.getPluginManager().disablePlugin(getInstance());
    }

	@Override
	protected void enable() {
		Main.config = ItemSettings.load();
		this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		new File(getDataFolder() + "/players").mkdirs();
		new File(getDataFolder() + "/worlds").mkdirs();

		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			this.getLogger().log(Level.INFO, "PlaceholderAPI found, enabling Placeholders!");
			new PlaceholderManager(this).register();
		}

		if(Validate.validate()) {
			Main.sabotage = new Sabotage();
			Bukkit.getOnlinePlayers().forEach(player -> Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(player, "")));
		}
	}

	@Override
	protected void disable() {
		World main = Bukkit.getWorlds().get(0);
		Lists.newArrayList(ACTIVE_ARENAS.iterator()).forEach(arena -> arena.deleteNow(main));
	}

	@Override
	protected List<Class<? extends Manager>> getManagerLoadPriority() {
		return List.of();
	}

	public static Main getInstance() {
		return instance;
	}

}
package ml.sabotage.game.roles;

import ml.sabotage.game.managers.ConfigManager;
import ml.sabotage.game.managers.PlayerManager;
import ml.sabotage.game.tasks.Illusion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import ml.sabotage.Main;
import ml.sabotage.game.SabPlayer;
import ml.zer0dasho.plumber.utils.Sprink;

public class Innocent extends IngamePlayer {

    public Innocent(SabPlayer sabPlayer) {
        super(sabPlayer);
    }
    @Override
    public void sendRoleMessage(Detective detective) {
        player.sendMessage(Sprink.color("&6You are an &a&lInnocent &r&6this game!"));
        player.sendMessage(Sprink.color("&6Your job is to kill the &cSaboteurs."));
        player.sendMessage(Sprink.color("&6The detective is &9") + detective.player.getName() + ".");
    }

    @Override
    public int determineKarma(IngamePlayer victim) {
        if(victim instanceof Innocent) return ConfigManager.Setting.INNOCENT_INNOCENT.getInt();
        if(victim instanceof Saboteur) return ConfigManager.Setting.INNOCENT_SABOTEUR.getInt();
        if(victim instanceof Detective) return ConfigManager.Setting.INNOCENT_DETECTIVE.getInt();
        
        return 0;
    }
    
	@Override
	public int karmaOnDeath() {
		return ConfigManager.Setting.INNOCENT_DETECTIVE.getInt();
	}

    @SHOP
    public void Speed_II() {
        if(!hasKarma(40)) return;
        
        if(player.hasPotionEffect(PotionEffectType.SPEED))
            player.sendMessage(Sprink.color("&cYou already have speed...!"));
        else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 2));
            player.sendMessage(Sprink.color("&aYou just bought Speed II!"));
            sabPlayer.addKarma(-40);
        }
    }
    
    @SHOP
    public void Invisibility() {
        if(!hasKarma(80)) return;
        
        if(player.hasPotionEffect(PotionEffectType.INVISIBILITY))
            player.sendMessage(Sprink.color("&cYou already have invisibility...!"));
        else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 600, 2));
            player.sendMessage(Sprink.color("&aYou just bought Invisibility!"));
            sabPlayer.addKarma(-80);
        }
    }

    @SHOP
    public void Second_Wind() {
        if(!hasKarma(60)) return;

        double currentHealth = player.getHealth();
        if(currentHealth >= 10) {
            player.sendMessage(Sprink.color("&cYour health is too high!"));
        }else{
            player.setHealth(Math.random() * 4 + 8);
            player.sendMessage(Sprink.color("&aYou just bought Second Wind!"));
            sabPlayer.addKarma(-60);
        }
    }

    @SHOP
    public void Mirror_Illusion() {
        PlayerManager playerManager = Main.getInstance().getManager(PlayerManager.class);
        if(!hasKarma(100)) return;

        for(SabPlayer p : Main.SAB_PLAYERS.values()) {
            if(playerManager.isAlive(player.getUniqueId())) {
                new Illusion(p.player).runTaskTimer(Main.getInstance(), 0L, 20L);
            }
        }
        player.sendMessage(Sprink.color("&aYou just bought Mirror Illusion!"));
        sabPlayer.addKarma(-100);
    }

	@Override
	public String getRole() {
		return Sprink.color("an &a&lInnocent");
	}
}


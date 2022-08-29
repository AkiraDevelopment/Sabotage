package ml.sabotage;

import java.util.UUID;

import ml.sabotage.game.managers.ConfigManager;
import ml.sabotage.game.managers.DataManager;

public class PlayerData {

	private final UUID uuid;

	private int karma,
			lifetime = 200,
			wins,
			losses,
			sabpasses,
			kills,
			correct_kills,
			incorrect_kills,
			deaths;

	public PlayerData(UUID uuid){
		this.uuid = uuid;
		this.karma = ConfigManager.Setting.DEFAULT_KARMA.getInt();
		this.wins = 0;
		this.losses = 0;
		this.sabpasses = 0;
		this.kills = 0;
		this.correct_kills = 0;
		this.incorrect_kills = 0;
		this.deaths = 0;
	}

	public void setData(int karma, int lifetime, int wins, int losses, int sabpasses, int kills, int correct_kills, int incorrect_kills, int deaths){
		this.karma = karma;
		this.lifetime = lifetime;
		this.wins = wins;
		this.losses = losses;
		this.sabpasses = sabpasses;
		this.kills = kills;
		this.correct_kills = correct_kills;
		this.incorrect_kills = incorrect_kills;
		this.deaths = deaths;
	}

	public void setKarma(int karma){
		this.karma = karma;
	}
	public void addKarma(int karma){
		this.karma += karma;
	}
	public void setLifetime(int lifetime){
		this.lifetime = lifetime;
	}
	public void addWin(){
		this.wins += 1;
	}
	public void addLoss(){
		this.losses += 1;
	}
	public void addSabPasses(int passes){
		this.sabpasses += passes;
	}
	public void addKill(){
		this.kills += 1;
	}
	public void addCorrectKill(){
		this.correct_kills += 1;
	}
	public void addIncorrectKill(){
		this.incorrect_kills += 1;
	}
	public void addDeath(){
		this.deaths += 1;
	}

	public UUID getUUID(){
		return this.uuid;
	}

	public int getKarma(){
		return this.karma;
	}
	public int getKarmaTotal(){
		return this.lifetime;
	}
	public int getWins(){
		return this.wins;
	}
	public int getLosses(){
		return this.losses;
	}
	public int getSabpasses(){
		return this.sabpasses;
	}
	public int getKills(){
		return this.kills;
	}
	public int getCorrectKills(){
		return this.correct_kills;
	}
	public int getIncorrectKills(){
		return this.incorrect_kills;
	}
	public int getDeaths(){
		return this.deaths;
	}

	public void save(){
		Main.getInstance().getManager(DataManager.class).updatePlayerData(this);
	}

}
package com.avygeil.GrandVide.game;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Scoreboard;

import com.avygeil.util.BukkitUtils;
import com.avygeil.util.ImprovedOfflinePlayer;

public class Challenger implements Comparable<Challenger>
{
	private Player player;
	private Team team;
	private PlayerState savedState;
	private boolean forcedTeamChat;
	
	public Challenger(Player player, Team team)
	{
		this.player = player;
		this.team = team;
		savedState = new PlayerState(player);
		forcedTeamChat = true;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	public Team getTeam()
	{
		return team;
	}
	
	public void setTeam(Team team)
	{
		this.team = team;
	}
	
	public Statistics getStats()
	{
		return Statistics.getStats(player.getName());
	}
	
	public PlayerState getSavedState()
	{
		return savedState;
	}
	
	public void toggleTeamChat()
	{
		forcedTeamChat ^= true;
	}
	
	public boolean isForcedTeamChat()
	{
		return forcedTeamChat;
	}
	
	public void setScoreboard(Scoreboard sb)
	{
		if (player.isOnline())
			player.setScoreboard(sb);
	}
	
	public void teleport(Location loc)
	{
		if (player.isOnline())
			player.teleport(loc);
		else
			new ImprovedOfflinePlayer(player.getName(), true).setLocation(loc);
	}
	
	@SuppressWarnings("deprecation")
	public void giveAccessories()
	{
		final ItemStack wool = team.getWool();
		final ItemStack chestplate = team.getChestplate();
		
		if (player.isOnline())
		{
			PlayerInventory inv = player.getInventory();
			inv.clear();
			BukkitUtils.clearPlayerArmor(player);
			
			if (wool != null)
				inv.addItem(wool);
			
			if (chestplate != null)
				inv.setChestplate(chestplate);
			
			player.updateInventory();
		}
		else
		{
			ImprovedOfflinePlayer offlinePlayer = new ImprovedOfflinePlayer(player.getName());
			PlayerInventory inv = BukkitUtils.createPlayerInventory();
			
			if (wool != null)
				inv.addItem(wool);
			
			if (chestplate != null)
				inv.setChestplate(chestplate);
			
			offlinePlayer.setInventory(inv);
			offlinePlayer.savePlayerData();
		}
	}
	
	public void restoreSavedState()
	{
		if (player.isOnline())
		{
			player.teleport(savedState.getLocation());
			player.setGameMode(savedState.getGameMode());
			player.setExp(savedState.getExp());
			player.setLevel(savedState.getLevel());
			player.setFoodLevel(savedState.getFoodLevel());
			player.setHealth(savedState.getHealth());
			player.getInventory().clear();
			BukkitUtils.clearPlayerArmor(player);
			player.getInventory().setContents(savedState.getInventoryContents());
			player.getInventory().setArmorContents(savedState.getArmorContents());
		}
		else
		{
			ImprovedOfflinePlayer offlinePlayer = new ImprovedOfflinePlayer(player.getName());
			offlinePlayer.setLocation(savedState.getLocation());
			offlinePlayer.setGameMode(savedState.getGameMode());
			offlinePlayer.setExp(savedState.getExp());
			offlinePlayer.setLevel(savedState.getLevel());
			offlinePlayer.setFoodLevel(savedState.getFoodLevel());
			offlinePlayer.setHealth(savedState.getHealth());
			PlayerInventory inv = BukkitUtils.createPlayerInventory();
			inv.setContents(savedState.getInventoryContents());
			inv.setArmorContents(savedState.getArmorContents());
			offlinePlayer.setInventory(inv);
			offlinePlayer.savePlayerData();
		}
	}
	
	public void resetState()
	{
		player.setGameMode(GameMode.SURVIVAL);
		player.setExp(0.0f);
		player.setLevel(0);
		player.setFoodLevel(20);
		player.setHealth(20.0D);
		player.getInventory().clear();
		BukkitUtils.clearPlayerArmor(player);
	}
	
	public final int compareTo(Challenger o)
	{
		return team.ordinal() - o.getTeam().ordinal();
	}
}

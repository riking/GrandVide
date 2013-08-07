package com.avygeil.GrandVide.game;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerState
{
	private final Location loc;
	private final GameMode gm;
	private final float xp;
	private final int level;
	private final int food;
	private final double health;
	private final ItemStack[] inventoryContents;
	private final ItemStack[] armorContents;
	
	public PlayerState(Player player)
	{
		loc = player.getLocation().clone();
		gm = player.getGameMode();
		xp = player.getExp();
		level = player.getLevel();
		food = player.getFoodLevel();
		health = player.getHealth();
		inventoryContents = player.getInventory().getContents().clone();
		armorContents = player.getInventory().getArmorContents().clone();
	}
	
	public Location getLocation()
	{
		return loc;
	}
	
	public GameMode getGameMode()
	{
		return gm;
	}
	
	public float getExp()
	{
		return xp;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public int getFoodLevel()
	{
		return food;
	}
	
	public double getHealth()
	{
		return health;
	}
	
	public ItemStack[] getInventoryContents()
	{
		return inventoryContents;
	}
	
	public ItemStack[] getArmorContents()
	{
		return armorContents;
	}
}

package com.avygeil.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;

import net.minecraft.server.v1_6_R2.NBTCompressedStreamTools;
import net.minecraft.server.v1_6_R2.NBTTagCompound;
import net.minecraft.server.v1_6_R2.NBTTagDouble;
import net.minecraft.server.v1_6_R2.NBTTagFloat;
import net.minecraft.server.v1_6_R2.NBTTagList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R2.inventory.CraftInventoryPlayer;

public class ImprovedOfflinePlayer
{
	private File file;
	private NBTTagCompound compound;
	private String name;
	private boolean exists = false;
	private boolean autoSave = true;
	
	public ImprovedOfflinePlayer(String playerName)
	{
		this(playerName, false);
	}
	
	public ImprovedOfflinePlayer(String playerName, boolean autoSave)
	{
		this.autoSave = autoSave;
		exists = loadPlayerData(playerName);
	}
	
	public boolean exists()
	{
		return exists;
	}
	
	public void setAutoSave(boolean autoSave)
	{
		this.autoSave = autoSave;
	}
	
	public boolean getAutoSave()
	{
		return autoSave;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setLocation(Location location)
	{
		World w = location.getWorld();
		UUID uuid = w.getUID();
		compound.setLong("WorldUUIDMost", uuid.getMostSignificantBits());
		compound.setLong("WorldUUIDLeast", uuid.getLeastSignificantBits());
		compound.setInt("Dimension", w.getEnvironment().getId());
		NBTTagList position = new NBTTagList();
		position.add(new NBTTagDouble(null, location.getX()));
		position.add(new NBTTagDouble(null, location.getY()));
		position.add(new NBTTagDouble(null, location.getZ()));
		compound.set("Pos", position);
		NBTTagList rotation = new NBTTagList();
		rotation.add(new NBTTagFloat(null, location.getYaw()));
		rotation.add(new NBTTagFloat(null, location.getPitch()));
		compound.set("Rotation", rotation);
		
		if (autoSave)
			savePlayerData();
	}
	
	public void setGameMode(GameMode gamemode)
	{
		compound.setInt("playerGameType", gamemode.getValue());
		
		if(autoSave)
			savePlayerData();
	}
	
	public void setExp(float exp)
	{
		compound.setFloat("XpP", exp);
		
		if (autoSave)
			savePlayerData();
	}
	
	public void setLevel(int level)
	{
		compound.setInt("XpLevel", level);
		
		if(autoSave)
			savePlayerData();
	}
	
	public void setFoodLevel(int foodLevel)
	{
		compound.setInt("foodLevel", foodLevel);
		
		if (autoSave)
			savePlayerData();
	}
	
	public void setHealth(double health)
	{		
		compound.setShort("Health", (short)((int)Math.ceil(health)));
		compound.setFloat("HealF", (float)health);
		
		if (autoSave)
			savePlayerData();
	}
	
	public void setInventory(org.bukkit.inventory.PlayerInventory inventory)
	{
		compound.set("Inventory", ((CraftInventoryPlayer)inventory).getInventory().a(new NBTTagList()));
		
		if (autoSave)
			savePlayerData();
	}
	
	public void savePlayerData()
	{
		if (exists)
		{
			try
			{
				NBTCompressedStreamTools.a(compound, new FileOutputStream(file));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private boolean loadPlayerData(String playerName)
	{
		try
		{			
			for (World w : Bukkit.getWorlds())
			{
				file = new File(w.getWorldFolder(), "players" + File.separator + playerName + ".dat");
				
				if (file.exists())
				{
					compound = NBTCompressedStreamTools.a(new FileInputStream(file));
					name = file.getCanonicalFile().getName().replace(".dat", "");
					
					return true;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
}

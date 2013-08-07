package com.avygeil.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class BukkitUtils
{
	private BukkitUtils() {}
	
	public static Location calculateAverageLocation(Location[] loc)
	{
		if (loc == null)
			throw new NullPointerException();
		
		if (loc.length == 0)
			throw new IllegalArgumentException("Au moins un emplacement doit etre specifiee");
		
		World world = loc[0].getWorld();
		
		double avg_X = 0;
		double avg_Y = 0;
		double avg_Z = 0;
		
		for (int i = 0 ; i < loc.length ; i++)
		{
			if (loc[i].getWorld() != world)
				throw new IllegalArgumentException("Les emplacements doivent etre dans le meme monde");
			
			avg_X += loc[i].getX();
			avg_Y += loc[i].getY();
			avg_Z += loc[i].getZ();
		}
		
		return new Location(world, avg_X / loc.length, avg_Y / loc.length, avg_Z / loc.length);
	}
	
	public static Location parseLocation(String worldName, String[] coordinatesStr) throws Exception
	{		
		World world = Bukkit.getWorld(worldName);
		
		if (world == null)
			throw new NullPointerException("Le monde " + worldName + " n'existe pas ou n'a pas été trouvé");
		
		int[] coordinates = new int[3];
		
		for (int i = 0 ; i < 3 ; i++)
		{
			if (!StringUtils.isNumeric(coordinatesStr[i]))
				throw new Exception("Les coordonnées doivent être des nombres entiers");
			
			try
			{
				coordinates[i] = Integer.parseInt(coordinatesStr[i]);
			}
			catch (Exception e)
			{
				throw new Exception("Ce nombre est invalide ou trop grand");
			}
		}
		
		if (coordinates[1] < 0 || coordinates[1] > 255)
			throw new Exception("Y doit être compris entre 0 et 255");
		
		return new Location(world, (double)coordinates[0], (double)coordinates[1], (double)coordinates[2]);
	}
	
	public static String escapeConsoleName(CommandSender sender)
	{		
		return sender instanceof ConsoleCommandSender ? "*Console" : sender.getName();
	}
	
	public static boolean signContainsLines(Sign sign, String[] lines)
	{
		for (int i = 0 ; i < lines.length ; i++)
		{
			if (!sign.getLine(i).contains(lines[i]))
				return false;
		}
		
		return true;
	}
	
	public static PlayerInventory createPlayerInventory()
	{
		return new org.bukkit.craftbukkit.v1_6_R2.inventory.CraftInventoryPlayer(new net.minecraft.server.v1_6_R2.PlayerInventory(null));
	}
	
	public static void clearPlayerArmor(Player player)
	{
		player.getInventory().setArmorContents(new ItemStack[player.getInventory().getArmorContents().length]);
	}
}

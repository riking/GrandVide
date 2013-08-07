package com.avygeil.GrandVide.dropper;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import com.avygeil.GrandVide.GrandVide;
import com.avygeil.util.BukkitUtils;

public class DropperManager
{
	private static ArrayList<ISpecialDropper> droppers = new ArrayList<ISpecialDropper>();
	
	public static void register(Block block, String[] args)
	{
		try
		{
			if (args[0].equals("Scheduled"))
			{
				String[] parameters = args[1].split(":", 3);
				final long delay = Long.parseLong(parameters[0]) * 20L;
				final int id = Integer.parseInt(parameters[1]);
				final short meta = Short.parseShort(parameters[2]);
					
				if (delay <= 0)
					throw new Exception("Delai negatif");
					
				register(block, new ItemStack(id, 1, meta), delay);
			}
			else if (args[0].equals("Event"))
			{
				if (GrandVide.getInstance().configurationHandler.scriptExists(args[1]))
					register(block, new EventScript(GrandVide.getInstance().configurationHandler.getScript(args[1])));
				else
					throw new Exception("Le script \"" + args[1] + "\" n'existe pas");
			}
			else
			{
				throw new Exception("Type \"" + args[0] + "\" non reconnu");
			}
		}
		catch (Exception e)
		{
			final Location loc = block.getLocation();
			final int x = loc.getBlockX();
			final int y = loc.getBlockY();
			final int z = loc.getBlockZ();
			
			GrandVide.getInstance().getLogger().warning("Le dropper aux coordonnées " + x + " " + y + " " + z + " est corrompu: " + e.getMessage());
		}
	}
	
	public static void register(Block block, ItemStack item, long delay)
	{
		register(new ScheduledDropper(block, item, delay)); 
	}
	
	public static void register(Block block, EventScript script) throws Exception
	{
		register(new EventDropper(block, script));
	}
	
	public static void register(ISpecialDropper dropper)
	{
		droppers.add(dropper);
		dropper.start();
	}
	
	public static void unregister(Block block)
	{
		ISpecialDropper toRemove = null;
		
		for (ISpecialDropper dropper : droppers)
		{
			if (dropper.getBlock().getLocation().equals(block.getLocation()))
				toRemove = dropper;
		}
		
		if (toRemove != null)
			unregister(toRemove);
	}
	
	public static void unregister(ISpecialDropper dropper)
	{
		dropper.stop();
		droppers.remove(dropper);
	}
	
	public static void unregisterAll()
	{
		for (ISpecialDropper dropper : droppers)
			dropper.stop();
		
		droppers.clear();
	}
	
	public static boolean isRegistered(Block block)
	{		
		for (ISpecialDropper dropper : droppers)
		{
			if (dropper.getBlock().getLocation().equals(block.getLocation()))
				return true;
		}
		
		return false;
	}
	
	public static String[] getSpecialDropper(Block b)
	{
		if (b.getType() != Material.DROPPER)
			return null;
		
		final BlockFace[] faces = new BlockFace[] {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
		
		for (BlockFace face : faces)
		{			
			if (b.getRelative(face).getType() != Material.WALL_SIGN)
				continue;
			
			Sign sign = (Sign)b.getRelative(face).getState();
			
			if (BukkitUtils.signContainsLines(sign, new String[] {"[GrandVide]", "Dropper"}))
				return new String[] {ChatColor.stripColor(sign.getLine(2)), sign.getLine(3)};
		}
		
		return null;
	}
}

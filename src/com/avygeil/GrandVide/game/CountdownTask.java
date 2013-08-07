package com.avygeil.GrandVide.game;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import com.avygeil.GrandVide.GrandVide;

public class CountdownTask extends BukkitRunnable
{
	private static GrandVide gv = GrandVide.getInstance();
	private Game game;
	private int count;
	
	public CountdownTask(Game game, int count)
	{
		if (count <= 0)
			throw new IllegalArgumentException();
		
		this.game = game;
		this.count = count;
	}
	
	public void run()
	{
		game.broadcast(ChatColor.GOLD + "" + count + " secondes");
		--count;
		
		if (count > 0)
		{
			gv.getServer().getScheduler().scheduleSyncDelayedTask(gv, new CountdownTask(game, count), 20L);
		}
		else
		{
			final Location power = game.getRegion().getPowerLocation();
			
			if (power != null)
				power.getBlock().setType(Material.REDSTONE_BLOCK);
			
			game.broadcast(ChatColor.GOLD + "Bonne chance !");
			game.diffuseSound(Sound.AMBIENCE_THUNDER, 1.0F, 0.0F);
			game.isRunning = true;
		}
	}
}

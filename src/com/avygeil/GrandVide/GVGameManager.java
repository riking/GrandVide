package com.avygeil.GrandVide;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.HandlerList;

import com.avygeil.GrandVide.dropper.DropperManager;
import com.avygeil.GrandVide.game.Events;
import com.avygeil.GrandVide.game.Game;
import com.avygeil.GrandVide.game.Statistics;
import com.avygeil.GrandVide.region.Region;
import com.avygeil.util.BukkitUtils;

import de.diddiz.LogBlock.QueryParams;
import de.diddiz.LogBlock.QueryParams.BlockChangeType;
import de.diddiz.worldedit.RegionContainer;

public class GVGameManager
{
	private final GrandVide gv;
	
	private Game currentGame;
	private Events eventListener;
	
	GVGameManager(GrandVide grandvide)
	{
		gv = grandvide;
		currentGame = null;
		eventListener = null;
	}
	
	public Game getCurrentGame()
	{
		return currentGame;
	}
	
	public boolean isPlaying()
	{
		return currentGame != null;
	}
	
	public boolean isBeingPlayed(Region reg)
	{
		 return isPlaying() && getCurrentGame().getRegion().equals(reg);
	}
	
	public boolean start(CommandSender initiating, Region reg)
	{
		if (currentGame != null)
			return false;
		
		currentGame = new Game(initiating, reg);
		eventListener = new Events(gv, currentGame);
		gv.pm.registerEvents(eventListener, gv);
		
		gv.getServer().broadcastMessage(ChatColor.DARK_AQUA + BukkitUtils.escapeConsoleName(initiating) + ChatColor.GOLD + " organise un Grand Vide " + reg.getName() + " !");
		
		return true;
	}
	
	public void stop(boolean save)
	{
		if (!isPlaying())
			return;
		
		gv.getServer().broadcastMessage(ChatColor.GOLD + "Fin du Grand Vide");
		currentGame.removeAll();
		DropperManager.unregisterAll();
		
		if (save)
		{
			Statistics.saveAll();
			
			if (gv.configurationHandler.clearItems)
			{
				gv.getLogger().info("Nettoyage des items sur le sol...");
				
				for (Entity entity : currentGame.getRegion().getContainer().getWorld().getEntities())
				{
					if (entity instanceof Item && currentGame.getRegion().getContainer().contains(((Item)entity).getLocation()))
						((Item)entity).remove();
				}
				
				gv.getLogger().info("Nettoyage termine !");
			}
			
			if (gv.configurationHandler.rollBack)
			{
				final QueryParams params = new QueryParams(gv.logBlock);
				params.world = currentGame.getRegion().getContainer().getWorld();
				params.sel = new RegionContainer(currentGame.getRegion().getContainer().toSelection());
				params.bct = BlockChangeType.ALL;
				final int timeElapsed = (int)((System.currentTimeMillis() - currentGame.getTimeStart())/60000L);
				params.since = timeElapsed > 1 ? timeElapsed : 1;
				params.silent = true;
				
				new Thread(new Runnable()
				{
					public void run()
					{
						try
						{
							gv.getLogger().info("Rollback en cours... (" + params.since + " minutes)");
							gv.logBlock.getCommandsHandler().new CommandRollback(gv.getServer().getConsoleSender(), params, true);
							gv.getLogger().info("Rollback termine !");
						}
						catch (Exception e)
						{
							gv.getLogger().warning("Le rollback a echoue :");
							e.printStackTrace();
						}
					}
				}).start();
			}
		}
		
		gv.getServer().getScheduler().cancelTasks(gv);
		HandlerList.unregisterAll(eventListener);
		eventListener = null;
		currentGame = null;
	}
}

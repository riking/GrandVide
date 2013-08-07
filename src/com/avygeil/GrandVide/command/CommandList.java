package com.avygeil.GrandVide.command;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.avygeil.GrandVide.game.Team;
import com.avygeil.GrandVide.region.Region;

public class CommandList extends Command
{
	private Region reg;
	
	public CommandList(CommandSender sender, String[] args) throws CommandException
	{
		super(sender, args);
	}
	
	@Override
	protected String getPermission()
	{
		return "gv.list";
	}
	
	@Override
	protected int[] getPossibleArgsCount()
	{
		return new int[] {0, 1};
	}
	
	@Override
	protected void process() throws CommandException
	{
		try
		{
			if (argsCount == 1)
			{
				reg = gv.regionManager.findRegion(args[0]);
			}
			else
			{
				if (gv.regionManager.getRegions().isEmpty())
					throw new CommandException("Aucune arène n'a été trouvée");
				
				reg = null;
			}
		}
		catch (Exception e)
		{
			throw new CommandException(e.getMessage());
		}
	}
	
	@Override
	protected void run()
	{
		if (reg != null)
		{
			queueMessage(ChatColor.GOLD + "Spawns de l'arène " + reg.getName() + " :");
			queueMessage(ChatColor.GRAY + "<Équipe> <X Y Z>");
			
			ArrayList<Team> teams = reg.getTeams();
			Collections.sort(teams);
			
			for (Team team : teams)
			{
				StringBuilder sb = new StringBuilder();
				
				final Location spawn = reg.getSpawn(team);
				
				sb.append(team.getPrefixColor() + "[" + team.getName() + "] ");
				sb.append(ChatColor.GRAY + "(" + ChatColor.WHITE);
				sb.append(spawn.getBlockX() + " ");
				sb.append(spawn.getBlockY() + " ");
				sb.append(spawn.getBlockZ() + "" + ChatColor.GRAY + ")");
				
				queueMessage(sb.toString());
			}
		}
		else
		{
			queueMessage(ChatColor.GOLD + "Liste des arènes :");
			queueMessage(ChatColor.GRAY + "<ID> <Nom> <Monde> <Point 1> <Point 2> <Courant>");
			
			for (Region current : gv.regionManager.getRegions())
			{
				StringBuilder sb = new StringBuilder();
				
				sb.append(current.getID());
				sb.append(ChatColor.GRAY + " - " + ChatColor.GOLD);
				sb.append(current.getName());
				sb.append(ChatColor.GRAY + " (" + ChatColor.LIGHT_PURPLE);
				sb.append(current.getContainer().getWorld().getName());
				sb.append(ChatColor.GRAY + " | " + ChatColor.WHITE);
				sb.append(current.getContainer().getMinimumPoint().getBlockX() + " ");
				sb.append(current.getContainer().getMinimumPoint().getBlockY() + " ");
				sb.append(current.getContainer().getMinimumPoint().getBlockZ());
				sb.append(ChatColor.GRAY + " | " + ChatColor.WHITE);
				sb.append(current.getContainer().getMaximumPoint().getBlockX() + " ");
				sb.append(current.getContainer().getMaximumPoint().getBlockY() + " ");
				sb.append(current.getContainer().getMaximumPoint().getBlockZ());
				sb.append(ChatColor.GRAY + ") " + ChatColor.RED);
				
				if (current.getPowerLocation() != null)
				{
					sb.append(current.getPowerLocation().getBlockX() + " ");
					sb.append(current.getPowerLocation().getBlockY() + " ");
					sb.append(current.getPowerLocation().getBlockZ());
				}
				else
				{
					sb.append("Aucun");
				}
				
				if (gv.gameManager.isBeingPlayed(current))
					sb.append(" " + ChatColor.RED + "[EN JEU!]");
				
				queueMessage(sb.toString());
			}
		}
		
		queueProcess();
	}
}

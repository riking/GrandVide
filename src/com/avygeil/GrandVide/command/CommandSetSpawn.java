package com.avygeil.GrandVide.command;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.avygeil.GrandVide.game.Team;
import com.avygeil.GrandVide.region.Region;
import com.avygeil.util.BukkitUtils;

public class CommandSetSpawn extends Command
{
	private Region reg;
	private Team team;
	private Location spawn;
	
	public CommandSetSpawn(CommandSender sender, String[] args) throws CommandException
	{
		super(sender, args);
	}
	
	@Override
	protected String getPermission()
	{
		return "gv.setspawn";
	}
	
	@Override
	protected int[] getPossibleArgsCount()
	{
		return new int[] {2, 5};
	}
	
	@Override
	protected void process() throws CommandException
	{
		try
		{
			reg = gv.regionManager.findRegion(args[0]);
			
			if (gv.gameManager.isBeingPlayed(reg))
				throw new CommandException("Une partie est en cours sur cette arène");
			
			team = Team.getByName(args[1]);
			
			if (team == null)
				throw new CommandException("Cette équipe n'existe pas");		
			
			if (argsCount == 5)
			{
				spawn = BukkitUtils.parseLocation(reg.getContainer().getWorld().getName(), Arrays.copyOfRange(args, 2, 5));
			}
			else
			{
				if (isSenderConsole())
					throw new CommandException("Les coordonnées sont obligatoires depuis la console");
				
				spawn = ((Player)sender).getLocation();
			}
			
			if (gv.configurationHandler.spawnInsideRegion && !reg.getContainer().contains(spawn))
				throw new CommandException("Le spawn doit être dans l'arène");
			else if (reg.getContainer().getWorld() != spawn.getWorld())
				throw new CommandException("Le spawn doit être dans le même monde que l'arène");
		}
		catch (Exception e)
		{
			throw new CommandException(e.getMessage());
		}
	}
	
	@Override
	protected void run() throws CommandException
	{
		if (!gv.regionManager.setRegionSpawn(reg, team, spawn))
			throw new CommandException("Le spawn de l'arène n'a pas pu être changé");
		
		success("Le spawn " + team.getName() + " de l'arène \"" + reg.getName() + "\" a été changé avec succès");
	}
}

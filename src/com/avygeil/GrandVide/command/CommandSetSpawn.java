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
				throw new CommandException("Une partie est en cours sur cette ar�ne");
			
			team = Team.getByName(args[1]);
			
			if (team == null)
				throw new CommandException("Cette �quipe n'existe pas");		
			
			if (argsCount == 5)
			{
				spawn = BukkitUtils.parseLocation(reg.getContainer().getWorld().getName(), Arrays.copyOfRange(args, 2, 5));
			}
			else
			{
				if (isSenderConsole())
					throw new CommandException("Les coordonn�es sont obligatoires depuis la console");
				
				spawn = ((Player)sender).getLocation();
			}
			
			if (gv.configurationHandler.spawnInsideRegion && !reg.getContainer().contains(spawn))
				throw new CommandException("Le spawn doit �tre dans l'ar�ne");
			else if (reg.getContainer().getWorld() != spawn.getWorld())
				throw new CommandException("Le spawn doit �tre dans le m�me monde que l'ar�ne");
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
			throw new CommandException("Le spawn de l'ar�ne n'a pas pu �tre chang�");
		
		success("Le spawn " + team.getName() + " de l'ar�ne \"" + reg.getName() + "\" a �t� chang� avec succ�s");
	}
}

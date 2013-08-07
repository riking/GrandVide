package com.avygeil.GrandVide.command;

import org.bukkit.command.CommandSender;
import com.avygeil.GrandVide.game.Team;
import com.avygeil.GrandVide.region.Region;

public class CommandDeleteSpawn extends Command
{
	private Region reg;
	private Team team;
	
	public CommandDeleteSpawn(CommandSender sender, String[] args) throws CommandException
	{
		super(sender, args);
	}
	
	@Override
	protected String getPermission()
	{
		return "gv.delspawn";
	}
	
	@Override
	protected int[] getPossibleArgsCount()
	{
		return new int[] {2};
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
			
			if (reg.getSpawn(team) == null)
				throw new CommandException("Aucun spawn n'est défini pour cette équipe");
			
			if (team == Team.NOTEAM)
				throw new CommandException("Impossible de supprimer le spawn par défaut");
		}
		catch (Exception e)
		{
			throw new CommandException(e.getMessage());
		}
	}
	
	@Override
	protected void run() throws CommandException
	{
		if (!gv.regionManager.deleteRegionSpawn(reg, team))
			throw new CommandException("Le spawn de l'arène n'a pas pu être supprimé");
		
		success("Le spawn " + team.getName() + " de l'arène \"" + reg.getName() + "\" a été supprimé avec succès");
	}
}

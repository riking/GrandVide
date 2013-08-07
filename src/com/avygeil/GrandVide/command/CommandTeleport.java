package com.avygeil.GrandVide.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.avygeil.GrandVide.game.Team;
import com.avygeil.GrandVide.region.Region;

public class CommandTeleport extends Command
{
	private Region reg;
	private Team team;
	private Player player;
	
	public CommandTeleport(CommandSender sender, String[] args) throws CommandException
	{
		super(sender, args);
	}
	
	@Override
	protected boolean isPlayerCommand()
	{
		return true;
	}
	
	@Override
	protected String getPermission()
	{
		return "gv.tp";
	}
	
	@Override
	protected int[] getPossibleArgsCount()
	{
		return new int[] {1, 2};
	}
	
	@Override
	protected void process() throws CommandException
	{
		try
		{
			reg = gv.regionManager.findRegion(args[0]);
			
			if (argsCount == 2)
			{
				team = Team.getByName(args[1]);
				
				if (team == null)
					throw new CommandException("Cette équipe n'existe pas");
				
				if (reg.getSpawn(team) == null)
					throw new CommandException("Aucun spawn n'est défini pour cette équipe");
			}
			else
			{
				team = Team.NOTEAM;
			}
			
			player = (Player)sender;
		}
		catch (Exception e)
		{
			throw new CommandException(e.getMessage());
		}
	}
	
	@Override
	protected void run()
	{
		player.teleport(reg.getSpawn(team));
	}
}

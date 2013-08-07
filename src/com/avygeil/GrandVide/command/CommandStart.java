package com.avygeil.GrandVide.command;

import org.bukkit.command.CommandSender;

import com.avygeil.GrandVide.game.Team;
import com.avygeil.GrandVide.region.Region;

public class CommandStart extends Command
{
	private Region reg;
	
	public CommandStart(CommandSender sender, String[] args) throws CommandException
	{
		super(sender, args);
	}
	
	@Override
	protected String getPermission()
	{
		return "gv.start";
	}
	
	@Override
	protected int[] getPossibleArgsCount()
	{
		return new int[] {1};
	}
	
	@Override
	protected void process() throws CommandException
	{
		try
		{
			reg = gv.regionManager.findRegion(args[0]);
			
			int playableTeams = 0;
			
			for (Team team : reg.getTeams())
			{
				if (team.isPlayable())
					++playableTeams;
			}
			
			if (playableTeams < 2)
				throw new CommandException("L'arène a moins de 2 équipes jouables");
		}
		catch (Exception e)
		{
			throw new CommandException(e.getMessage());
		}
	}
	
	@Override
	protected void run() throws CommandException
	{
		if (!gv.gameManager.start(sender, reg))
			throw new CommandException("Un Grand Vide est déjà en cours");
	}
}

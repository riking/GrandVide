package com.avygeil.GrandVide.command;

import org.bukkit.command.CommandSender;

import com.avygeil.GrandVide.region.Region;

public class CommandDelete extends Command
{
	private Region reg;
	
	public CommandDelete(CommandSender sender, String[] args) throws CommandException
	{
		super(sender, args);
	}
	
	@Override
	protected String getPermission()
	{
		return "gv.delete";
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
			
			if (gv.gameManager.isBeingPlayed(reg))
				throw new CommandException("Une partie est en cours sur cette arène");
		}
		catch (Exception e)
		{
			throw new CommandException(e.getMessage());
		}
	}
	
	@Override
	protected void run() throws CommandException
	{
		if (!gv.regionManager.deleteRegion(reg))
			throw new CommandException("L'arène n'a pas pu être supprimée");
		
		success("L'arène \"" + reg.getName() + "\" a été supprimée avec succès");
	}
}

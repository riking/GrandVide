package com.avygeil.GrandVide.command;

import org.bukkit.command.CommandSender;

public class CommandAbort extends Command
{
	private boolean save;
	
	public CommandAbort(CommandSender sender, String[] args) throws CommandException
	{
		super(sender, args);
	}
	
	@Override
	protected String getPermission()
	{
		return "gv.abort";
	}
	
	@Override
	protected void process() throws CommandException
	{
		if (!gv.gameManager.isPlaying())
			throw new CommandException("Il n'y a aucune partie de Grand Vide en cours");
		
		if (!gv.gameManager.getCurrentGame().isRunning && !gv.gameManager.getCurrentGame().canJoin)
			throw new CommandException("Vous ne pouvez pas faire cela maintenant");
		
		save = !gv.gameManager.getCurrentGame().canJoin;
	}
	
	@Override
	protected void run()
	{
		gv.gameManager.stop(save);
	}
}

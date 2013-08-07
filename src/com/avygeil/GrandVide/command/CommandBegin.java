package com.avygeil.GrandVide.command;

import org.bukkit.command.CommandSender;

public class CommandBegin extends Command
{	
	public CommandBegin(CommandSender sender, String[] args) throws CommandException
	{
		super(sender, args);
	}
	
	@Override
	protected String getPermission()
	{
		return "gv.begin";
	}
	
	@Override
	protected void process() throws CommandException
	{
		if (!gv.gameManager.isPlaying())
			throw new CommandException("Il n'y a aucune partie de Grand Vide en cours");
		
		if (!gv.gameManager.getCurrentGame().canJoin)
			throw new CommandException("La partie a déjà commencé");
	}
	
	@Override
	protected void run() throws CommandException
	{
		if (!gv.gameManager.getCurrentGame().begin())
			throw new CommandException("Au moins 2 équipes doivent avoir des joueurs");
	}
}

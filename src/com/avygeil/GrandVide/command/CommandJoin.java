package com.avygeil.GrandVide.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandJoin extends Command
{
	Player player;
	
	public CommandJoin(CommandSender sender, String[] args) throws CommandException
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
		return "gv.join";
	}
	
	@Override
	protected void process() throws CommandException
	{
		if (!gv.gameManager.isPlaying())
			throw new CommandException("Il n'y a aucune partie de Grand Vide en cours");
			
		player = (Player)sender;
	}
	
	@Override
	protected void run() throws CommandException
	{
		if (!gv.gameManager.getCurrentGame().addPlayer(player))
			throw new CommandException("Vous participez déjà à cette partie");
		
		success("Vous avez rejoint la partie");
	}
}

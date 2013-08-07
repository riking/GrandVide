package com.avygeil.GrandVide.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.avygeil.GrandVide.game.Challenger;

public class CommandLeave extends Command
{
	Challenger challenger;
	
	public CommandLeave(CommandSender sender, String[] args) throws CommandException
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
		return "gv.leave";
	}
	
	@Override
	protected void process() throws CommandException
	{
		if (!gv.gameManager.isPlaying())
			throw new CommandException("Il n'y a aucune partie de Grand Vide en cours");
		
		challenger = gv.gameManager.getCurrentGame().getPlayer((Player)sender);
		
		if (challenger == null)
			throw new CommandException("Vous ne participez pas à la partie");
	}
	
	@Override
	protected void run()
	{		
		gv.gameManager.getCurrentGame().removePlayer(challenger);
		
		success("Vous avez quitté la partie");
	}
}

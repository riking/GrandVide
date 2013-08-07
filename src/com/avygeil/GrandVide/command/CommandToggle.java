package com.avygeil.GrandVide.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.avygeil.GrandVide.game.Challenger;

public class CommandToggle extends Command
{
	private Challenger challenger;
	
	public CommandToggle(CommandSender sender, String[] args) throws CommandException
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
		return "gv.toggle";
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
		challenger.toggleTeamChat();
		
		success(challenger.isForcedTeamChat() ? "Vous avez verouillé le chat équipe" : "Vous avez déverouillé le chat équipe");
	}
}

package com.avygeil.GrandVide.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.avygeil.GrandVide.game.Challenger;

public class CommandPlayers extends Command
{
	public CommandPlayers(CommandSender sender, String[] args) throws CommandException
	{
		super(sender, args);
	}
	
	@Override
	protected String getPermission()
	{
		return "gv.players";
	}
	
	@Override
	protected void process() throws CommandException
	{
		if (!gv.gameManager.isPlaying())
			throw new CommandException("Il n'y a aucune partie de Grand Vide en cours");
			
		if (gv.gameManager.getCurrentGame().getPlayers().isEmpty())
			throw new CommandException("Il n'y a actuellement aucun participant");
	}
	
	@Override
	protected void run()
	{
		queueMessage(ChatColor.GOLD + "Joueurs actuels :");
		queueMessage(ChatColor.GRAY + "<Équipe> <Joueur>");
		
		for (Challenger c : gv.gameManager.getCurrentGame().getPlayers())
		{
			StringBuilder sb = new StringBuilder();
			
			sb.append(c.getTeam().getPrefixColor() + "[" + c.getTeam().getName() + "] ");
			sb.append(ChatColor.WHITE + c.getPlayer().getName());
			
			if (!c.getPlayer().isOnline())
				sb.append(ChatColor.GRAY + " (Hors Ligne)");
			
			queueMessage(sb.toString());
		}
		
		queueProcess();
	}
}

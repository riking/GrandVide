package com.avygeil.GrandVide.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.avygeil.GrandVide.game.Challenger;

public class CommandGlobalChat extends Command
{
	private Challenger challenger;
	
	public CommandGlobalChat(CommandSender sender, String[] args) throws CommandException
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
		return "gv.globalchat";
	}
	
	@Override
	protected int[] getPossibleArgsCount()
	{
		return new int[] {-1};
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
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.BOLD + "" + ChatColor.DARK_GRAY + "[");
		sb.append(ChatColor.GOLD + "GV" + ChatColor.DARK_GRAY + "]");
		sb.append(ChatColor.RESET + " ");
		sb.append(challenger.getTeam().getPrefixColor());
		sb.append(challenger.getPlayer().getName());
		sb.append(ChatColor.RESET + ": ");
		
		for (String arg : args)
			sb.append(arg + " ");
		
		gv.getServer().broadcastMessage(sb.toString());
	}
}

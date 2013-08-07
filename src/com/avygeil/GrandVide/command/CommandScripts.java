package com.avygeil.GrandVide.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.avygeil.GrandVide.dropper.EventScript;

public class CommandScripts extends Command
{
	public CommandScripts(CommandSender sender, String[] args) throws CommandException
	{
		super(sender, args);
	}
	
	@Override
	protected String getPermission()
	{
		return "gv.scripts";
	}
	
	@Override
	protected void process() throws CommandException
	{
		if (gv.configurationHandler.getScripts().isEmpty())
			throw new CommandException("Aucun script n'a été trouvé");
	}
	
	@Override
	protected void run()
	{
		queueMessage(ChatColor.GOLD + "Liste des scripts :");
		queueMessage(ChatColor.GRAY + "<Nom> <Répétable> <Séquence>");
		
		for (EventScript script : gv.configurationHandler.getScripts())
		{
			StringBuilder sb = new StringBuilder();
			
			sb.append(ChatColor.LIGHT_PURPLE + script.getName());
			sb.append(ChatColor.GRAY  + " - " + ChatColor.RED);
			sb.append(script.getRepeat() ? "Oui" : "Non");
			sb.append(ChatColor.GRAY + " - ");
			sb.append(ChatColor.RESET + script.getSequence());
			
			queueMessage(sb.toString());
		}
		
		queueProcess();
	}
}

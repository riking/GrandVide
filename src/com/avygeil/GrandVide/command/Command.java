package com.avygeil.GrandVide.command;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import com.avygeil.GrandVide.GrandVide;

public abstract class Command
{
	protected static GrandVide gv = GrandVide.getInstance();
	
	protected CommandSender sender;
	protected String[] args;
	protected int argsCount;
	
	private ArrayList<String> queue = new ArrayList<String>();
	
	protected Command(CommandSender sender, String[] arguments) throws CommandException
	{
		this.sender = sender;
		args = arguments;
		argsCount = args == null ? 0 : args.length;
		
		if (isPlayerCommand() && isSenderConsole())
			throw new CommandException("Seul un joueur peut utiliser cette commande");
		
		if (!hasPermission())
			throw new CommandException("Vous n'avez pas la permission d'utiliser cette commande");
		
		if (!hasEnoughArgs())
			throw new CommandException("Nombre d'arguments invalide");
		
		try
		{
			process();
			run();
		}
		catch (CommandException e)
		{
			throw e;
		}
	}
		
	private boolean hasEnoughArgs()
	{		
		for (int i : getPossibleArgsCount())
		{
			if (i == argsCount || (i == -1 && argsCount > 0))
				return true;
		}
		
		return false;
	}
	
	private boolean hasPermission()
	{
		final String permission = getPermission();
		
		return permission.equals("") || sender.hasPermission(permission) || sender.isOp();
	}
	
	protected boolean isSenderConsole()
	{
		return sender instanceof ConsoleCommandSender;
	}
	
	protected void success(String msg)
	{
		sender.sendMessage(ChatColor.GREEN + msg);
	}
	
	protected void queueMessage(String msg)
	{
		queue.add(msg);
	}
	
	protected void queueProcess()
	{
		for (String msg : queue)
			sender.sendMessage(msg);
		
		queue.clear();
	}
	
	protected boolean isPlayerCommand()
	{
		return false;
	}
	
	protected String getPermission()
	{
		return "";
	}
	
	protected int[] getPossibleArgsCount()
	{
		return new int[] {0};
	}
	
	protected abstract void process() throws CommandException;
	
	protected abstract void run() throws CommandException;
}

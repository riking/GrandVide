package com.avygeil.GrandVide.command;

import org.bukkit.command.CommandSender;

public class CommandDeleteScript extends Command
{
	private String name;
	
	public CommandDeleteScript(CommandSender sender, String[] args) throws CommandException
	{
		super(sender, args);
	}
	
	@Override
	protected String getPermission()
	{
		return "gv.delscript";
	}
	
	@Override
	protected int[] getPossibleArgsCount()
	{
		return new int[] {1};
	}
	
	@Override
	protected void process() throws CommandException
	{
		if (!gv.configurationHandler.scriptExists(args[0]))
			throw new CommandException("Ce script n'existe pas");
		
		name = args[0];
	}
	
	@Override
	protected void run()
	{
		gv.configurationHandler.deleteScript(name);
		
		success("Script supprimé avec succès");
	}
}

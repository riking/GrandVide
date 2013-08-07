package com.avygeil.GrandVide.command;

import org.bukkit.command.CommandSender;

import com.avygeil.util.StringUtils;

public class CommandSetScript extends Command
{
	private String name;
	private boolean repeat;
	private String sequence;
	private boolean existed;
	
	public CommandSetScript(CommandSender sender, String[] args) throws CommandException
	{
		super(sender, args);
	}
	
	@Override
	protected String getPermission()
	{
		return "gv.setscript";
	}
	
	@Override
	protected int[] getPossibleArgsCount()
	{
		return new int[] {3};
	}
	
	@Override
	protected void process() throws CommandException
	{
		if (!StringUtils.isAlpha(args[0]))
			throw new CommandException("Le nom contient des caractères spéciaux ou des nombres");
		
		name = args[0];
		repeat = Boolean.parseBoolean(args[1]);
		
		for (String event : args[2].split(";"))
		{
			String[] details = event.split(":", 3);
			
			if (details.length != 3)
				throw new CommandException("Chaque event doit être séparé par 3 \":\"");
			
			try
			{
				Long.parseLong(details[0]);
				Integer.parseInt(details[1]);
				Short.parseShort(details[2]);
			}
			catch (NumberFormatException e)
			{
				throw new CommandException("Les métadonnées doivent être des nombres");
			}
		}
		
		sequence = args[2];
		existed = gv.configurationHandler.scriptExists(name);
	}
	
	@Override
	protected void run()
	{
		gv.configurationHandler.setScript(name, repeat, sequence);
		
		if (existed)
			success("Script modifié avec succès");
		else
			success("Script ajouté avec succès");
	}
}

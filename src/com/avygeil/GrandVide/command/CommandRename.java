package com.avygeil.GrandVide.command;

import org.bukkit.command.CommandSender;

import com.avygeil.GrandVide.region.Region;
import com.avygeil.util.StringUtils;

public class CommandRename extends Command
{
	private Region reg;
	private String name;
	
	public CommandRename(CommandSender sender, String[] args) throws CommandException
	{
		super(sender, args);
	}
	
	@Override
	protected String getPermission()
	{
		return "gv.rename";
	}
	
	@Override
	protected int[] getPossibleArgsCount()
	{
		return new int[] {2};
	}
	
	@Override
	protected void process() throws CommandException
	{
		try
		{
			reg = gv.regionManager.findRegion(args[0]);
			
			if (gv.gameManager.isBeingPlayed(reg))
				throw new CommandException("Une partie est en cours sur cette ar�ne");
			
			if (!StringUtils.isAlpha(args[1]))
				throw new CommandException("Le nom contient des caract�res sp�ciaux ou des nombres");
			
			name = args[1];
			
			if (reg.getName().equals(name))
				throw new CommandException("L'ar�ne porte d�j� ce nom");
			
			if (!reg.getName().equalsIgnoreCase(name) && gv.regionManager.getRegion(name) != null)
				throw new CommandException("L'ar�ne " + name + " existe d�j�");
		}
		catch (Exception e)
		{
			throw new CommandException(e.getMessage());
		}
	}
	
	@Override
	protected void run() throws CommandException
	{
		if (!gv.regionManager.renameRegion(reg, name))
			throw new CommandException("L'ar�ne n'a pas pu �tre renomm�e");
		
		success("L'ar�ne a �t� renomm�e en \"" + name + "\" avec succ�s");
	}
}

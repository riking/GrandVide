package com.avygeil.GrandVide.command;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.avygeil.GrandVide.region.AreaContainer;
import com.avygeil.GrandVide.region.Region;
import com.avygeil.util.BukkitUtils;
import com.avygeil.util.StringUtils;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class CommandCreate extends Command
{
	private String name;
	private AreaContainer container;
	
	public CommandCreate(CommandSender sender, String[] args) throws CommandException
	{
		super(sender, args);
	}
	
	@Override
	protected String getPermission()
	{
		return "gv.create";
	}
	
	@Override
	protected int[] getPossibleArgsCount()
	{
		return new int[] {1, 8};
	}
	
	@Override
	protected void process() throws CommandException
	{
		try
		{
			if (!StringUtils.isAlpha(args[0]))
				throw new CommandException("Le nom contient des caract�res sp�ciaux ou des nombres");
			
			name = args[0];
			
			Region reg = gv.regionManager.getRegion(name);
			
			if (reg != null)
				throw new CommandException("L'ar�ne \"" + reg.getName() + "\" existe d�j�");
			
			if (argsCount == 8)
			{
				final Location pt1 = BukkitUtils.parseLocation(args[1], Arrays.copyOfRange(args, 2, 5));
				final Location pt2 = BukkitUtils.parseLocation(args[1], Arrays.copyOfRange(args, 5, 8));
				
				container = new AreaContainer(pt1, pt2);
			}
			else
			{
				if (isSenderConsole())
					throw new CommandException("Les coordonn�es sont obligatoires depuis la console");
				
				if (!gv.configurationHandler.worldEdit)
					throw new CommandException("WorldEdit doit �tre actif pour utiliser la s�lection");
				
				final Selection sel = gv.worldEdit.getSelection((Player)sender);
				
				if (sel == null)
					throw new CommandException("Vous devez d'abord s�lectionner une r�gion");
				
				if (!(sel instanceof CuboidSelection))
					throw new CommandException("La s�lection doit �tre cubique");
				
				container = new AreaContainer(sel.getMinimumPoint(), sel.getMaximumPoint());
			}
		}
		catch (Exception e)
		{
			throw new CommandException(e.getMessage());
		}
	}
	
	@Override
	protected void run() throws CommandException
	{
		if (!gv.regionManager.createRegion(name, container))
			throw new CommandException("L'ar�ne n'a pas pu �tre cr��e");
		
		success("L'ar�ne \"" + name + "\" a �t� cr��e avec succ�s");
	}
}

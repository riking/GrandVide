package com.avygeil.GrandVide.command;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.avygeil.GrandVide.region.Region;
import com.avygeil.util.BukkitUtils;

public class CommandSetPower extends Command
{
	private Region reg;
	private Location loc;
	
	public CommandSetPower(CommandSender sender, String[] args) throws CommandException
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
		return "gv.setpower";
	}
	
	@Override
	protected int[] getPossibleArgsCount()
	{
		return new int[] {1, 4};
	}
	
	@Override
	protected void process() throws CommandException
	{
		try
		{
			reg = gv.regionManager.findRegion(args[0]);
			
			if (gv.gameManager.isBeingPlayed(reg))
				throw new CommandException("Une partie est en cours sur cette ar�ne");
			
			if (argsCount == 4)
			{
				loc = BukkitUtils.parseLocation(reg.getContainer().getWorld().getName(), Arrays.copyOfRange(args, 1, 4));
			}
			else
			{
				if (isSenderConsole())
					throw new CommandException("Les coordonn�es sont obligatoires depuis la console");
				
				if (((Player)sender).getWorld() != reg.getContainer().getWorld())
					throw new CommandException("Vous devez �tre dans le m�me monde que l'ar�ne");
				
				loc = ((Player)sender).getTargetBlock(null, 120).getLocation();
			}
			
			if (loc.getBlock().getType() == Material.AIR)
				loc = null;
			
			if (loc == null && reg.getPowerLocation() == null)
				throw new CommandException("Le courant n'existe d�j� pas");
			
			if (loc != null && reg.getPowerLocation() != null && loc.equals(reg.getPowerLocation()))
				throw new CommandException("Le courant est d�j� ici");
		}
		catch (Exception e)
		{
			throw new CommandException(e.getMessage());
		}
	}
	
	@Override
	protected void run() throws CommandException
	{
		if (!gv.regionManager.setRegionPowerLocation(reg, loc))
			throw new CommandException("Le bloc de courant de l'ar�ne n'a pas pu �tre chang�");
		
		if (loc != null)
			success("Le bloc de courant de l'ar�ne \"" + reg.getName() + "\" a �t� chang� avec succ�s");
		else
			success("Le bloc de courant de l'ar�ne \"" + reg.getName() + "\" a �t� supprim� avec succ�s");
	}
}

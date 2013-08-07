package com.avygeil.GrandVide.dropper;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.avygeil.GrandVide.GrandVide;

public class DropperTask extends BukkitRunnable
{
	private final ISpecialDropper dropper;
	
	public DropperTask(ISpecialDropper dropper)
	{
		this.dropper = dropper;
	}
	
	public void run()
	{
		if (!dropper.fire())
			return;
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(GrandVide.getInstance(), new DropperTask(dropper), dropper.getNextDropTicks());
	}
}
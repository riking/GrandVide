package com.avygeil.GrandVide.dropper;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.inventory.ItemStack;

import com.avygeil.GrandVide.GrandVide;


public class ScheduledDropper implements ISpecialDropper
{
	private final Block block;
	private final ItemStack item;
	private final long delay;
	private boolean stopped;
	
	public ScheduledDropper(Block block, ItemStack item, long delay)
	{
		this.block = block;
		this.item = item;
		this.delay = delay;
		stopped = false;
	}
	
	public Block getBlock()
	{
		return block;
	}
	
	public void start()
	{
		Bukkit.getScheduler().scheduleSyncDelayedTask(GrandVide.getInstance(), new DropperTask(this), 0L);
	}
	
	public void stop()
	{
		stopped = true;
	}
	
	public long getNextDropTicks()
	{
		return delay;
	}
	
	public boolean fire()
	{
		if (stopped || block.getType() != Material.DROPPER)
			return false;
		
		Dropper dropper = (Dropper)block.getState();
		dropper.getInventory().addItem(item.clone());
		dropper.drop();
		dropper.update();
		
		return true;
	}
}

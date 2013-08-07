package com.avygeil.GrandVide.dropper;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.inventory.ItemStack;

import com.avygeil.GrandVide.GrandVide;


public class EventDropper implements ISpecialDropper
{
	private final Block block;
	private final DropperSequence seq;
	private TimedItem current;
	private boolean stopped;
	
	public EventDropper(Block block, EventScript script) throws Exception
	{
		this.block = block;
		seq = new DropperSequence(script);
		current = seq.getNextTimedItem();
		stopped = false;
	}
	
	public Block getBlock()
	{
		return block;
	}
	
	public void start()
	{
		Bukkit.getScheduler().scheduleSyncDelayedTask(GrandVide.getInstance(), new DropperTask(this), getNextDropTicks());
	}
	
	public void stop()
	{
		stopped = true;
	}
	
	public long getNextDropTicks()
	{
		return current.getTicks();
	}
	
	public boolean fire()
	{
		if (stopped || block.getType() != Material.DROPPER)
			return false;
		
		Dropper dropper = (Dropper)block.getState();
		dropper.getInventory().addItem(current.getItem().clone());
		dropper.drop();
		dropper.update();
		
		current = seq.getNextTimedItem();
		
		return current != null;
	}
	
	private class DropperSequence
	{
		private ArrayList<TimedItem> items;
		private int cursor;
		private final EventScript script;
		
		public DropperSequence(EventScript script) throws Exception
		{
			items = new ArrayList<TimedItem>();
			this.script = script;
			
			for (String event : script.getSequence().split(";"))
			{
				String[] details = event.split(":", 3);
				
				final long delay = Long.parseLong(details[0]) * 20L;
				final int id = Integer.parseInt(details[1]);
				final short meta = Short.parseShort(details[2]);
				
				items.add(new TimedItem(new ItemStack(id, 1, meta), delay));
			}
			
			cursor = 0;
		}
		
		public TimedItem getNextTimedItem()
		{
			if (cursor == items.size())
			{
				if (script.getRepeat())
					cursor = 0;
				else
					return null;
			}
			
			return items.get(cursor++);
		}
	}
	
	private class TimedItem
	{
		private final ItemStack item;
		private final long ticks;
		
		public TimedItem(ItemStack item, long ticks)
		{
			this.item = item;
			this.ticks = ticks;
		}
		
		public ItemStack getItem()
		{
			return item;
		}
		
		public long getTicks()
		{
			return ticks;
		}
	}
}

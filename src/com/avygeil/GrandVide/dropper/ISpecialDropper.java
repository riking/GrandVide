package com.avygeil.GrandVide.dropper;

import org.bukkit.block.Block;

public interface ISpecialDropper
{
	public Block getBlock();
	
	public void start();
	
	public void stop();
	
	public long getNextDropTicks();
	
	public boolean fire();
}

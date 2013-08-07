package com.avygeil.GrandVide.region;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.World;

import com.avygeil.GrandVide.GrandVide;
import com.avygeil.util.Serialization;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class AreaContainer
{
	private World world;
	private Location min;
	private Location max;
	
	public AreaContainer(Location pt1, Location pt2)
	{
		if (pt1 == null || pt2 == null)
			throw new NullPointerException();
			
		final World world = pt1.getWorld();
		
		if (!world.equals(pt2.getWorld()))
			throw new IllegalArgumentException("Les mondes des points sont differents");
		
		double minX = Math.min(pt1.getX(), pt2.getX());
		double minY = Math.min(pt1.getY(), pt2.getY());
		double minZ = Math.min(pt1.getZ(), pt2.getZ());
		
		double maxX = Math.max(pt1.getX(), pt2.getX());
		double maxY = Math.max(pt1.getY(), pt2.getY());
		double maxZ = Math.max(pt1.getZ(), pt2.getZ());

		this.world = world;
		this.min = new Location(world, minX, minY, minZ);
		this.max = new Location(world, maxX, maxY, maxZ);
	}
	
	public AreaContainer(World world, byte[] bytes) throws Exception
	{
		this.world = world;
		
		if (bytes.length != 24)
			throw new IllegalArgumentException("Format du conteneur invalide");
		
		try
		{
			int[] minCoordinates = new int[3];
			int[] maxCoordinates = new int[3];
			
			minCoordinates[0] = Serialization.toInt(Arrays.copyOfRange(bytes, 0, 4));
			minCoordinates[1] = Serialization.toInt(Arrays.copyOfRange(bytes, 4, 8));
			minCoordinates[2] = Serialization.toInt(Arrays.copyOfRange(bytes, 8, 12));
			
			maxCoordinates[0] = Serialization.toInt(Arrays.copyOfRange(bytes, 12, 16));
			maxCoordinates[1] = Serialization.toInt(Arrays.copyOfRange(bytes, 16, 20));
			maxCoordinates[2] = Serialization.toInt(Arrays.copyOfRange(bytes, 20, 24));
			
			min = new Location(world, minCoordinates[0], minCoordinates[1], minCoordinates[2]);
			max = new Location(world, maxCoordinates[0], maxCoordinates[1], maxCoordinates[2]);
		}
		catch (Exception e)
		{
			throw e;
		}
	}
	
	public World getWorld()
	{
		return world;
	}
	
	public Location getMinimumPoint()
	{
		return min;
	}
	
	public Location getMaximumPoint()
	{
		return max;
	}
	
	public boolean contains(Location loc)
	{		
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		
		return x >= min.getBlockX() && x <= max.getBlockX()
				&& y >= min.getBlockY() && y <= max.getBlockY()
				&& z >= min.getBlockZ() && z <= max.getBlockZ()
				&& loc.getWorld() == world;
	}
	
	public Selection toSelection()
	{
		if (!GrandVide.getInstance().configurationHandler.worldEdit)
			return null;
		
		return new CuboidSelection(world, min, max);
	}
	
	public byte[] serialize()
	{
		byte[] minX = Serialization.toByteArray(min.getBlockX());
		byte[] minY = Serialization.toByteArray(min.getBlockY());
		byte[] minZ = Serialization.toByteArray(min.getBlockZ());
		
		byte[] maxX = Serialization.toByteArray(max.getBlockX());
		byte[] maxY = Serialization.toByteArray(max.getBlockY());
		byte[] maxZ = Serialization.toByteArray(max.getBlockZ());
		
		byte[] min = Serialization.concatByteArrays(minX, minY, minZ);
		byte[] max = Serialization.concatByteArrays(maxX, maxY, maxZ);
		
		return Serialization.concatByteArrays(min, max);
	}
}

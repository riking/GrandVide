package com.avygeil.GrandVide.region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Location;
import org.bukkit.World;

import com.avygeil.GrandVide.game.Team;
import com.avygeil.util.Serialization;

public class Region
{
	private int id;
	private String name;
	private AreaContainer container;
	private Map<Team, Location> teamSpawns;
	private Location powerLocation;

	public Region(int id, String name, AreaContainer container, Location spawn)
	{
		if (id <= 0)
			throw new IllegalArgumentException("L'ID ne peut pas etre negatif ou nul");
		
		this.id = id;
		this.name = name;
		this.container = container;
		teamSpawns = new HashMap<Team, Location>();
		teamSpawns.put(Team.NOTEAM, spawn);
		powerLocation = null;
	}
	
	public Region(int id, String name, World world, byte[] area, byte[] teams, byte[] powerLocation) throws Exception
	{
		if (id <= 0)
			throw new IllegalArgumentException("L'ID ne peut pas etre negatif ou nul");
		
		this.id = id;
		this.name = name;
		
		container = new AreaContainer(world, area);
		teamSpawns = new HashMap<Team, Location>();
		
		if (teams.length % 33 != 0)
			throw new IllegalArgumentException("Format des equipes invalide");
		
		try
		{
			for (int i = 0 ; i < teams.length ; i += 33)
			{
				double[] coordinates = new double[3];
				float[] angles = new float[2];
				
				coordinates[0] = Serialization.toDouble(Arrays.copyOfRange(teams, i + 1, i + 9));
				coordinates[1] = Serialization.toDouble(Arrays.copyOfRange(teams, i + 9, i + 17));
				coordinates[2] = Serialization.toDouble(Arrays.copyOfRange(teams, i + 17, i + 25));
				angles[0] = Serialization.toFloat(Arrays.copyOfRange(teams, i + 25, i + 29));
				angles[1] = Serialization.toFloat(Arrays.copyOfRange(teams, i + 29, i + 33));
				
				Location loc = new Location(container.getWorld(), coordinates[0], coordinates[1], coordinates[2], angles[0], angles[1]);
				
				teamSpawns.put(Team.getByID(teams[i]), loc);
			}
		}
		catch (Exception e)
		{
			throw e;
		}
		
		if (powerLocation.length == 12)
		{
			int[] powerCoordinates = new int[3];
			
			powerCoordinates[0] = Serialization.toInt(Arrays.copyOfRange(powerLocation, 0, 4));
			powerCoordinates[1] = Serialization.toInt(Arrays.copyOfRange(powerLocation, 4, 8));
			powerCoordinates[2] = Serialization.toInt(Arrays.copyOfRange(powerLocation, 8, 12));
			
			this.powerLocation = new Location(world, powerCoordinates[0], powerCoordinates[1], powerCoordinates[2]);
		}
		else
		{
			powerLocation = null;
		}
	}
	
	public int getID()
	{
		return id;
	}
	
	public void decreaseID()
	{
		--id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public AreaContainer getContainer()
	{
		return container;
	}
	
	public void setContainer(AreaContainer container)
	{
		this.container = container;
	}
	
	public Map<Team, Location> getTeamSpawns()
	{
		return teamSpawns;
	}
	
	public void setTeamSpawn(Team team, Location spawn)
	{
		teamSpawns.put(team, spawn);
	}
	
	public void deleteTeamSpawn(Team team)
	{
		teamSpawns.remove(team);
	}
	
	public ArrayList<Team> getTeams()
	{
		ArrayList<Team> result = new ArrayList<Team>();
		
		for (Entry<Team, Location> entry : teamSpawns.entrySet())
			result.add(entry.getKey());
		
		return result;
	}
	
	public boolean hasTeam(Team team)
	{
		return teamSpawns.containsKey(team);
	}
	
	public Location getSpawn()
	{
		return getSpawn(Team.NOTEAM);
	}
	
	public Location getSpawn(Team team)
	{
		return teamSpawns.get(team);
	}
	
	public void setPowerLocation(Location powerLocation)
	{
		this.powerLocation = powerLocation;
	}
	
	public Location getPowerLocation()
	{
		return powerLocation;
	}
	
	public byte[] serializeTeamMap() throws Exception
	{		
		ArrayList<byte[]> values = new ArrayList<byte[]>();
		byte[] result = null;
		
		try
		{
			for (Entry<Team, Location> entry : teamSpawns.entrySet())
			{			
				final Team team = entry.getKey();
				final Location loc = entry.getValue();
				
				byte[] x = Serialization.toByteArray(loc.getX());
				byte[] y = Serialization.toByteArray(loc.getY());
				byte[] z = Serialization.toByteArray(loc.getZ());
				byte[] yaw = Serialization.toByteArray(loc.getYaw());
				byte[] pitch = Serialization.toByteArray(loc.getPitch());
				
				byte[] coordinates = Serialization.concatByteArrays(x, y, z, yaw, pitch);
				
				values.add(Serialization.concatByteArrays(new byte[] { team.getID() }, coordinates));
			}
			
			for (byte[] bytes : values)
				result = Serialization.concatByteArrays(result, bytes);
		}
		catch (Exception e)
		{
			throw e;
		}
		
		return result;
	}
	
	public byte[] serializePowerLocation() throws Exception
	{
		if (powerLocation == null)
			return new byte[] {0};
		
		byte[] x = Serialization.toByteArray(powerLocation.getBlockX());
		byte[] y = Serialization.toByteArray(powerLocation.getBlockY());
		byte[] z = Serialization.toByteArray(powerLocation.getBlockZ());
		
		return Serialization.concatByteArrays(x, y, z);
	}
}

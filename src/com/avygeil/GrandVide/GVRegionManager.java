package com.avygeil.GrandVide;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.World;

import com.avygeil.GrandVide.game.Team;
import com.avygeil.GrandVide.region.AreaContainer;
import com.avygeil.GrandVide.region.Region;
import com.avygeil.util.BukkitUtils;
import com.avygeil.util.StringUtils;

public class GVRegionManager
{
	private final GrandVide gv;
	
	private ArrayList<Region> regions;
	private int nextID;
	
	GVRegionManager(GrandVide grandvide)
	{
		gv = grandvide;
		regions = new ArrayList<Region>();
		nextID = 1;
	}
	
	public ArrayList<Region> getRegions()
	{
		return regions;
	}
	
	private int nextID()
	{
		return nextID++;
	}
	
	@SuppressWarnings("resource")
	public void readRegions() throws Exception
	{
		ResultSet rs = null;
		
		try
		{
			rs = gv.databaseHelper.query("SELECT * FROM regions ORDER BY id");
			
			while (rs.next())
			{
				final String name = rs.getString("name").trim();
				final World world = gv.getServer().getWorld(rs.getString("world").trim());
				
				if (world == null)
					throw new Exception("Le monde " + rs.getString("world") + " n'a pas pu etre lu, assurez-vous qu'il soit charge au demarrage si vous utilisez un gestionnaire de mondes");
				
				regions.add(new Region(nextID(), name, world, rs.getBytes("container"), rs.getBytes("teams"), rs.getBytes("power")));
			}
		}
		catch (Exception e)
		{
			gv.getLogger().severe("Impossible de lire les arenes depuis la BDD");
			
			throw e;
		}
		finally
		{
			gv.databaseHelper.closeResultSet(rs);
		}
	}
	
	public boolean createRegion(String name, AreaContainer container)
	{		
		try
		{
			World world = container.getWorld();
			Location spawn = BukkitUtils.calculateAverageLocation(new Location[] { container.getMaximumPoint(), container.getMaximumPoint() });
			
			Region reg = new Region(nextID(), name, container, spawn);
			regions.add(reg);
			
			gv.databaseHelper.prepare("INSERT INTO regions(id, name, world, container, teams, power) VALUES(NULL, '" + name + "', '" + world.getName() + "', ?, ?, ?)");
			gv.databaseHelper.getPrepared().setBytes(1, container.serialize());
			gv.databaseHelper.getPrepared().setBytes(2, reg.serializeTeamMap());
			gv.databaseHelper.getPrepared().setBytes(3, reg.serializePowerLocation());
			gv.databaseHelper.finalize();
		}
		catch (Exception e)
		{
			gv.getLogger().warning("Impossible de creer l'arene");
			e.printStackTrace();
			
			return false;
		}
		
		return true;
	}
	
	public boolean renameRegion(Region reg, String name)
	{
		try
		{
			gv.databaseHelper.execute("UPDATE regions SET name='" + name + "' WHERE name " + GVDatabaseHelper.NOCASE + " = '" + reg.getName() + "'");
			
			reg.setName(name);
		}
		catch (Exception e)
		{
			gv.getLogger().warning("Un probleme est survenu en renommant l'arene");
			e.printStackTrace();
			
			return false;
		}
		
		return true;
	}
	
	public boolean setRegionLocation(Region reg, AreaContainer container)
	{		
		try
		{			
			for (Entry<Team, Location> entry : reg.getTeamSpawns().entrySet())
			{
				final Team team = entry.getKey();
				final Location loc = entry.getValue();
				
				if (loc.getWorld() != container.getWorld() || (gv.configurationHandler.spawnInsideRegion && !container.contains(loc)))
					setRegionSpawn(reg, team, BukkitUtils.calculateAverageLocation(new Location[] { container.getMinimumPoint(), container.getMaximumPoint() }));
			}
			
			reg.setContainer(container);
			
			gv.databaseHelper.prepare("UPDATE regions SET world='" + container.getWorld().getName() + "', container=? WHERE name " + GVDatabaseHelper.NOCASE + " = '" + reg.getName() + "'");
			gv.databaseHelper.getPrepared().setBytes(1, container.serialize());
			gv.databaseHelper.finalize();
		}
		catch (Exception e)
		{
			gv.getLogger().warning("Un probleme est survenu en changeant l'emplacement de l'arene");
			e.printStackTrace();
			
			return false;
		}
		
		return true;
	}
	
	public boolean setRegionSpawn(Region reg, Team team, Location spawn)
	{
		try
		{
			reg.setTeamSpawn(team, spawn);
			
			gv.databaseHelper.prepare("UPDATE regions SET teams=? WHERE name " + GVDatabaseHelper.NOCASE + " = '" + reg.getName() + "'");
			gv.databaseHelper.getPrepared().setBytes(1, reg.serializeTeamMap());
			gv.databaseHelper.finalize();
		}
		catch (Exception e)
		{
			gv.getLogger().warning("Un probleme est survenu en changeant le spawn de l'arene");
			e.printStackTrace();
			
			return false;
		}
		
		return true;
	}
	
	public boolean deleteRegionSpawn(Region reg, Team team)
	{
		try
		{
			reg.deleteTeamSpawn(team);
			
			gv.databaseHelper.prepare("UPDATE regions SET teams=? WHERE name " + GVDatabaseHelper.NOCASE + " = '" + reg.getName() + "'");
			gv.databaseHelper.getPrepared().setBytes(1, reg.serializeTeamMap());
			gv.databaseHelper.finalize();
		}
		catch (Exception e)
		{
			gv.getLogger().warning("Un probleme est survenu en supprimant le spawn de l'arene");
			e.printStackTrace();
			
			return false;
		}
		
		return true;
	}
	
	public boolean setRegionPowerLocation(Region reg, Location loc)
	{		
		try
		{
			reg.setPowerLocation(loc);
			
			gv.databaseHelper.prepare("UPDATE regions SET power=? WHERE name " + GVDatabaseHelper.NOCASE + " = '" + reg.getName() + "'");
			gv.databaseHelper.getPrepared().setBytes(1, reg.serializePowerLocation());
			gv.databaseHelper.finalize();
		}
		catch (Exception e)
		{
			gv.getLogger().warning("Un probleme est survenu lors du changement du bloc de courant de l'arene");
			e.printStackTrace();
			
			return false;
		}
		
		return true;
	}
	
	public boolean deleteRegion(Region reg)
	{		
		try
		{
			gv.databaseHelper.execute("DELETE FROM regions WHERE name " + GVDatabaseHelper.NOCASE + " = '" + reg.getName() + "'");
			
			final int id = reg.getID();
			regions.remove(reg);
			--nextID;
			
			for (int i = id + 1 ; i <= nextID ; i++)
				getRegion(i).decreaseID();
		}
		catch (Exception e)
		{
			gv.getLogger().warning("Un probleme est survenu lors de la suppression de l'arene");
			e.printStackTrace();
			
			return false;
		}
		
		return true;
	}
	
	public Region getRegion(int id)
	{
		for (Region reg : regions)
		{
			if (reg.getID() == id)
				return reg;
		}
		
		return null;
	}
	
	public Region getRegion(String name)
	{
		for (Region reg : regions)
		{
			if (reg.getName().equalsIgnoreCase(name))
				return reg;
		}
		
		return null;
	}
	
	public Region findRegion(String ref) throws Exception
	{
		Region reg = null;
		
		if (StringUtils.isNumeric(ref))
		{
			int id = 0;
			
			try
			{
				id = Integer.parseInt(ref);
				reg = getRegion(id);
				
				if (reg == null)
					throw new Exception("Aucune arène ne porte l'ID \"" + id + "\"");
			}
			catch (NumberFormatException e)
			{
				throw new Exception("Ce nombre est invalide ou trop grand");
			}
		}
		else
		{
			reg = getRegion(ref);
			
			if (reg == null)
				throw new Exception("L'arène \"" + ref + "\" n'existe pas");
		}
		
		return reg;
	}
}

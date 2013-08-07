package com.avygeil.GrandVide.game;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.avygeil.GrandVide.GVDatabaseHelper;
import com.avygeil.GrandVide.GrandVide;

public class Statistics
{
	private static Map<String, Statistics> cachedStats = new HashMap<String, Statistics>();
	
	public static Statistics getStats(String playerName)
	{
		if (!cachedStats.containsKey(playerName.toLowerCase()))
			cachedStats.put(playerName.toLowerCase(), pullStats(playerName));
		
		return cachedStats.get(playerName.toLowerCase());
	}
	
	public static void saveAll()
	{
		for (Entry<String, Statistics> entry : cachedStats.entrySet())
		{
			final String playerName = entry.getKey();
			final Statistics stats = entry.getValue();
			
			if (stats.wasChanged())
			{
				pushStats(playerName, stats);
				stats.save();
			}
		}
	}
	
	public static void clear()
	{
		cachedStats.clear();
	}
	
	private static void pushStats(String playerName, Statistics stats)
	{
		try
		{
			GrandVide.getInstance().databaseHelper.execute("UPDATE stats SET kills=" + stats.getKills() + ", deaths=" + stats.getDeaths() + ", damage_dealt=" + stats.getDamageDealt() + ", damage_taken=" + stats.getDamageTaken() + ", block_break=" + stats.getBlockBreak() + ", block_place=" + stats.getBlockPlace() + ", games_joined=" + stats.getGamesJoined() + ", games_finished=" + stats.getGamesFinished() + " WHERE player " + GVDatabaseHelper.NOCASE + " = '" + playerName + "'");
		}
		catch (Exception e)
		{
			GrandVide.getInstance().getLogger().warning("Impossible de mettre a jour les stats de \"" + playerName + "\"");
			e.printStackTrace();
		}
	}
	
	private static Statistics pullStats(String playerName)
	{
		Statistics result = null;
		ResultSet rs = null;
		
		try
		{
			rs = GrandVide.getInstance().databaseHelper.query("SELECT * FROM stats WHERE player " + GVDatabaseHelper.NOCASE + " = '" + playerName + "'");
			
			if (rs.next())
			{
				final int kills = rs.getInt("kills");
				final int deaths = rs.getInt("deaths");
				final int damageDealt = rs.getInt("damage_dealt");
				final int damageTaken = rs.getInt("damage_taken");
				final int blockBreak = rs.getInt("block_break");
				final int blockPlace = rs.getInt("block_place");
				final int gamesJoined = rs.getInt("games_joined");
				final int gamesFinished = rs.getInt("games_finished");
				
				result = new Statistics(kills, deaths, damageDealt, damageTaken, blockBreak, blockPlace, gamesJoined, gamesFinished);
			}
			else
			{
				result = new Statistics();
				
				GrandVide.getInstance().databaseHelper.execute("INSERT INTO stats(id, player, kills, deaths, damage_dealt, damage_taken, block_break, block_place, games_joined, games_finished) VALUES(NULL, '" + playerName + "', '0', '0', '0', '0', '0', '0', '0', '0')");
			}
		}
		catch (Exception e)
		{
			GrandVide.getInstance().getLogger().warning("Impossible de recuperer les stats de \"" + playerName + "\"");
			e.printStackTrace();
		}
		finally
		{
			GrandVide.getInstance().databaseHelper.closeResultSet(rs);
		}
		
		return result;
	}
	
	private boolean changed;
	private int kills;
	private int deaths;
	private int damageDealt;
	private int damageTaken;
	private int blockBreak;
	private int blockPlace;
	private int gamesJoined;
	private int gamesFinished;
	
	public Statistics()
	{
		this(0, 0, 0, 0, 0, 0, 0, 0);
	}
	
	public Statistics(int kills, int deaths, int damageDealt, int damageTaken, int blockBreak, int blockPlace, int gamesJoined, int gamesFinished)
	{
		this.kills = kills;
		this.deaths = deaths;
		this.damageDealt = damageDealt;
		this.damageTaken = damageTaken;
		this.blockBreak = blockBreak;
		this.blockBreak = blockPlace;
		this.gamesJoined = gamesJoined;
		this.gamesFinished = gamesFinished;
	}
	
	public boolean wasChanged()
	{
		return changed;
	}
	
	public void save()
	{
		changed = false;
	}
	
	public void addKill()
	{
		++kills;
		changed = true;
	}
	
	public int getKills()
	{
		return kills;
	}
	
	public void addDeath()
	{
		++deaths;
		changed = true;
	}
	
	public int getDeaths()
	{
		return deaths;
	}
	
	public void addDamageDealt(int damage)
	{
		damageDealt += damage;
		changed = true;
	}
	
	public int getDamageDealt()
	{
		return damageDealt;
	}
	
	public void addDamageTaken(int damage)
	{
		damageTaken += damage;
		changed = true;
	}
	
	public int getDamageTaken()
	{
		return damageTaken;
	}
	
	public void addBlockBreak()
	{
		++blockBreak;
		changed = true;
	}
	
	public int getBlockBreak()
	{
		return blockBreak;
	}
	
	public void addBlockPlace()
	{
		++blockPlace;
		changed = true;
	}
	
	public int getBlockPlace()
	{
		return blockPlace;
	}
	
	public void addGameJoined()
	{
		++gamesJoined;
		changed = true;
	}
	
	public int getGamesJoined()
	{
		return gamesJoined;
	}
	
	public void addGameFinished()
	{
		++gamesFinished;
		changed = true;
	}
	
	public int getGamesFinished()
	{
		return gamesFinished;
	}
}

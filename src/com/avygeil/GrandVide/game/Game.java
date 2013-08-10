package com.avygeil.GrandVide.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.avygeil.GrandVide.GrandVide;
import com.avygeil.GrandVide.command.CommandException;
import com.avygeil.GrandVide.command.CommandStats;
import com.avygeil.GrandVide.region.Region;
import com.avygeil.util.BukkitUtils;

public class Game
{
	private ArrayList<Challenger> players;
	private Random seed;
	public HashMap<Team, Integer> teamCounts;
	
	private ScoreboardManager sm;
	private Scoreboard scoreboard;
	private Objective objective;
	
	private CommandSender initiating;
	private Region region;
	private long timeStart;
	
	public boolean isRunning;
	public boolean canJoin;
	
	public Game(CommandSender initiating, Region region)
	{
		this.initiating = initiating;
		this.region = region;
		timeStart = System.currentTimeMillis();
		
		players = new ArrayList<Challenger>();
		teamCounts = new HashMap<Team, Integer>();
		seed = new Random();
		
		sm = Bukkit.getScoreboardManager();
		scoreboard = sm.getNewScoreboard();
		objective = null;
		
		for (Team team : region.getTeams())
		{
			if (team.isPlayable())
				teamCounts.put(team, 0);
		}
		
		isRunning = false;
		canJoin = true;
	}
	
	public ArrayList<Challenger> getPlayers()
	{
		return players;
	}
	
	public Challenger getPlayer(Player player)
	{		
		for (Challenger challenger : players)
		{
			if (challenger.getPlayer().equals(player))
				return challenger;
		}
		
		return null;
	}
	
	public Challenger getPlayer(String name)
	{
		for (Challenger challenger : players)
		{
			if (challenger.getPlayer().getName().equalsIgnoreCase(name))
				return challenger;
		}
		
		return null;
	}
	
	public boolean isPlaying(Player player)
	{
		return getPlayer(player) != null;
	}
	
	public boolean addPlayer(Player player)
	{
		if (getPlayer(player) != null)
			return false;
		
		broadcast(player.getDisplayName() + ChatColor.YELLOW + " a rejoint le Grand Vide", Team.NOTEAM);
		
		final Team team = (canJoin) ? Team.NOTEAM : Team.SPECTATOR;
		Challenger c = new Challenger(player, team);
		c.teleport(region.getSpawn(team));
		c.resetState();
		players.add(c);
		Statistics.getStats(player.getName());
		
		if (isRunning)
			c.setScoreboard(scoreboard);
		
		return true;
	}
	
	public void removePlayer(Challenger c)
	{
		final Team team = c.getTeam();
		
		decreaseTeamCount(team);
		
		if (isRunning && team.isPlayable())
		{
			getScoreboardTeam(team).removePlayer(c.getPlayer());
			scoreboardUpdate(team);
		}
		
		c.setScoreboard(sm.getMainScoreboard());
		c.restoreSavedState();
		players.remove(c);
	}
	
	public void removeAll()
	{
		for (Challenger c : players)
		{
			c.restoreSavedState();
			c.setScoreboard(sm.getMainScoreboard());
		}
		
		players.clear();
	}
	
	public void kickPlayer(Challenger c)
	{
		c.getPlayer().sendMessage(ChatColor.RED + "Vous avez été kické de la partie");
		removePlayer(c);
		broadcast(c.getPlayer().getDisplayName() + ChatColor.YELLOW + " a été kické");
	}
	
	private void decreaseTeamCount(Team team)
	{
		shiftTeamCount(team, -1);
	}
	
	private void increaseTeamCount(Team team)
	{
		shiftTeamCount(team, 1);
	}
	
	private void shiftTeamCount(Team team, int relative)
	{
		if (!teamCounts.containsKey(team))
			return;
		
		teamCounts.put(team, teamCounts.get(team) + relative);
	}
	
	public boolean swapTeam(Challenger c, Team team)
	{
		return swapTeam(c, team, false);
	}
	
	public boolean swapTeam(Challenger c, Team team, boolean silent)
	{
		if (!region.hasTeam(team))
			return false;
			
		if (team == Team.NOTEAM && !canJoin)
			team = Team.SPECTATOR;
		
		final Team oldTeam = c.getTeam();
		
		decreaseTeamCount(oldTeam);
		increaseTeamCount(team);
		
		if (team.isPlayable())
		{
			if (!silent)
				broadcast(c.getPlayer().getDisplayName() + ChatColor.WHITE + " a rejoint votre équipe", team);
			
			if (isRunning)
			{
				getScoreboardTeam(team).addPlayer(c.getPlayer());
				scoreboardUpdate(team);
			}
		}
		
		c.setTeam(team);
		c.teleport(region.getSpawn(team));
		c.giveAccessories();
		
		if (oldTeam.isPlayable())
		{
			if (!silent)
				broadcast(c.getPlayer().getDisplayName() + ChatColor.WHITE + " a quitté votre équipe", oldTeam);
			
			if (isRunning)
			{
				getScoreboardTeam(oldTeam).removePlayer(c.getPlayer());
				scoreboardUpdate(oldTeam);
			}
		}
		
		if (!oldTeam.isPlayable() && team.isPlayable())
			c.getStats().addGameJoined();
		
		if (!silent)
			c.getPlayer().sendMessage("Vous êtes maintenant " + team.getColoredName());
		
		if (isRunning)
			checkGameState();
		
		return true;
	}
	
	public Team pickTeam()
	{
		int lowest = Integer.MAX_VALUE;
		
		for (Integer n : teamCounts.values())
		{
			if (n < lowest)
				lowest = n;
		}
		
		ArrayList<Team> candidates = new ArrayList<Team>();
		
		for (Entry<Team, Integer> entry : teamCounts.entrySet())
		{
			if (entry.getValue() == lowest)
				candidates.add(entry.getKey());
		}
		
		return candidates.get((candidates.size() == 1) ? 0 : seed.nextInt(candidates.size()));
	}
	
	public CommandSender getInitiating()
	{
		return initiating;
	}
	
	public void setInitiating(CommandSender initiating)
	{
		this.initiating = initiating;
	}
	
	public boolean isInitiatingPlaying()
	{
		return getPlayer(BukkitUtils.escapeConsoleName(initiating)) != null;
	}
	
	public Region getRegion()
	{
		return region;
	}
	
	public Scoreboard getScoreboard()
	{
		return scoreboard;
	}
	
	public boolean begin()
	{
		ArrayList<Team> playedTeams = new ArrayList<Team>();
		
		for (Entry<Team, Integer> entry : teamCounts.entrySet())
		{
			final Team team = entry.getKey();
			
			if (team.isPlayable())
			{
				if (teamCounts.get(team) > 0)
					playedTeams.add(team);
				else
					teamCounts.put(team, -1);
			}
		}
		
		if (playedTeams.size() < 2)
			return false;
		
		objective = scoreboard.registerNewObjective("counts", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName("Joueurs");
		
		for (Team team : playedTeams)
		{
			org.bukkit.scoreboard.Team sbTeam = scoreboard.registerNewTeam(team.getName().toLowerCase());
			sbTeam.setPrefix(team.getPrefixColor());
			sbTeam.setDisplayName(team.getName());
			sbTeam.setCanSeeFriendlyInvisibles(true);
			sbTeam.setAllowFriendlyFire(GrandVide.getInstance().configurationHandler.friendlyFire);
			
			scoreboardUpdate(team);
		}
		
		for (Challenger c : players)
		{
			if (c.getTeam().isPlayable())
				getScoreboardTeam(c.getTeam()).addPlayer(c.getPlayer());
			
			if (c.getTeam() == Team.NOTEAM)
				swapTeam(c, Team.SPECTATOR);
			
			c.setScoreboard(scoreboard);
		}
		
		if (!isInitiatingPlaying() && initiating instanceof Player && ((Player)initiating).isOnline())
			((Player)initiating).setScoreboard(scoreboard);
		
		broadcast(ChatColor.GOLD + "La partie va commencer !");
		
		canJoin = false;
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(GrandVide.getInstance(), new CountdownTask(this, 10), 40L);
		
		return true;
	}
	
	public void checkGameState()
	{
		ArrayList<Team> remainingTeams = new ArrayList<Team>();
		
		for (Entry<Team, Integer> entry : teamCounts.entrySet())
		{
			final Team team = entry.getKey();
			final int count = entry.getValue();
			
			if (count <= 0)
			{
				if (count == 0)
					broadcast(ChatColor.GOLD + "L'équipe " + team.getColoredName() + ChatColor.GOLD + " a été vaincue !");
				
				teamCounts.put(team, -1);
				continue;
			}
			
			remainingTeams.add(team);
		}
		
		if (remainingTeams.size() == 1)
		{
			isRunning = false;
			
			final Team winner = remainingTeams.get(0);
			teamCounts.put(winner, -1);
			
			broadcast(ChatColor.GOLD + "L'équipe " + winner.getColoredName() + ChatColor.GOLD + " a gagné !");
			
			final Location power = region.getPowerLocation();
			
			if (power != null)
				power.getBlock().setType(Material.AIR);
			
			for (org.bukkit.scoreboard.Team sbTeam : scoreboard.getTeams())
				sbTeam.unregister();
			
			objective.unregister();
			
			final int timeElapsed = (int)((System.currentTimeMillis() - timeStart)/60000L);
			broadcast(ChatColor.RED + "Durée de la partie : " + (timeElapsed > 1 ? timeElapsed + " minutes" : "1 minute ou moins"));
			
			for (Challenger c : players)
			{
				if (c.getTeam() == winner)
				{
					c.getStats().addGameFinished();
					swapTeam(c, Team.SPECTATOR, true);
				}
				
				c.setScoreboard(sm.getMainScoreboard());
			}
			
			if (!isInitiatingPlaying() && initiating instanceof Player && ((Player)initiating).isOnline())
				((Player)initiating).setScoreboard(sm.getMainScoreboard());
			
			for (Team team : teamCounts.keySet())
				region.getContainer().getWorld().spawnEntity(region.getSpawn(team), EntityType.FIREWORK);
			
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(GrandVide.getInstance(), new Runnable()
			{
				public void run()
				{
					for (Challenger c : players)
					{
						try
						{
							if (c.getPlayer().isOnline())
								new CommandStats(c.getPlayer(), null);
						}
						catch (CommandException ignore) {}
					}
					
					GrandVide.getInstance().gameManager.stop(true);
				}
			}, 400L);
		}
	}
	
	public long getTimeStart()
	{
		return timeStart;
	}
	
	public void broadcast(String msg, Team team)
	{
		for (Challenger c : players)
		{
			if (c.getTeam() == team)
				c.getPlayer().sendMessage(msg);
		}
	}
	
	public void broadcast(String msg)
	{
		for (Team team : region.getTeams())
			broadcast(msg, team);
		
		if (!isInitiatingPlaying())
			initiating.sendMessage(msg);
	}
	
	public void diffuseSound(Sound sound, float volume, float pitch)
	{
		for (Challenger c : players)
			c.getPlayer().playSound(c.getPlayer().getLocation(), sound, volume, pitch);
		
		if (isInitiatingPlaying())
			((Player)initiating).playSound(((Player)initiating).getLocation(), sound, volume, pitch);
	}
	
	private org.bukkit.scoreboard.Team getScoreboardTeam(Team team)
	{
		return scoreboard.getTeam(team.getName().toLowerCase());
	}
	
	private void scoreboardUpdate(Team team)
	{
		final int count = teamCounts.get(team);
		String teamStr = team.getColoredName() + ChatColor.RESET + " : ";
		
		if (count > 0)
		{		
			Score score = objective.getScore(Bukkit.getOfflinePlayer(teamStr));
			score.setScore(count);
		}
		else
		{
			scoreboard.resetScores(Bukkit.getOfflinePlayer(teamStr));
		}
	}
}

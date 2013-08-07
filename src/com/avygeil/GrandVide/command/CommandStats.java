package com.avygeil.GrandVide.command;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.avygeil.GrandVide.game.Statistics;

public class CommandStats extends Command
{
	String playerName;
	Statistics stats;
	
	public CommandStats(CommandSender sender, String[] args) throws CommandException
	{
		super(sender, args);
	}
	
	@Override
	protected String getPermission()
	{
		return "gv.stats";
	}
	
	@Override
	protected int[] getPossibleArgsCount()
	{
		return new int[] {0, 1};
	}
	
	@Override
	protected void process() throws CommandException
	{
		if (argsCount == 1)
		{
			Player player = gv.getServer().getPlayer(args[0]);
			
			if (player == null)
			{
				OfflinePlayer offlinePlayer = gv.getServer().getOfflinePlayer(args[0]);
				
				if (!offlinePlayer.hasPlayedBefore())
					throw new CommandException("Ce joueur ne s'est jamais connecté");
				
				playerName = offlinePlayer.getName();
			}
			else
			{
				playerName = player.getName();
			}
		}
		else
		{
			if (isSenderConsole())
				throw new CommandException("Le nom est obligatoire depuis la console");
			
			playerName = ((Player)sender).getName();
		}
		
		stats = Statistics.getStats(playerName);
	}
	
	@Override
	protected void run()
	{
		queueMessage(ChatColor.GOLD + "Statistiques de " + playerName);
		queueMessage(formatStat(true, "Joueurs tués", stats.getKills()));
		queueMessage(formatStat(true, "Morts", stats.getDeaths()));
		queueMessage(formatStat(false, "Dommages donnés", stats.getDamageDealt()));
		queueMessage(formatStat(false, "Dommages reçus", stats.getDamageTaken()));
		queueMessage(formatStat(true, "Blocs cassés", stats.getBlockBreak()));
		queueMessage(formatStat(true, "Blocs placés", stats.getBlockPlace()));
		queueMessage(formatStat(false, "Parties rejointes", stats.getGamesJoined()));
		queueMessage(formatStat(false, "Parties terminées", stats.getGamesFinished()));
		queueProcess();
	}
	
	private String formatStat(boolean overlay, String label, int value)
	{
		return (overlay ? ChatColor.DARK_GREEN : ChatColor.GREEN) + label + " : " + value;
	}
}

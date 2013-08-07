package com.avygeil.GrandVide;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.avygeil.GrandVide.command.CommandAbort;
import com.avygeil.GrandVide.command.CommandBegin;
import com.avygeil.GrandVide.command.CommandCreate;
import com.avygeil.GrandVide.command.CommandDelete;
import com.avygeil.GrandVide.command.CommandDeleteScript;
import com.avygeil.GrandVide.command.CommandDeleteSpawn;
import com.avygeil.GrandVide.command.CommandException;
import com.avygeil.GrandVide.command.CommandGlobalChat;
import com.avygeil.GrandVide.command.CommandHelp;
import com.avygeil.GrandVide.command.CommandJoin;
import com.avygeil.GrandVide.command.CommandKick;
import com.avygeil.GrandVide.command.CommandLeave;
import com.avygeil.GrandVide.command.CommandList;
import com.avygeil.GrandVide.command.CommandPlayers;
import com.avygeil.GrandVide.command.CommandRename;
import com.avygeil.GrandVide.command.CommandScripts;
import com.avygeil.GrandVide.command.CommandSetLocation;
import com.avygeil.GrandVide.command.CommandSetPower;
import com.avygeil.GrandVide.command.CommandSetScript;
import com.avygeil.GrandVide.command.CommandSetSpawn;
import com.avygeil.GrandVide.command.CommandStart;
import com.avygeil.GrandVide.command.CommandStats;
import com.avygeil.GrandVide.command.CommandSwap;
import com.avygeil.GrandVide.command.CommandTeleport;
import com.avygeil.GrandVide.command.CommandToggle;

public class GVCommandHandler implements CommandExecutor
{
	private final GrandVide gv;
	
	GVCommandHandler(GrandVide grandvide)
	{
		this.gv = grandvide;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (!sender.hasPermission("gv.basic"))
		{
			sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission d'utiliser cette commande");
			
			return true;
		}
		
		if (args.length == 0)
		{
			if (gv.gameManager.isPlaying())
			{
				sender.sendMessage(ChatColor.GREEN + "Un Grand Vide \"" + gv.gameManager.getCurrentGame().getRegion().getName() + "\" va commencer !");
				
				if (sender instanceof Player)
					sender.sendMessage(ChatColor.DARK_GREEN + "/gv join" + ChatColor.GREEN + " pour y participer");
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "Il n'y a aucune partie de Grand Vide en cours");
			}
			
			sender.sendMessage("Tapez " + ChatColor.GOLD + "/gv help " + ChatColor.RESET + "pour la liste des commandes");
			
			return true;
		}
			
		final String command = args[0].toLowerCase();
		final String[] parameters = (args.length > 1) ? Arrays.copyOfRange(args, 1, args.length) : null;
		
		try
		{
			switch (command)
			{
				case "help": new CommandHelp(sender, parameters); break;
				case "join": new CommandJoin(sender, parameters); break;
				case "leave": new CommandLeave(sender, parameters); break;
				case "g": new CommandGlobalChat(sender, parameters); break;
				case "toggle": new CommandToggle(sender, parameters); break;
				case "stats": new CommandStats(sender, parameters); break;
				case "start": new CommandStart(sender, parameters); break;
				case "abort": new CommandAbort(sender, parameters); break;
				case "swap": new CommandSwap(sender, parameters); break;
				case "begin": new CommandBegin(sender, parameters); break;
				case "tp": new CommandTeleport(sender, parameters); break;
				case "players": new CommandPlayers(sender, parameters); break;
				case "kick": new CommandKick(sender, parameters); break;
				case "create": new CommandCreate(sender, parameters); break;
				case "rename": new CommandRename(sender, parameters); break;
				case "setloc": new CommandSetLocation(sender, parameters); break;
				case "setspawn": new CommandSetSpawn(sender, parameters); break;
				case "delspawn": new CommandDeleteSpawn(sender, parameters); break;
				case "setpower": new CommandSetPower(sender, parameters); break;
				case "delete": new CommandDelete(sender, parameters); break;
				case "list": new CommandList(sender, parameters); break;
				case "setscript": new CommandSetScript(sender, parameters); break;
				case "delscript": new CommandDeleteScript(sender, parameters); break;
				case "scripts": new CommandScripts(sender, parameters); break;
				default: return false;
			}
		}
		catch (CommandException e)
		{
			sender.sendMessage(ChatColor.RED + e.getMessage());
		}
		
		return true;
	}
}

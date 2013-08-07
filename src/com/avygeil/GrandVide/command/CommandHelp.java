package com.avygeil.GrandVide.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommandHelp extends Command
{	
	public CommandHelp(CommandSender sender, String[] args) throws CommandException
	{
		super(sender, args);
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
			if (!(args[0].equalsIgnoreCase("player") || args[0].equalsIgnoreCase("animatoo") || args[0].equalsIgnoreCase("config")))
				throw new CommandException("\"" + args[0] + "\" n'est pas un paramètre valide");
			
			if (!sender.hasPermission("gv.help." + args[0]))
				throw new CommandException("Vous n'avez pas la permission d'utiliser cette commande");
		}
	}
	
	@Override
	protected void run()
	{
		if (argsCount == 0)
		{
			sender.sendMessage(ChatColor.GOLD + "" + ChatColor.ITALIC + gv.getDescription().getFullName() + " by Avygeil");
			sender.sendMessage(ChatColor.GOLD + "Commandes :");
			playerHelp("help player", "", "Les commandes joueur");
			animatooHelp("help animatoo", "", "Les commandes animatoo");
			configHelp("help config", "", "Les commandes de configuration");
		}
		else if (args[0].equalsIgnoreCase("player"))
		{
			sender.sendMessage(ChatColor.GREEN + "Commandes Joueur :");
			playerHelp("join", "", "Rejoindre la partie");
			playerHelp("leave", "", "Quitter la partie");
			playerHelp("g", "<message>", "Message global sur la partie");
			playerHelp("toggle", "", "(Dé)verouiller le chat équipe");
			playerHelp("stats", "[joueur]", "Consulter les statistiques");
			playerHelp("players", "", "Lister les joueurs");
		}
		else if (args[0].equalsIgnoreCase("animatoo"))
		{
			sender.sendMessage(ChatColor.DARK_AQUA + "Commandes Animatoo :");
			animatooHelp("start", "<nom/id>", "Organiser une partie");
			animatooHelp("abort", "", "Annuler la partie");
			animatooHelp("swap", "<joueur> <équipe>", "Changer un joueur d'équipe");
			animatooHelp("begin", "", "Démarrer la partie");
			animatooHelp("players", "", "Lister les joueurs");
			animatooHelp("kick", "<joueur>", "Exclure un joueur de la partie");
			animatooHelp("tp", "<nom/id> [équipe]", "Se téléporter à une arène");
			animatooHelp("list", "<nom/id>", "Lister les arènes");
			animatooHelp("scripts", "", "Lister les scripts de droppers");
		}
		else if (args[0].equalsIgnoreCase("config"))
		{
			sender.sendMessage(ChatColor.RED + "Commandes de configuration :");
			configHelp("create", "<nom> [monde coords]", "Créer une arène");
			configHelp("rename", "<nom/id> <nouveau nom>", "Renommer une arène");
			configHelp("setloc", "<nom/id> [monde coords]", "Changer d'emplacement");
			configHelp("setspawn", "<nom/id> <équipe> [x y z]", "Définir un spawn");
			configHelp("delspawn", "<nom/id> <équipe>", "Supprimer un spawn");
			configHelp("setpower", "<nom/id> [coords]", "Changer le bloc courant");
			configHelp("delete", "<nom/id>", "Supprimer une arène");
			configHelp("tp", "<nom/id> <équipe>", "Se téléporter à une arène");
			configHelp("list", "<nom/id>", "Lister les arènes ou leurs spawns");
			configHelp("setscript", "<nom> <rép.> <séquence>", "Définir un script");
			configHelp("delscript", "<nom>", "Supprimer un script");
			configHelp("scripts", "", "Lister les scripts de droppers");
		}
	}
	
	private void playerHelp(String cmd, String arguments, String desc)
	{
		describeCommand(ChatColor.GREEN, cmd, arguments, desc);
	}
	
	private void animatooHelp(String cmd, String arguments, String desc)
	{
		describeCommand(ChatColor.DARK_AQUA, cmd, arguments, desc);
	}
	
	private void configHelp(String cmd, String arguments, String desc)
	{
		describeCommand(ChatColor.RED, cmd, arguments, desc);
	}
	
	private void describeCommand(ChatColor prefix, String cmd, String parameters, String desc)
	{
		final String arguments = (parameters.equals("")) ? "" : " " + ChatColor.ITALIC + parameters;
		sender.sendMessage("- " + prefix + "/gv " + cmd + arguments + ChatColor.RESET + " : " + desc);
	}
}

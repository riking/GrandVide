package com.avygeil.GrandVide.command;

import org.bukkit.command.CommandSender;
import com.avygeil.GrandVide.game.Challenger;

public class CommandKick extends Command
{
	Challenger challenger;
	
	public CommandKick(CommandSender sender, String[] args) throws CommandException
	{
		super(sender, args);
	}
	
	@Override
	protected String getPermission()
	{
		return "gv.kick";
	}
	
	@Override
	protected int[] getPossibleArgsCount()
	{
		return new int[] {1};
	}
	
	@Override
	protected void process() throws CommandException
	{
		if (!gv.gameManager.isPlaying())
			throw new CommandException("Il n'y a aucune partie de Grand Vide en cours");
		
		challenger = gv.gameManager.getCurrentGame().getPlayer(args[0]);
		
		if (challenger == null)
			throw new CommandException("Ce joueur ne participe pas à la partie");
	}
	
	@Override
	protected void run()
	{
		gv.gameManager.getCurrentGame().kickPlayer(challenger);
		
		success("Joueur kické avec succès");
	}
}

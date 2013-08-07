package com.avygeil.GrandVide.command;

import org.bukkit.command.CommandSender;
import com.avygeil.GrandVide.game.Challenger;
import com.avygeil.GrandVide.game.Team;

public class CommandSwap extends Command
{
	private Challenger challenger;
	private Team team;
	
	public CommandSwap(CommandSender sender, String[] args) throws CommandException
	{
		super(sender, args);
	}
	
	@Override
	protected String getPermission()
	{
		return "gv.swap";
	}
	
	@Override
	protected int[] getPossibleArgsCount()
	{
		return new int[] {2};
	}
	
	@Override
	protected void process() throws CommandException
	{
		if (!gv.gameManager.isPlaying())
			throw new CommandException("Il n'y a aucune partie de Grand Vide en cours");
		
		challenger = gv.gameManager.getCurrentGame().getPlayer(args[0]);
		
		if (challenger ==  null)
			throw new CommandException("Ce joueur ne participe pas à la partie");
		
		team = Team.getByName(args[1]);
		
		if (team == null)
			throw new CommandException("Cette équipe n'existe pas");
		
		final Integer count = gv.gameManager.getCurrentGame().teamCounts.get(team);
		
		if (count != null && count == -1)
			throw new CommandException("Cette équipe est verouillée");
		
		if (challenger.getTeam() == team)
			throw new CommandException("Le joueur est déjà dans cette équipe");
		
		if (!gv.gameManager.getCurrentGame().canJoin && !gv.gameManager.getCurrentGame().isRunning)
			throw new CommandException("La partie est en train de commencer");
	}
	
	@Override
	protected void run() throws CommandException
	{
		if (!gv.gameManager.getCurrentGame().swapTeam(challenger, team))
			throw new CommandException("L'équipe " + team.getName() + " n'est pas définie pour cette arène");
		
		success("Le joueur " + challenger.getPlayer().getName() + " est maintenant " + team.getColoredName());
	}
}

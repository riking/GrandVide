package com.avygeil.GrandVide.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import com.avygeil.GrandVide.GrandVide;
import com.avygeil.GrandVide.command.CommandGlobalChat;
import com.avygeil.util.BukkitUtils;

public class Events implements Listener
{
	private final GrandVide gv;
	private final Game game;
	
	public Events(GrandVide gv, Game game)
	{
		this.gv = gv;
		this.game = game;
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		if ((e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.WALL_SIGN)
				&& e.getAction() == Action.RIGHT_CLICK_BLOCK
				&& BukkitUtils.signContainsLines((Sign)e.getClickedBlock().getState(), new String[] {"[GrandVide]", "Warp", game.getRegion().getName()}))
		{
			final Player player = e.getPlayer();
			
			try
			{
				if (!player.hasPermission("gv.sign.use"))
					throw new Exception("Vous n'avez pas la permission de faire cela");
				
				if (!game.isPlaying(player))
					throw new Exception("Vous ne participez pas à la partie");
				
				Team team = null;
				
				if (game.canJoin)
					team = game.pickTeam();
				else
					team = Team.SPECTATOR;
				
				game.swapTeam(game.getPlayer(player), team);
			}
			catch (Exception ex)
			{
				player.sendMessage(ChatColor.RED + ex.getMessage());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		Challenger c = game.getPlayer(e.getPlayer().getName());
		
		if (c != null)
		{
			c.setPlayer(e.getPlayer());
			e.getPlayer().setScoreboard(game.getScoreboard());
		}
		
		if (game.getInitiating().getName().equalsIgnoreCase(e.getPlayer().getName()))
		{
			game.setInitiating(e.getPlayer());
			
			if (!game.isInitiatingPlaying())
				e.getPlayer().setScoreboard(game.getScoreboard());
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBurn(BlockBurnEvent e)
	{
		if (!gv.configurationHandler.fireSpread && game.getRegion().getContainer().contains(e.getBlock().getLocation()))
			e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e)
	{
		if (game.getRegion().getContainer().contains(e.getBlock().getLocation()))
		{
			if (isAllowedAction(e.getPlayer()))
			{				
				Challenger c = game.getPlayer(e.getPlayer());
				
				if (c.getTeam().isPlayable())
					c.getStats().addBlockBreak();
			}
			else
			{
				e.setCancelled(true);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e)
	{
		if (game.getRegion().getContainer().contains(e.getBlock().getLocation()))
		{
			if (isAllowedAction(e.getPlayer()))
			{
				Challenger c = game.getPlayer(e.getPlayer());
				
				if (c.getTeam().isPlayable())
					c.getStats().addBlockPlace();
			}
			else
			{
				e.setCancelled(true);
				e.getPlayer().updateInventory();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onFoodLevelChange(FoodLevelChangeEvent e)
	{
		if (e.getEntity() instanceof Player && game.isPlaying((Player)e.getEntity()))
			e.setCancelled(!isAllowedAction((Player)e.getEntity()));
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent e)
	{
		if (e.getEntity() instanceof Player && game.isPlaying((Player)e.getEntity()))
		{
			if (!game.isRunning && e.getCause() == DamageCause.VOID)
			{
				Challenger c = game.getPlayer((Player)e.getEntity());
				c.teleport(game.getRegion().getSpawn(c.getTeam()));
			}
			
			e.setCancelled(!isAllowedAction((Player)e.getEntity()));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e)
	{
		if (e.getDamager() instanceof Player && e.getEntity() instanceof Player)
		{
			final Challenger a = game.getPlayer((Player)e.getDamager());
			final Challenger d = game.getPlayer((Player)e.getEntity());
			
			if (a != null && d != null && a.getTeam().isPlayable() && d.getTeam().isPlayable())
			{
				final int damage = (int)Math.ceil(e.getDamage());
				
				a.getStats().addDamageDealt(damage);
				d.getStats().addDamageTaken(damage);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent e)
	{
		if (game.isPlaying(e.getPlayer()))
		{
			final Challenger c = game.getPlayer(e.getPlayer());
			
			if (c.isForcedTeamChat())
			{
				StringBuilder sb = new StringBuilder();
				sb.append("(");
				sb.append(c.getTeam().getPrefixColor());
				sb.append(c.getPlayer().getName());
				sb.append(ChatColor.RESET);
				sb.append("): ");
				sb.append(ChatColor.AQUA + e.getMessage());
				
				final String msg = sb.toString();
				game.broadcast(msg, c.getTeam());
				gv.getLogger().info("[EQUIPE " + c.getTeam().getName().toUpperCase() + "] " + ChatColor.stripColor(msg));
			}
			else
			{
				try
				{
					new CommandGlobalChat(c.getPlayer(), new String[]{e.getMessage()});
				}
				catch (Exception ignore) {}
			}
			
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent e)
	{
		if (game.isPlaying(e.getEntity()))
		{
			final Challenger c = game.getPlayer(e.getEntity());
			final Team team = c.getTeam();
			
			if (!team.isPlayable())
			{
				e.setDeathMessage("");
				return;
			}
			
			final Player killer = e.getEntity().getKiller();
			
			if (killer != null)
			{
				final Challenger k = game.getPlayer(killer);
				
				if (k != null && k.getTeam().isPlayable())
					k.getStats().addKill();
			}
			
			c.getStats().addDeath();
			
			game.swapTeam(c, Team.SPECTATOR, true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerRespawn(PlayerRespawnEvent e)
	{
		if (game.isPlaying(e.getPlayer()))
			e.setRespawnLocation(game.getRegion().getSpawn(Team.SPECTATOR));
	}
	
	private boolean isAllowedAction(Player player)
	{
		final Challenger c = game.getPlayer(player);
		
		return c != null && game.isRunning && c.getTeam().isPlayable();
	}
}

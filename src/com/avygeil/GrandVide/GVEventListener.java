package com.avygeil.GrandVide;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dropper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Sign;

import com.avygeil.GrandVide.dropper.DropperManager;
import com.avygeil.GrandVide.region.Region;
import com.avygeil.util.StringUtils;

public class GVEventListener implements Listener
{
	private final GrandVide gv;
	
	GVEventListener(GrandVide grandvide)
	{
		this.gv = grandvide;
		
		gv.pm.registerEvents(this, gv);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSignChange(SignChangeEvent e)
	{		
		if (e.getLine(0).toLowerCase().contains("[GrandVide]".toLowerCase()))
		{
			final Player player = e.getPlayer();
			final String command = e.getLine(1);
			
			try
			{
				if (command.equalsIgnoreCase("Warp"))
				{
					if (!player.hasPermission("gv.sign.create"))
						throw new Exception("Vous n'avez pas la permission de faire cela");
					
					final Region reg = gv.regionManager.findRegion(e.getLine(2));
					
					if (gv.configurationHandler.spawnInsideRegion && !reg.getContainer().contains(e.getBlock().getLocation()))
						throw new Exception("Le panneau doit être dans l'arène");
					
					e.setLine(1, ChatColor.GOLD + "Warp");
					e.setLine(2, ChatColor.RED + reg.getName());
					e.setLine(3, "Cliquez ici !");
					
					player.sendMessage(ChatColor.GREEN + "Panneau d'équipe créé avec succès");
				}
				else if (command.equalsIgnoreCase("Dropper"))
				{
					if (!e.getPlayer().hasPermission("gv.dropper"))
						throw new Exception("Vous n'avez pas la permission de faire cela");
					
					Sign sign = (Sign)e.getBlock().getState().getData();
					Block attached = e.getBlock().getRelative(sign.getAttachedFace());
					
					if (!sign.isWallSign() || attached.getType() != Material.DROPPER)
						throw new Exception("Vous devez placer le panneau sur un dropper");
					
					Dropper dropper = (Dropper)attached.getState();
					final String type = e.getLine(2);
					
					if (type.equalsIgnoreCase("Scheduled"))
					{
						String[] parameters = e.getLine(3).split(":", 3);
						
						if (parameters.length != 3)
						{							
							ItemStack item = null;
							
							for (ItemStack current : dropper.getInventory().getContents())
							{
								if (current != null)
								{
									if (item != null)
										throw new Exception("Il y a trop d'objets");
									
									item = current.clone();
								}
							}
							
							if (item == null)
								throw new Exception("Format: \"delay:id:meta\"");
							
							final int delay = item.getAmount();
							final int id = item.getTypeId();
							final short meta = item.getDurability();
							e.setLine(3, delay + ":" + id + ":" + meta);
						}
						else
						{
							for (String parameter : parameters)
							{
								if (!StringUtils.isNumeric(parameter))
									throw new Exception("Vous devez entrer un nombre");
							}
						}
						
						e.setLine(2, ChatColor.RED + "Scheduled");
					}
					else if (type.equalsIgnoreCase("Event"))
					{						
						if (!gv.configurationHandler.scriptExists(e.getLine(3)))
							throw new Exception("Ce script n'existe pas");
						
						e.setLine(2, ChatColor.RED + "Event");
					}
					else
					{
						throw new Exception("Un dropper doit être Scheduled ou Event");
					}
					
					dropper.getInventory().clear();
					dropper.update();
					
					e.setLine(1, ChatColor.GOLD + "Dropper");
					player.sendMessage(ChatColor.GREEN + "Dropper créé avec succès");
				}
				else
				{
					throw new Exception("Panneau Grand Vide invalide");
				}
				
				e.setLine(0, ChatColor.GOLD + "[GrandVide]");
			}
			catch (Exception ex)
			{
				player.sendMessage(ChatColor.RED + ex.getMessage());
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInventoryOpen(InventoryOpenEvent e)
	{
		if (e.getInventory().getType() == InventoryType.DROPPER
				&& e.getInventory().getHolder() instanceof Dropper
				&& DropperManager.getSpecialDropper(((Dropper)e.getInventory().getHolder()).getBlock()) != null)
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e)
	{
		if (DropperManager.getSpecialDropper(e.getBlock()) != null)
		{
			if (!e.getPlayer().hasPermission("gv.dropper"))
				e.setCancelled(true);
			else			
				DropperManager.unregister(e.getBlock());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockRedstone(BlockRedstoneEvent e)
	{
		if (e.getBlock().getType() == Material.REDSTONE_TORCH_OFF
				|| e.getBlock().getType() == Material.REDSTONE_TORCH_ON)
		{
			final Block above = e.getBlock().getRelative(BlockFace.UP);
			String[] arguments = DropperManager.getSpecialDropper(above);
			
			if (arguments == null)
				return;
			
			if (e.getOldCurrent() == 0 && e.getNewCurrent() > 0
					&& !above.isBlockPowered()
					&& !DropperManager.isRegistered(above))
			{
				DropperManager.register(above, arguments);
			}
			else if (e.getOldCurrent() > 0 && e.getNewCurrent() == 0
					&& above.isBlockPowered()
					&& DropperManager.isRegistered(above))
			{
				DropperManager.unregister(above);
			}
		}
	}
}

package com.avygeil.GrandVide.game;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public enum Team
{
	NOTEAM((byte)-1, "Lobby", false, ChatColor.WHITE, DyeColor.WHITE),
	SPECTATOR((byte)0, "Spectateur", false, ChatColor.GRAY, DyeColor.GRAY),
	BLUE((byte)1, "Bleu", true, ChatColor.BLUE, DyeColor.LIGHT_BLUE),
	RED((byte)2, "Rouge", true, ChatColor.RED, DyeColor.RED),
	YELLOW((byte)3, "Jaune", true, ChatColor.YELLOW, DyeColor.YELLOW),
	GREEN((byte)4, "Vert", true, ChatColor.GREEN, DyeColor.LIME),
	ORANGE((byte)5, "Orange", true, ChatColor.GOLD, DyeColor.ORANGE),
	PURPLE((byte)6, "Violet", true, ChatColor.DARK_PURPLE, DyeColor.PURPLE);
	
	private final byte id;
	private final String name;
	private final boolean playable;
	private final String prefixColor;
	private final DyeColor dyeColor;
	private final Color color;
	private final ItemStack wool;
	private final ItemStack chestplate;
	
	private final static Map<Byte, Team> BY_ID = new HashMap<Byte, Team>();
	private final static Map<String, Team> BY_NAME = new HashMap<String, Team>();
	
	private Team(byte id, String name, boolean playable, ChatColor prefixColor, DyeColor dyeColor)
	{
		this.id = id;
		this.name = name;
		this.playable = playable;
		this.prefixColor = prefixColor.toString();
		this.dyeColor = dyeColor;
		color = dyeColor.getColor();
		
		if (playable)
		{
			wool = new ItemStack(Material.WOOL, 1, dyeColor.getWoolData());
			
			chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
			LeatherArmorMeta meta = (LeatherArmorMeta)chestplate.getItemMeta();
			meta.setColor(color);
			chestplate.setItemMeta(meta);
		}
		else
		{
			wool = null;
			chestplate = null;
		}
	}
	
	public byte getID()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean isPlayable()
	{
		return playable;
	}
	
	public String getPrefixColor()
	{
		return prefixColor;
	}
	
	public String getColoredName()
	{
		return prefixColor + name;
	}
	
	public DyeColor getDyeColor()
	{
		return dyeColor;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public ItemStack getWool()
	{
		return wool != null ? wool.clone() : null;
	}
	
	public ItemStack getChestplate()
	{
		return chestplate != null ? chestplate.clone() : null;
	}
	
	public static Team getByID(byte id)
	{
		return BY_ID.get(id);
	}
	
	public static Team getByName(String name)
	{
		return BY_NAME.get(name.toLowerCase());
	}
	
	static
	{
		for (Team t : values())
		{
			BY_ID.put(t.id, t);
			BY_NAME.put(t.name.toLowerCase(), t);
		}
	}
}

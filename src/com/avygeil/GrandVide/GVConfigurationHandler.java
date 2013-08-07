package com.avygeil.GrandVide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;

import com.avygeil.GrandVide.dropper.EventScript;
import com.avygeil.util.StringUtils;

public class GVConfigurationHandler
{
	private final GrandVide gv;
	private  ConfigurationSection config;
	
	public String sqlDriver;
	
	public String mysqlHost;
	public int mysqlPort;
	public String mysqlDatabase;
	public String mysqlUser;
	public String mysqlPassword;
	
	public boolean worldEdit;
	public boolean rollBack;
	public boolean clearItems;
	public boolean spawnInsideRegion;
	
	public boolean fireSpread;
	public boolean friendlyFire;
	private List<EventScript> scripts;
	
	GVConfigurationHandler(GrandVide grandvide)
	{
		this.gv = grandvide;
		scripts = new ArrayList<EventScript>();
		config = null;
	}
	
	public void load()
	{
		config = gv.getConfig();
		final Map<String, Object> defaultValues = new HashMap<String, Object>();
		
		defaultValues.put("general.sqlDriver", "sqlite");
		
		defaultValues.put("mysql.host", "localhost");
		defaultValues.put("mysql.port", 3306);
		defaultValues.put("mysql.database", "grandvide");
		defaultValues.put("mysql.user", "root");
		defaultValues.put("mysql.password", "password");
		
		defaultValues.put("region.worldEdit", true);
		defaultValues.put("region.rollBack", true);
		defaultValues.put("region.clearItems", true);
		defaultValues.put("region.spawnInsideRegion", true);

		defaultValues.put("game.fireSpread", false);
		defaultValues.put("game.friendlyFire", false);
		List<String> defaultScripts = new ArrayList<String>();
		defaultScripts.add("sample;true;60:374:0;1:384:0;1:372:0");
		defaultValues.put("game.scripts", defaultScripts);
		
		for (final Entry<String, Object> e : defaultValues.entrySet())
		{
			if (!config.contains(e.getKey()))
				config.set(e.getKey(), e.getValue());
		}
		
		gv.saveConfig();
		
		sqlDriver = config.getString("general.sqlDriver", "sqlite");
		
		if (!sqlDriver.equalsIgnoreCase("sqlite") && !sqlDriver.equalsIgnoreCase("mysql"))
		{
			gv.getLogger().warning("Driver SQL iconnu : \"" + sqlDriver + "\". GrandVide utilisera SQLite");
			sqlDriver = "sqlite";
		}
		
		mysqlHost = config.getString("mysql.host", "localhost");
		mysqlPort = config.getInt("mysql.port", 3306);
		mysqlDatabase = config.getString("mysql.database", "grandvide");
		mysqlUser = config.getString("mysql.user", "root");
		mysqlPassword = config.getString("mysql.password", "password");
		
		worldEdit = config.getBoolean("region.worldEdit", true);
		rollBack = config.getBoolean("region.rollBack", true);
		clearItems = config.getBoolean("region.clearItems", true);
		spawnInsideRegion = config.getBoolean("region.spawnInsideRegion", true);
		
		fireSpread = config.getBoolean("game.fireSpread", false);
		friendlyFire = config.getBoolean("game.friendlyFire", false);
		
		for (String script : config.getStringList("game.scripts"))
		{
			String[] defs = script.split(";", 3);
			
			try
			{
				final String name = defs[0];
				
				if (!StringUtils.isAlpha(name))
					throw new Exception("Nom invalide");
				
				if (scriptExists(name))
					throw new Exception("Ce script existe deja");
				
				final boolean repeat = Boolean.parseBoolean(defs[1]);
				final String sequence = defs[2];
				
				scripts.add(new EventScript(name, repeat, sequence));
			}
			catch (Exception e)
			{
				gv.getLogger().warning("Format de script invalide \"" + script + "\" : " + e.getMessage());
			}
		}
	}
	
	public void setScript(String name, boolean repeat, String sequence)
	{
		if (scriptExists(name))
			getScript(name).setName(name).setRepeat(repeat).setSequence(sequence);
		else
			scripts.add(new EventScript(name, repeat, sequence));
		
		updateConfigScripts();
	}
	
	public void deleteScript(String name)
	{
		scripts.remove(getScript(name));
		updateConfigScripts();
	}
	
	private void updateConfigScripts()
	{
		final List<String> scripts = new ArrayList<String>();
		
		for (EventScript script : this.scripts)
			scripts.add(new String(script.getName() + ";" + Boolean.toString(script.getRepeat()).toLowerCase() + ";" + script.getSequence()).trim());
		
		config.set("game.scripts", scripts);
		gv.saveConfig();
	}
	
	public List<EventScript> getScripts()
	{
		return scripts;
	}
	
	public EventScript getScript(String name)
	{
		for (EventScript script : scripts)
		{
			if (script.getName().equalsIgnoreCase(name))
				return script;
		}
		
		return null;
	}
	
	public boolean scriptExists(String name)
	{
		return getScript(name) != null;
	}
}

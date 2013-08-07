package com.avygeil.GrandVide;

import java.lang.reflect.Field;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.avygeil.GrandVide.game.Statistics;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import de.diddiz.LogBlock.LogBlock;

public class GrandVide extends JavaPlugin
{
	private static GrandVide instance = null;
	
	public WorldEditPlugin worldEdit;
	public LogBlock logBlock;
	public GVDatabaseHelper databaseHelper;
	public GVConfigurationHandler configurationHandler;
	public GVCommandHandler commandHandler;
	public GVEventListener eventListener;
	public GVRegionManager regionManager;
	public GVGameManager gameManager;
	
	public PluginManager pm;
	
	private boolean failedToLoad = false;
	
	public static GrandVide getInstance()
	{
		return instance;
	}
	
	@Override
	public void onLoad()
	{
		instance = this;
		
		configurationHandler = new GVConfigurationHandler(this);
		configurationHandler.load();
		
		databaseHelper = new GVDatabaseHelper(this);
		
		try
		{
			databaseHelper.setConnection();
			databaseHelper.execute(GVDatabaseHelper.DB_REGIONS_SCHEME);
			databaseHelper.execute(GVDatabaseHelper.DB_STATS_SCHEME);
		}
		catch (Exception e)
		{
			getLogger().severe("Erreur lors de la mise en place de la BDD :");
			e.printStackTrace();
			
			failedToLoad = true;
		}
	}
	
	@Override
	public void onEnable()
	{		
		pm = getServer().getPluginManager();
		
		if (failedToLoad)
		{
			getLogger().severe("Des erreurs sont survenues au chargement");
			killPlugin();
			
			return;
		}
		
		if (configurationHandler.worldEdit)
		{
			worldEdit = (WorldEditPlugin)pm.getPlugin("WorldEdit");
			
			if (worldEdit == null)
			{
				configurationHandler.worldEdit = false;
				getLogger().warning("WorldEdit non detecte, les selections ont ete desactivees");
			}
		}
		
		if (configurationHandler.rollBack)
		{
			logBlock = (LogBlock)pm.getPlugin("LogBlock");
			
			if (logBlock == null)
			{
				configurationHandler.rollBack = false;
				getLogger().warning("LogBlock non detecte, le rollback a ete desactive");
			}
			else if (!logBlockHasDatabase(logBlock))
			{
				configurationHandler.rollBack = false;
				getLogger().warning("LogBlock n'est pas connecte a MySQL, le rollback a ete desactive");
			}
			else if (!configurationHandler.worldEdit)
			{
				configurationHandler.rollBack = false;
				getLogger().warning("WorldEdit est inactif, le rollback a ete desactive");
			}
		}
		
		regionManager = new GVRegionManager(this);
		
		try
		{
			regionManager.readRegions();
		}
		catch (Exception e)
		{
			getLogger().severe("Erreur lors de la lecture des arenes :");
			e.printStackTrace();
			killPlugin();
			
			return;
		}
		
		getLogger().info("Arenes chargees: " + regionManager.getRegions().size());
		
		commandHandler = new GVCommandHandler(this);
		eventListener = new GVEventListener(this);
		gameManager = new GVGameManager(this);
		
		getCommand("gv").setExecutor(commandHandler);
	}
	
	@Override
	public void onDisable()
	{
		if (gameManager != null)
			gameManager.stop(false);
		
		Statistics.clear();
		databaseHelper.closeConnection();
	}
	
	private void killPlugin()
	{
		getLogger().severe("Erreur fatale: le plugin doit quitter");
		pm.disablePlugin(this);
	}
	
	private boolean logBlockHasDatabase(LogBlock lb)
	{
		try
		{
			Field f = lb.getClass().getDeclaredField("noDb");
			f.setAccessible(true);
			boolean noDb = f.getBoolean(lb);
			
			return !noDb;
		}
		catch (Exception e)
		{
			getLogger().warning("Impossible de recuperer les informations de base de donnees de LogBlock: il est possible que le rollback de ne fonctionne pas");
		}
		
		return true;
	}
}

package com.avygeil.GrandVide;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GVDatabaseHelper
{
	private final GrandVide gv;
	
	private final String url;
	private final String driverClass;
	private final String user;
	private final String password;
	
	public static String DB_REGIONS_SCHEME;
	public static String DB_STATS_SCHEME;
	public static String AUTOINCREMENT;
	public static String NOCASE;
	
	private Connection connection = null;
	private Statement statement = null;
	private PreparedStatement prepared = null;

	GVDatabaseHelper(GrandVide grandvide)
	{
		this.gv = grandvide;
		
		if (gv.configurationHandler.sqlDriver.equalsIgnoreCase("sqlite"))
		{
			url = "jdbc:sqlite:plugins/GrandVide/grandvide.db";
			driverClass = "org.sqlite.JDBC";
			user = "";
			password = "";
			AUTOINCREMENT = "AUTOINCREMENT";
			NOCASE = "COLLATE NOCASE";
		}
		else if (gv.configurationHandler.sqlDriver.equalsIgnoreCase("mysql"))
		{
			url = "jdbc:mysql://" + gv.configurationHandler.mysqlHost + ":" + gv.configurationHandler.mysqlPort + "/" + gv.configurationHandler.mysqlDatabase;
			driverClass = "com.mysql.jdbc.Driver";
			user = gv.configurationHandler.mysqlUser;
			password = gv.configurationHandler.mysqlPassword;
			AUTOINCREMENT = "AUTO_INCREMENT";
			NOCASE = "";
		}
		else
		{
			url = null;
			driverClass = null;
			user = null;
			password = null;
			AUTOINCREMENT = null;
			NOCASE = null;
		}
		
		DB_REGIONS_SCHEME = "CREATE TABLE IF NOT EXISTS " + gv.configurationHandler.mysqlPrefix + "regions(id INTEGER PRIMARY KEY " + AUTOINCREMENT + ", name TEXT, world TEXT, container BLOB, teams BLOB, power BLOB)";
		DB_STATS_SCHEME = "CREATE TABLE IF NOT EXISTS " + gv.configurationHandler.mysqlPrefix + "stats(id INTEGER PRIMARY KEY " + AUTOINCREMENT + ", player TEXT, kills INTEGER, deaths INTEGER, damage_dealt INTEGER, damage_taken INTEGER, block_break INTEGER, block_place INTEGER, games_joined INTEGER, games_finished INTEGER)";
	}
	
	public void setConnection() throws Exception
	{
		Class.forName(driverClass);
		
		try
		{
			gv.getLogger().info("Connexion a " + url + "...");
			connection = DriverManager.getConnection(url, user, password);
		}
		catch (SQLException e)
		{
			gv.getLogger().severe("Impossible d'etablir la connexion a la base de donnees");
			throw e;
		}
		
		try
		{
			statement = connection.createStatement();
		}
		catch (Exception e)
		{
			try { connection.close(); } catch (Exception ignore) {}
			connection = null;
			
			gv.getLogger().severe("Une erreur s'est produite avec la base de donnees");
			throw e;
		}
	}
	
	public void closeConnection()
	{
		if (statement != null)
			try { statement.close(); } catch (Exception ignore) {}
		
		if (connection != null)
			try { connection.close(); } catch (Exception ignore) {}
	}
	
	public void closeResultSet(ResultSet rs)
	{
		if (rs != null)
			try { rs.close(); } catch (Exception ignore) {}
	}
	
	public void execute(String instruction) throws Exception
	{
		try
		{
			statement.executeUpdate(instruction);
		}
		catch (SQLException e)
		{
			gv.getLogger().warning("La demande SQL n'a pas pu etre executee");
			
			throw new Exception(e.getMessage());
		}
	}
	
	public ResultSet query(String query) throws Exception
	{
		try
		{
			return statement.executeQuery(query);
		}
		catch (SQLException e)
		{
			gv.getLogger().warning("La requete SQL n'a pas pu etre executee");
			
			throw new Exception(e.getMessage());
		}
	}
	
	public PreparedStatement getPrepared()
	{
		return prepared;
	}
	
	public void prepare(String query) throws Exception
	{
		try
		{
			prepared = connection.prepareStatement(query);
		}
		catch (SQLException e)
		{
			gv.getLogger().warning("La requete preparee SQL n'a pas pu etre executee");
			
			throw new Exception(e.getMessage());
		}
	}
	
	public void finalize() throws Exception
	{
		try
		{
			prepared.execute();
		}
		catch (SQLException e)
		{
			gv.getLogger().warning("La requete preparee SQL n'a pas pu etre finalisee");
			
			throw new Exception(e.getMessage());
		}
		finally
		{
			if (prepared != null)
				try { prepared.close(); } catch (Exception ignore) {}
		}
	}
}

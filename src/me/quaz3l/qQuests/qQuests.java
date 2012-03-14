package me.quaz3l.qQuests;

import java.util.logging.Logger;

import me.quaz3l.qQuests.API.QuestAPI;
import me.quaz3l.qQuests.API.Listeners.Craft;
import me.quaz3l.qQuests.API.Listeners.Damage;
import me.quaz3l.qQuests.API.Listeners.Destroy;
import me.quaz3l.qQuests.API.Listeners.Distance;
import me.quaz3l.qQuests.API.Listeners.GoTo;
import me.quaz3l.qQuests.API.Listeners.Kill;
import me.quaz3l.qQuests.API.Listeners.Kill_Player;
import me.quaz3l.qQuests.API.Listeners.Place;
import me.quaz3l.qQuests.Plugins.cmd;
import me.quaz3l.qQuests.Util.Chat;
import me.quaz3l.qQuests.Util.Config;
import me.quaz3l.qQuests.Util.Interwebs;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class qQuests extends JavaPlugin 
{
	// General Setup
	public static qQuests plugin;
	public final Logger logger = Logger.getLogger(("Minecraft"));
	public Config Config = new Config(this);
	public Integer qPlugins = 0;
	public QuestAPI qAPI;
	
	// Economy
	public Economy economy = null;
	
	// Prefixes
	public final String chatPrefix = ChatColor.AQUA + "[" + ChatColor.LIGHT_PURPLE + "qQuests" + ChatColor.AQUA + "] " + ChatColor.LIGHT_PURPLE + " ";
	public final String prefix = "[qQuests] ";
	
	
	public qQuests() {
		super();
		qQuests.plugin = this;
	}
	@Override
	public void onDisable() 
	{
		Chat.logger("info", "v" + this.getDescription().getVersion() + " by Quaz3l: Disabled");
	}

	@Override
	public void onEnable() 
	{
		// Initialize The Configuration File
		Config.initializeQuestConfig();
		Config.initializeConfig();
		Config.dumpQuestConfig();
		
		Interwebs.pingStatus();
		
		// Check For Update
		if(Interwebs.checkForUpdates())
			getServer().getPluginManager().disablePlugin(this);
		else
		{
			// Get The API
			this.qAPI = new QuestAPI();
			
			// Find Economy
			this.startEconomy();
			
			// Register Events
			this.registerEvents();
			
			// Build Quests
			qAPI.getQuestWorker().buildQuests();
			//Chat.logger("info", "" + qAPI.getQuestWorker().getQuests().get("d").onJoin().amounts().get(0));
			
			//Start Stock qPlugins
			stockPlugins();
			
			// Notify Logger
			Chat.logger("info", this.qPlugins + " qPlugins Linked.");
			Chat.logger("info", "v" + this.getDescription().getVersion() + " by Quaz3l: Enabled");
		}
	}
	// Hooks Into The Economy
	private void startEconomy()
	{
		if(getServer().getPluginManager().isPluginEnabled("Vault")) {
			RegisteredServiceProvider<Economy> economyProvider =
					getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (economyProvider != null) {
				this.economy = economyProvider.getProvider();
				Chat.logger("info", "[Economy] Enabled - Vault And Economy Plugin Found.");
			}
			else
			{
				Chat.logger("warning", "################################################################");
				Chat.logger("warning", "[Economy] Disabled - Economy Plugin Not Found! Go Dowmnload An Economy Plugin From: http://dev.bukkit.org/server-mods/iconomy");
				Chat.logger("warning", "################################################################");
			}
		}
		else
		{
			Chat.logger("warning", "################################################################");
			Chat.logger("warning", "[Economy] Disabled - Vault Not Found! Go Download Vault From: http://dev.bukkit.org/server-mods/vault");
			Chat.logger("warning", "################################################################");
		}
	}
	// Register Events
	private void registerEvents()
	{
		// Collect Is Handled By The Command
		getServer().getPluginManager().registerEvents(new Damage(), this);
		getServer().getPluginManager().registerEvents(new Destroy(), this);
		getServer().getPluginManager().registerEvents(new Place(), this);
		
		getServer().getPluginManager().registerEvents(new Distance(), this);
		getServer().getPluginManager().registerEvents(new GoTo(), this);
		
		getServer().getPluginManager().registerEvents(new Kill_Player(), this);
		getServer().getPluginManager().registerEvents(new Kill(), this);
		
		getServer().getPluginManager().registerEvents(new Craft(), this);
	}
	
	// Starts The Stock Plugins
	private void stockPlugins()
	{
		qPluginCommands();
		//qPluginSigns();
		//qPluginNPCs();
	}
	// Stock Plugins
	private void qPluginCommands()
	{
		// Setup Command Executors
		CommandExecutor cmd = new cmd();
			getCommand("Q").setExecutor(cmd);
			getCommand("QUEST").setExecutor(cmd);
			getCommand("QUESTS").setExecutor(cmd);
			getCommand("qQUESTS").setExecutor(cmd);
	}
	// To connect to qQuests put this function in your plugin
	// And "depend: [qQuests]" in your plugin.yml
	/*
	public qQuests qQuests = null;
	public QuestAPI qAPI = null;
	
	public void setupQQuests()
	  {
	    this.qQuests = (qQuests) getServer().getPluginManager().getPlugin("qQuests");

	    if (this.qAPI == null)
	      if (this.qQuests != null) {
	        this.qAPI = qQuests.qAPI;
	        System.out.println("qQuests found!");
	      } else {
	        System.out.println("qQuests not found. Disabling plugin.");
	        getServer().getPluginManager().disablePlugin(this);
	      }
	  }
	  */
}

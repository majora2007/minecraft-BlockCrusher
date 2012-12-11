package com.github.majora2007.blockcrusher;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.majora2007.blockcrusher.commandexecutors.CommandHandler;
/**
 * A Bukkit Plugin which upon a piston pushing a full stack of blocks into a non-breakable block, 
 * the last block is broken and an item is naturally dropped. 
 * 
 * The plugin is configurable to allow only specific blocks to be broken, by listing the block ids in 
 * the pluginConfig.yml file. 
 *
 * @author Majora2007
 */
public class BlockCrusher extends JavaPlugin
{
	public static Logger consoleLogger = Logger.getLogger("Minecraft");
	
	public static String pluginLogPrefix;
	public static FileConfiguration pluginConfig; //TODO Replace this immediately! Very bad!
	

	private final BlockCrusherBlockListener blockListener = new BlockCrusherBlockListener(this);
	private CommandHandler commandHandler;

	@Override
	public void onEnable()
	{
		PluginDescriptionFile pluginDescriptionFile = getDescription();
		pluginLogPrefix = "[" +  pluginDescriptionFile.getName() + " version: " + pluginDescriptionFile.getVersion() + "] - ";
		
		commandHandler = new CommandHandler(this);
		getServer().getPluginManager().registerEvents(this.blockListener, this);
		
		getCommand("blockcrusher").setExecutor(commandHandler);
		getCommand("bc").setExecutor( commandHandler );
		
		
		pluginConfig = this.getConfig();
		
		if (pluginConfig == null)
		{
			logAdd("pluginConfig.yml failed to load.");
		}
		
		
		logAdd("BlockCrusher has been enabled.");
	}

	@Override
	public void onDisable()
	{
		logAdd("BlockCrusher has been disabled.");
	}

	public static void logAdd(String logMessage) {
		consoleLogger.info(pluginLogPrefix + logMessage);
	}
}

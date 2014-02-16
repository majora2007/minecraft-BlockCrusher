/**
 * Distributed under The MIT License
 * http://www.opensource.org/licenses/MIT
 */
package com.github.majora2007.blockcrusher;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * A Bukkit Plugin which upon a piston pushing a full stack of blocks into a non-breakable block, 
 * the last block is broken and an item is naturally dropped. 
 * 
 * The plugin is configurable to allow only specific blocks to be broken, by listing the block ids in 
 * the pluginConfig.yml file. 
 *
 * @author Majora2007
 */
public final class BlockCrusher extends JavaPlugin
{
	public static Logger consoleLogger = Logger.getLogger("Minecraft");
	public static String pluginLogPrefix;
	

	private final BlockListener blockListener = new BlockListener(this);

	@Override
	public void onEnable()
	{
		// Save a copy of the default config.yml if one is not there
        this.saveDefaultConfig();
        
		declareLoggerPrefix();
		registerListeners();
	}


	@Override
	public void onDisable()
	{
		saveDefaultConfig();
	}

	public static void log(final String logMessage) 
	{
		consoleLogger.info(pluginLogPrefix + logMessage);
	}
	
	private void registerListeners()
	{
		registerBlockListener();
	}


	private void registerBlockListener()
	{
		getServer().getPluginManager().registerEvents(this.blockListener, this);
	}
	
	private void declareLoggerPrefix()
	{
		final PluginDescriptionFile pluginDescriptionFile = getDescription();
		pluginLogPrefix = "[" +  pluginDescriptionFile.getName() + "]: ";
	}
}

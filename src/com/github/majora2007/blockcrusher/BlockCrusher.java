package com.github.majora2007.blockcrusher;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
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
public class BlockCrusher extends JavaPlugin
{
	public static Logger consoleLogger = Logger.getLogger("Minecraft");
	public static String pluginLogPrefix;
	public static FileConfiguration pluginConfig; //TODO Replace this immediately! Very bad!
	

	private final BlockCrusherBlockListener blockListener = new BlockCrusherBlockListener(this);

	@Override
	public void onEnable()
	{
		PluginDescriptionFile pluginDescriptionFile = getDescription();
		pluginLogPrefix = "[" +  pluginDescriptionFile.getName() + " version: " + pluginDescriptionFile.getVersion() + "] - ";

		getServer().getPluginManager().registerEvents(this.blockListener, this);
		
		
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

	/*
	 * TO execute a command, we check if the command is a player, then we check if the command is for this plugin, then 
	 * we parse the plugin command and send a message to player if command executed successfully with status.
	 * 	TO check if the command is from a player, we check if the sender is a player and if so we extract the command and player from the event.
	 * 	TO check if command is for this plugin, we test if command starts with "blockcrusher" or "bc".
	 * 	TO parse the command, we check arguments for proper usage syntax and if pass, we execute the command and send a status message to user.
	 * 	TO return, we return true if command executed properly or false otherwise.
	 * 
	 * (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		logAdd("BlockCrusher received a command.");
		
		if (isCommandFromPlayer(sender)) 
		{
			logAdd("Command is from Player.");
			String command = cmd.getName().toLowerCase();
			Player player = (Player) sender;
			
			if ( isBlockCrusherCommand(command) ) 
			{
				logAdd("Command is for BlockCrusher.");
				CommandResponse response;
				
				if ( checkUsage(args) )
				{
					response = parseCommand(args);
					player.sendMessage( response.getResponseMessage() );
					
					return true;
				} else {
					logAdd("Command is not proper form.");
				}
				
			}
		}
		return false;
	}
	
	boolean isCommandFromPlayer(CommandSender cmdSender)
	{
		return cmdSender instanceof Player;
	}
	
	CommandResponse parseCommand(String[] commandArguments)
	{
		CommandResponse response = new CommandResponse();
		
		String command = extractSubCommand( commandArguments );
		
		if (isReloadCommand(command))
		{
			BlockCrusherConfig.load();
			response.setResponseMessage( pluginLogPrefix + " Configuration Reloaded." );
		} else if (command.startsWith("help") || command.equals("h")) {
			response.setResponseMessage("/blockcrusher reload"); // This should display all help options
		} else {
			response.setResponseMessage( "Usage: /blockcrusher help" );
		}
		
		return response;
	}
	
	boolean isBlockCrusherCommand(String command)
	{
		return (command.startsWith( "blockcrusher" ) || command.equals( "bc" ));
	}
	
	boolean checkUsage(String[] args)
	{
		return (args != null && args.length > 0);
	}
	
	String extractSubCommand(String[] args)
	{
		assert(args.length > 0);
		
		return args[0];
	}
	
	boolean isReloadCommand(String subCommand) 
	{
		return (subCommand.equals("reload") || subCommand.equals("r")); 
	}
	
	
}

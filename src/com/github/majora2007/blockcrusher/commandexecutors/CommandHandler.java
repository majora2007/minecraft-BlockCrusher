/**
 * 
 */
package com.github.majora2007.blockcrusher.commandexecutors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.majora2007.blockcrusher.BlockCrusher;
import com.github.majora2007.blockcrusher.CommandResponse;

/**
 * Handles execution of the "blockcrusher"/"bc" command.
 * 
 * @author Majora2007
 *
 */
public class CommandHandler implements CommandExecutor
{
	private BlockCrusher plugin;
	
	
	/**
	 * @param plugin Owning plugin
	 * 
	 */
	public CommandHandler(final BlockCrusher plugin) {
		this.plugin = plugin;
		
		this.plugin.getCommand("blockcrusher").setExecutor( this );
		this.plugin.getCommand("bc").setExecutor( this );
	}
	
	/* (non-Javadoc)
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand( CommandSender sender, Command cmd, String cmdAlias, String[] arguments )
	{
		if (isCommandFromPlayer(sender)) 
		{
			String command = cmd.getName().toLowerCase();
			Player player = (Player) sender;
			
			if ( isBlockCrusherCommand(command) ) 
			{
				CommandResponse response;
				
				if ( checkUsage(arguments) )
				{
					response = parseCommand(arguments);
					player.sendMessage( response.getResponseMessage() );
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	
	CommandResponse parseCommand(String[] commandArguments)
	{
		CommandResponse response = new CommandResponse();
		
		String command = extractSubCommand( commandArguments );
		
		if (isReloadCommand(command))
		{
			plugin.reloadConfig();
			response.setResponseMessage( BlockCrusher.pluginLogPrefix + " Configuration Reloaded." );
		} else if (command.startsWith("help") || command.equals("h")) {
			response.setResponseMessage("Help: /blockcrusher reload"); // This should display all help options
		} else {
			response.setResponseMessage( "Usage: /blockcrusher help" );
		}
		
		return response;
	}
	
	boolean isCommandFromPlayer(CommandSender cmdSender)
	{
		return cmdSender instanceof Player;
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

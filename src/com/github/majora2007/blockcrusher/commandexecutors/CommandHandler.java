/**
 * Distributed under the The Non-Profit Open Software License version 3.0 (NPOSL-3.0)
 * http://www.opensource.org/licenses/NOSL3.0
 */
package com.github.majora2007.blockcrusher.commandexecutors;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.github.majora2007.blockcrusher.BlockCrusher;
import com.github.majora2007.blockcrusher.CommandResponse;

/**
 * Handles execution of the "blockcrusher"/"bc" command.
 * 
 * @author Majora2007
 *
 */
public class CommandHandler implements Listener
{
	private BlockCrusher parentPlugin;
	
	
	public CommandHandler(final BlockCrusher plugin) {
		this.parentPlugin = plugin;
	}
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		String[] commandArguments = event.getMessage().split( " " );

		if(!checkUsage(commandArguments)) return;
		
		if (!isBlockCrusherCommand(  commandArguments[0] )) return;
		event.setCancelled(true);

		Player player = event.getPlayer();
		
		CommandResponse response;
//		response = parseCommandAndCreateResponse(commandArguments);
//		player.sendMessage( response.getResponseMessage() );
		
		if (isReloadCommand( commandArguments[1] ))
		{
			parentPlugin.reloadConfig();
			player.sendMessage( BlockCrusher.pluginLogPrefix + "Configuration has been reloaded." );
		} else if (isHelpCommand( commandArguments[1] )) {
			player.sendMessage( this.parentPlugin.getCommand( "blockcrusher" ).getUsage() );
		}

	}
	
	
	
	CommandResponse parseCommandAndCreateResponse(String[] commandArguments)
	{
		CommandResponse response = new CommandResponse();
		
		
		if (isReloadCommand( commandArguments[1] ))
		{
			parentPlugin.reloadConfig();
			response.setResponseMessage( BlockCrusher.pluginLogPrefix + "Configuration has been reloaded." );
		} else if (isHelpCommand( commandArguments[1] )) {
			response.setResponseMessage( this.parentPlugin.getCommand( "blockcrusher" ).getUsage() );
		}
		
		return response;
	}

	private boolean isHelpCommand( String subCommand )
	{
		return (subCommand.equalsIgnoreCase( "help" ));
	}
	
	boolean isBlockCrusherCommand(String command)
	{
		return ((command.equalsIgnoreCase("/blockcrusher") || command.equalsIgnoreCase("/bc")));
	}
	
	boolean checkUsage(String[] args)
	{
		return (args != null && args.length >= 1);
	}
	
	boolean isReloadCommand(String subCommand) 
	{
		return (subCommand.equalsIgnoreCase("reload")); 
	}
	
}

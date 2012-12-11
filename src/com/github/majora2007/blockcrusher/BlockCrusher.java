package com.github.majora2007.blockcrusher;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
/**
 * BlockCrusher for Bukkit
 *
 * @author Dingmatt
 */
public class BlockCrusher extends JavaPlugin
{
	public static Logger log = Logger.getLogger("Minecraft");
	public static String PREFIX;
	public static FileConfiguration config; //TODO Replace this immediately! Very bad!
	

	private final BlockCrusherBlockListener blockListener = new BlockCrusherBlockListener(this);

	@Override
	public void onEnable()
	{
		PluginDescriptionFile pluginDescriptionFile = getDescription();
		PREFIX = "[" +  pluginDescriptionFile.getName() + " version: " + pluginDescriptionFile.getVersion() + "] - ";

		getServer().getPluginManager().registerEvents(this.blockListener, this);
		
		
		config = this.getConfig();
		
		if (config == null)
		{
			logAdd("config.yml failed to load.");
		}
		
		
		logAdd("BlockCrusher has been enabled.");
	}

	@Override
	public void onDisable()
	{
		logAdd("BlockCrusher has been disabled.");
	}

	public static void logAdd(String msg) {
		log.info(PREFIX + msg);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		logAdd("Test1");
		if (sender instanceof Player) {
			logAdd("Test2");
			String command = cmd.getName().toLowerCase();
			Player player = (Player)sender;
			if (command.equals("blockcrusher") || command.equals("bc")) {
				logAdd("Test3");
				String param1 = args.length > 0 ? args[0] : "";
				if (param1.equals("reload") || param1.equals("r")) {
					BlockCrusherConfig.load();
					player.sendMessage(PREFIX + "Config Reloaded");
				} else if (param1.equals("help") || param1.equals("h")) {
					player.sendMessage("/blockcrusher reload");
				}
				return true;
			}
		}
		return false;
	}
}

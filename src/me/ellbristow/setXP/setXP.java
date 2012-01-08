package me.ellbristow.setXP;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class setXP extends JavaPlugin {
	
	public static setXP plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled.");		
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args ) {
		if (sender instanceof Player) {
			// Command sent by player
			Boolean comreturn = readCommand((Player) sender, commandLabel, args);
			return comreturn;
		}
		else {
			// Command sent by console
			return consoleCommand(sender, commandLabel, args);
		}
	}
	
	public boolean readCommand(Player player, String command, String[] args) {
		if(command.equalsIgnoreCase("setxp")) {
			/*
			 *  Set XP level
			 */
			if (args.length == 0) {
				// No arguments sent, report error
				player.sendMessage(ChatColor.RED + "No level requested!");
			}
			else if (args.length == 1) {
				// 1 argument sent, apply level to player 
				int level = player.getLevel();
				// Check that level is an integer
				try {
					level = Integer.parseInt(args[0]);
				}
				catch(NumberFormatException nfe) {
					// Failed. Number not an integer
					player.sendMessage(ChatColor.RED + "Level must be a number!" );
					return false;
				}
				// Good to go!
				player.setLevel(level);
				player.sendMessage(ChatColor.GOLD + "XP level set to " + ChatColor.WHITE + player.getLevel());
				return true;
			}
			else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("add")) {
					// ADD levels to self
					if (!player.hasPermission("setxp.add")) {
						player.sendMessage(ChatColor.RED + "You do not have permission to add to your XP level!");
						return true;
					}
					int level;
					// Check that level is an integer
					try {
						level = Integer.parseInt(args[1]);
					}
					catch(NumberFormatException nfe) {
						// Failed. Number not an integer
						player.sendMessage(ChatColor.RED + "Level must be a number!" );
						return false;
					}
					player.setLevel(player.getLevel() + level);
					player.sendMessage(ChatColor.GOLD + "XP level set to " + ChatColor.WHITE + player.getLevel());
					return true;
				}
				else {
					// SET level or another player
					Player target = getServer().getPlayer(args[0]);
					// Check permission to set other players' level
					if (!player.hasPermission("setxp.setxp.others") && !player.isOp()) {
						player.sendMessage(ChatColor.RED + "You do not have permission to set another player's XP level!");
						return true;
					}
					// Check target player exists
					if (target == null) {
						// Target player not found 
						player.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + args[0] + ChatColor.RED + " not found or not online!");
						return false;
					}
					// Check if target layer is exempt from setXP
					if (target.hasPermission("setxp.exempt") && !player.hasPermission("setxp.override")) {
						player.sendMessage(target.getDisplayName() + ChatColor.RED + " is exempt from setXP!");
						return true;
					}
					int level;
					// Check that level is an integer
					try {
						level = Integer.parseInt(args[1]);
					}
					catch(NumberFormatException nfe) {
						// Failed. Number not an integer
						player.sendMessage(ChatColor.RED + "Level must be a number!" );
						return false;
					}
					// Good to go!
					target.setLevel(level);
					player.sendMessage(target.getDisplayName() + ChatColor.GOLD + " is now at XP level " + ChatColor.WHITE + target.getLevel());
					if (target.isOnline()) {
						// Target player is online, send message
						target.sendMessage(player.getDisplayName() + ChatColor.GOLD + " set your XP level to " + ChatColor.WHITE + target.getLevel());
					}
					return true;
				}
			}
			else if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
				// 2 arguments sent, apply level to another player
				Player target = getServer().getPlayer(args[1]);
				// Check permission to set other players' level
				if (!player.hasPermission("setxp.add.others") && !player.isOp()) {
					player.sendMessage(ChatColor.RED + "You do not have permission to add to another player's XP level!");
					return true;
				}
				// Check target player exists
				if (target == null) {
					// Target player not found 
					player.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + args[1] + ChatColor.RED + " not found or not online!");
					return false;
				}
				// Check if target layer is exempt from setXP
				if (target.hasPermission("setxp.exempt") && !player.hasPermission("setxp.override")) {
					player.sendMessage(target.getDisplayName() + ChatColor.RED + " is exempt from setXP!");
					return true;
				}
				int level;
				// Check that level is an integer
				try {
					level = Integer.parseInt(args[2]);
				}
				catch(NumberFormatException nfe) {
					// Failed. Number not an integer
					player.sendMessage(ChatColor.RED + "Level must be a number!" );
					return false;
				}
				// Good to go!
				target.setLevel(target.getLevel() + level);
				player.sendMessage(target.getDisplayName() + ChatColor.GOLD + " is now at XP level " + ChatColor.WHITE + target.getLevel());
				if (target.isOnline()) {
					// Target player is online, send message
					target.sendMessage(player.getDisplayName() + ChatColor.GOLD + " set your XP level to " + ChatColor.WHITE + target.getLevel());
				}
				return true;
			}
			return false;
		}
		else if (command.equalsIgnoreCase("getxp")) {
			/*
			 *  Fetch XP level of target player
			 */
			if (args.length == 0) {
				// No arguments sent
				player.sendMessage(ChatColor.RED + "You must specify a player!");
				return false;
			}
			Player target = getServer().getPlayer(args[0]);
			if (target == null) {
				// Target player not found
				player.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + args[0] + ChatColor.RED + " not found or not online!");
				return false;
			}
			// Good to Go!
			player.sendMessage(target.getDisplayName() + ChatColor.GOLD + " is at level " + ChatColor.WHITE + target.getLevel());
			return true;
		}
		return false;
	}
	
	public boolean consoleCommand(CommandSender sender, String command, String[] args) {
		if (command.equalsIgnoreCase("getxp")) {
			/*
			 * Fetch XP level of target player
			 */
			if (args.length == 0) {
				// No player requested
				sender.sendMessage(ChatColor.RED + "No target player specified!");
				return false;
			}
			Player target = getServer().getPlayer(args[0]);
			if (target == null) {
				// Player not found
				sender.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + args[0] + ChatColor.RED + " not found or not online!");
				return true;
			}
			sender.sendMessage(target.getDisplayName() + ChatColor.GOLD + "'s XP level is " + ChatColor.WHITE + target.getLevel());
			return true;
		}
		else if (command.equalsIgnoreCase("setxp")) {
			/*
			 * Set XP level of target player
			 */
			if (args[0].equalsIgnoreCase("add")) {
				// ADD levels
				if (args.length != 3) {
					// Incorrect number of arguments
					sender.sendMessage("/setxp add [player] [level]");
					return true;
				}
				Player target = getServer().getPlayer(args[1]);
				if (target == null) {
					// Player not found
					sender.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + args[1] + ChatColor.RED + " not found or not online!");
					return true;
				}
				int level;
				// Check that level is an integer
				try {
					level = Integer.parseInt(args[2]);
				}
				catch(NumberFormatException nfe) {
					// Failed. Number not an integer
					sender.sendMessage(ChatColor.RED + "Level must be a number!" );
					sender.sendMessage("/setxp add [player] [level]" );
					return true;
				}
				target.setLevel(target.getLevel() + level);
				sender.sendMessage(target.getDisplayName() + ChatColor.GOLD + "'s XP level is now " + ChatColor.WHITE + target.getLevel());
				target.sendMessage("SERVER " + ChatColor.GOLD + "set your XP level to " + ChatColor.WHITE + target.getLevel());
				return true;
			}
			else {
				// SET level
				if (args.length != 2) {
					// Incorrect number of arguments
					sender.sendMessage("/setxp [player] [level]");
					return true;
				}
				Player target = getServer().getPlayer(args[0]);
				if (target == null) {
					// Player not found
					sender.sendMessage(ChatColor.RED + "Player " + ChatColor.WHITE + args[0] + ChatColor.RED + " not found or not online!");
					return true;
				}
				int level;
				// Check that level is an integer
				try {
					level = Integer.parseInt(args[1]);
				}
				catch(NumberFormatException nfe) {
					// Failed. Number not an integer
					sender.sendMessage(ChatColor.RED + "Level must be a number!" );
					sender.sendMessage("/setxp [player] [level]" );
					return true;
				}
				target.setLevel(level);
				sender.sendMessage(target.getDisplayName() + ChatColor.GOLD + "'s XP level is now " + ChatColor.WHITE + target.getLevel());
				target.sendMessage("SERVER " + ChatColor.GOLD + "set your XP level to " + ChatColor.WHITE + target.getLevel());
				return true;
			}
		}
		else {
			sender.sendMessage("This command cannot be run in the console!");
			return true;
		}
	}
	
}

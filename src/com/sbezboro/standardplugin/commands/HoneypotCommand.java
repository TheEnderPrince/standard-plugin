package com.sbezboro.standardplugin.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.sbezboro.standardplugin.StandardPlugin;
import com.sbezboro.standardplugin.managers.HoneypotManager;
import com.sbezboro.standardplugin.util.MiscUtil;

public class HoneypotCommand extends BaseCommand {

	public HoneypotCommand(StandardPlugin plugin) {
		super(plugin, "honeypot");
	}

	@Override
	public boolean handle(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			showUsageInfo(sender);
			return false;
		}
		
		HoneypotManager honeypotManager = plugin.getHoneypotManager();

		if (args[0].equalsIgnoreCase("generate")) {
			
			if (args.length == 1) {
				honeypotManager.createHoneypot(null);
			} else if (args.length == 4) {
				World overworld = plugin.getServer().getWorld(StandardPlugin.OVERWORLD_NAME);
				
				try {
					int x = Integer.parseInt(args[1]);
					int y = Integer.parseInt(args[2]);
					int z = Integer.parseInt(args[3]);
					
					honeypotManager.createHoneypot(new Location(overworld, x, y, z));
				} catch (NumberFormatException e) {
					sender.sendMessage("Error parsing coordinates");
					return false;
				}
			} else {
				showUsageInfo(sender);
				return false;
			}
		} else if (args[0].equalsIgnoreCase("remove")) {
			Location location = honeypotManager.removeOldestHoneypot();
			
			if (location == null) {
				sender.sendMessage("No honeypots left to remove");
			} else {
				sender.sendMessage("Honeypot removed at " + MiscUtil.locationFormat(location));
			}
		}
		
		return true;
	}

	@Override
	public void showUsageInfo(CommandSender sender) {
		sender.sendMessage("Usage: /" + name + " generate [x y z]");
	}

	@Override
	public boolean isPlayerOnly(int numArgs) {
		return false;
	}

}
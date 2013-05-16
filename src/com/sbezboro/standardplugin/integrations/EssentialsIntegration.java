package com.sbezboro.standardplugin.integrations;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import com.sbezboro.standardplugin.StandardPlugin;

public class EssentialsIntegration extends PluginIntegration {
	private static final String CLASS_NAME = "com.earth2me.essentials.IEssentials";
	private static final String PLUGIN_NAME = "Essentials";
	private static IEssentials essentials;
	
	public static void init(StandardPlugin plugin) {
		essentials = init(plugin, CLASS_NAME, PLUGIN_NAME);
	}
	
	public static User getUser(String username) {
		if (!enabled) {
			return null;
		}
		return essentials.getUser(username);
	}
	
	public static boolean hasNickname(String username) {
		if (!enabled) {
			return false;
		}
		return getNickname(username) != null;
	}
	
	public static String getNickname(String username) {
		User user = getUser(username);
		if (user != null) {
			return user.getNickname();
		}
		
		return null;
	}
}
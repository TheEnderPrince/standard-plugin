package com.sbezboro.standardplugin.persistence.storages;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.sbezboro.standardplugin.StandardPlugin;
import com.sbezboro.standardplugin.persistence.PersistedObject;

public abstract class SingleFileStorage<T extends PersistedObject> implements FileStorage {
	protected StandardPlugin plugin;
	private String filename;
	
	protected HashMap<String, ConfigurationSection> idToConfig;
	protected HashMap<String, T> idToObject;

	protected FileConfiguration config = null;
	private File file = null;

	public SingleFileStorage(StandardPlugin plugin, String type) {
		this.plugin = plugin;
		this.filename = type + ".yml";

		idToObject = new HashMap<String, T>();
		idToConfig = new HashMap<String, ConfigurationSection>();
	}

	@Override
	public final void reload() {
		if (file == null) {
			file = new File(plugin.getDataFolder(), filename);
		}
		config = YamlConfiguration.loadConfiguration(file);

		InputStream configStream = plugin.getResource(filename);
		if (configStream != null) {
			InputStreamReader reader = new InputStreamReader(configStream);
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
			config.setDefaults(defConfig);
		}

		idToConfig.clear();
		idToObject.clear();
		
		Set<String> keys = config.getKeys(false);
		for (String key : keys) {
			try {
				ConfigurationSection section = config.getConfigurationSection(key);
				
				T object = createObject(key);
				
				idToObject.put(key, object);
				idToConfig.put(key, section);
				
				object.loadProperties();
			} catch (Exception e) {
				plugin.getLogger().severe("Couldn't load object " + key + " from " + filename + "! " + e.toString());
			}
		}

		onPostLoad(keys);
	}

	public abstract void onPostLoad(Set<String> keys);

	@Override
	public void unload() {
		for (T object : idToObject.values()) {
			if (object.toCommit()) {
				save();
				return;
			}
		}
	}

	@Override
	public ConfigurationSection load(String identifier) {
		return idToConfig.get(identifier);
	}

	@Override
	public void save(String identifier) {
		save();
	}
	
	private void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			plugin.getLogger().severe("Error saving object to file!");
		}
	}
	
	@Override
	public void remove(String identifier) {
		idToConfig.remove(identifier);
		idToObject.remove(identifier);
	}

	@Override
	public void rename(String fromIdentifier, String toIdentifier) {
		ConfigurationSection config = idToConfig.remove(fromIdentifier);
		T object = idToObject.remove(fromIdentifier);
		
		idToConfig.put(toIdentifier, config);
		idToObject.put(toIdentifier, object);
	}

	@Override
	public final Object loadProperty(String identifier, String key) {
		ConfigurationSection config = idToConfig.get(identifier);
		return config.get(key);
	}

	@Override
	public final void saveProperty(String identifier, String key, Object value) {
		ConfigurationSection section = idToConfig.get(identifier);
		if (section == null) {
			section = config.createSection(identifier);
			idToConfig.put(identifier, section);
		}
		section.set(key, value);
	}
	
	public abstract T createObject(String identifier);

	protected void addObject(T object) {
		idToObject.put(object.getIdentifier(), object);
		save();
	}

	protected void removeObject(T object) {
		idToObject.remove(object.getIdentifier());
		idToConfig.remove(object.getIdentifier());

		config.set(object.getIdentifier(), null);
		save();
	}
	
	protected T getObject(String identifier) {
		return idToObject.get(identifier);
	}
}

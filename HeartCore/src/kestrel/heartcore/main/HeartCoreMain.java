package kestrel.heartcore.main;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import kestrel.heartcore.main.module.combattimer.CombatTimer;
import kestrel.heartcore.main.module.command.CommandSetMaxHealth;
import kestrel.heartcore.main.module.command.CommandSkull;
import kestrel.heartcore.main.module.death.DeathHandler;

public class HeartCoreMain extends JavaPlugin {

	private static HeartCoreMain instance;
	private static YamlConfiguration config;

	private static String headNamePrefix;

	@Override
	public void onEnable() {
		instance = HeartCoreMain.getPlugin(HeartCoreMain.class);
		config = saveConfigFile();

		if (config.getBoolean("death.enabled"))
			Bukkit.getPluginManager().registerEvents(new DeathHandler(), this);
		if (config.getInt("combatTimer.time") > 0)
			Bukkit.getPluginManager().registerEvents(new CombatTimer(), this);
		if (config.getBoolean("commands.skull.enabled")) {
			getCommand("skull").setExecutor(new CommandSkull());
			getCommand("skull").setTabCompleter(new CommandSkull());
		}
		if (config.getBoolean("commands.setMaxHealth.enabled")) {
			getCommand("setmaxhealth").setExecutor(new CommandSetMaxHealth());
			getCommand("setmaxhealth").setTabCompleter(new CommandSetMaxHealth());
		}
		headNamePrefix = replaceColorCodes(config.getString("headNamePrefix"));
	}

	private static YamlConfiguration saveConfigFile() {
		File f = new File(instance.getDataFolder() + "/config.yml");
		if (!f.exists())
			instance.saveResource("config.yml", false);
		return YamlConfiguration.loadConfiguration(f);
	}

	public static YamlConfiguration getConfiguration() {
		return config;
	}

	public static HeartCoreMain getInstance() {
		return instance;
	}

	public static String replaceColorCodes(String replace) {
		for (ChatColor color : ChatColor.values())
			replace = replace.replace(color.name(), color.toString());
		return replace;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getPlayerHead(String name) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		meta.setOwner(name);
		meta.setDisplayName(headNamePrefix + name);
		if (Bukkit.getPlayer(name) != null)
			meta.setDisplayName(headNamePrefix + Bukkit.getPlayer(name).getName());
		head.setItemMeta(meta);
		return head;
	}

}
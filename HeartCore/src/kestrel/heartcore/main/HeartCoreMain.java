package kestrel.heartcore.main;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class HeartCoreMain extends JavaPlugin implements Listener {

	private static final HeartCoreMain instance = HeartCoreMain.getPlugin(HeartCoreMain.class);
	private static YamlConfiguration config;
	private static short modifier;
	private static boolean reduceOnPlayerKills;
	private static boolean dropHeadOnDeath;
	private static boolean dropHeadOnPlayerKill;
	private static String msgHealthReduced;
	private static String msgBanned;
	private static String headNamePrefix;

	private static int combatTime;
	private static String msgCombatTimer;
	private static String msgCombatOver;
	private static Map<Player, Long> combatTimers;

	@Override
	public void onEnable() {
		config = saveConfigFile();
		Bukkit.getPluginManager().registerEvents(this, this);
		modifier = (short) config.getInt("modifier");
		reduceOnPlayerKills = config.getBoolean("reduceOnPlayerKills");
		dropHeadOnDeath = config.getBoolean("dropHeadOnDeath");
		dropHeadOnPlayerKill = config.getBoolean("dropHeadOnPlayerKill");
		msgHealthReduced = replaceColorCodes(config.getString("msgHealthReduced"));
		msgBanned = replaceColorCodes(config.getString("msgBanned"));
		headNamePrefix = replaceColorCodes(config.getString("headNamePrefix"));

		combatTime = config.getInt("combatTime");
		if (combatTime > 0) {
			combatTimers = new HashMap<>();
			msgCombatTimer = replaceColorCodes(config.getString("msgCombatTimer"));
			msgCombatOver = replaceColorCodes(config.getString("msgCombatOver"));
			BukkitRunnable combatTimeMessager = new BukkitRunnable() {

				@Override
				public void run() {
					for (Player p : combatTimers.keySet()) {
						if (combatTimers.containsKey(p))
							if (combatTimers.get(p) > System.currentTimeMillis()) {
								p.sendMessage(msgCombatTimer
										+ Math.floor((combatTimers.get(p) - System.currentTimeMillis()) / 1000));
							} else {
								p.sendMessage(msgCombatOver);
								combatTimers.remove(p);
							}
					}
				}
			};
			combatTimeMessager.runTaskTimer(this, 10, 10);
		}
	}

	private static YamlConfiguration saveConfigFile() {
		instance.saveResource(instance.getDataFolder() + "/config.yml", false);
		return YamlConfiguration.loadConfiguration(new File(instance.getDataFolder() + "/config.yml"));
	}

	private static String replaceColorCodes(String replace) {
		for (ChatColor color : ChatColor.values())
			replace.replace(color.name(), color.toString());
		return replace;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (!reduceOnPlayerKills && isPlayerKill(e))
			return;

		if (dropHeadOnDeath && (dropHeadOnPlayerKill || !isPlayerKill(e)))
			e.getDrops().add(getPlayerHead(e.getEntity()));

		if (e.getEntity().getMaxHealth() < modifier) {
			Bukkit.getBanList(BanList.Type.NAME).addBan(e.getEntity().getName(), msgBanned, null, "HeartCore");
			e.getEntity().kickPlayer(msgBanned);
			return;
		}

		e.getEntity().setMaxHealth(e.getEntity().getMaxHealth() - modifier);
		e.getEntity().sendMessage(msgHealthReduced + modifier);
	}

	private static boolean isPlayerKill(PlayerDeathEvent e) {
		String stringToCheck = e.getDeathMessage().substring(e.getEntity().getName().length());
		for (Player p : Bukkit.getOnlinePlayers())
			if (!p.getName().equals(e.getEntity().getName()))
				if (stringToCheck.contains(p.getName()))
					return true;
		return false;
	}

	private static ItemStack getPlayerHead(Player p) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		meta.setOwningPlayer(p);
		meta.setDisplayName(headNamePrefix + p.getName());
		head.setItemMeta(meta);
		return head;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player))
			return;
		if (combatTime <= 0)
			return;
		combatTimers.put((Player) e.getEntity(), System.currentTimeMillis() + (combatTime * 1000));
		((Player) e.getEntity()).sendMessage(msgCombatTimer + combatTime);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (combatTime > 0)
			if (combatTimers.get(e.getPlayer()) > System.currentTimeMillis())
				e.getPlayer().damage(100);
	}
}
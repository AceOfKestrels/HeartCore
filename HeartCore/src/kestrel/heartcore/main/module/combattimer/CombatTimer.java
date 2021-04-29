package kestrel.heartcore.main.module.combattimer;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import kestrel.heartcore.main.HeartCoreMain;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class CombatTimer implements Listener {

	private int combatTime;
	private String msgCombatTimer;
	private String msgCombatOver;
	private String msgCombatLog;
	private String msgCombatLogEntity;
	private Map<Player, Long> combatTimers;

	public CombatTimer() {
		combatTime = HeartCoreMain.getConfiguration().getInt("combatTimer.time");
		combatTimers = new HashMap<>();
		msgCombatTimer = HeartCoreMain
				.replaceColorCodes(HeartCoreMain.getConfiguration().getString("combatTimer.msgCombatTimer"));
		msgCombatOver = HeartCoreMain
				.replaceColorCodes(HeartCoreMain.getConfiguration().getString("combatTimer.msgCombatOver"));
		msgCombatLog = HeartCoreMain
				.replaceColorCodes(HeartCoreMain.getConfiguration().getString("combatTimer.msgCombatLog"));
		msgCombatLogEntity = HeartCoreMain
				.replaceColorCodes(HeartCoreMain.getConfiguration().getString("combatTimer.msgCombatLogEntity"));
		BukkitRunnable combatTimeMessager = new BukkitRunnable() {

			@Override
			public void run() {
				for (Player p : combatTimers.keySet()) {
					if (combatTimers.containsKey(p))
						if (combatTimers.get(p) > System.currentTimeMillis()) {
							sendActionbar(msgCombatTimer
									+ (short) (Math.floor((combatTimers.get(p) - System.currentTimeMillis()) / 1000)
											+ 1),
									p);
						} else {
							sendActionbar(msgCombatOver, p);
							combatTimers.remove(p);
						}
				}
			}
		};
		combatTimeMessager.runTaskTimer(HeartCoreMain.getInstance(), 10, 10);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player))
			return;
		combatTimers.put((Player) e.getEntity(), System.currentTimeMillis() + (combatTime * 1000));
		sendActionbar(msgCombatTimer + combatTime, (Player) e.getEntity());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (combatTimers.containsKey(e.getPlayer()) && combatTimers.get(e.getPlayer()) > System.currentTimeMillis())
			e.getPlayer().damage(100);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (e.getDeathMessage().contains(" died because of ")) {
			e.setDeathMessage(e.getEntity().getName() + " " + msgCombatLogEntity + " "
					+ e.getDeathMessage().substring(e.getEntity().getName().length() + 17));
		}
		if (e.getDeathMessage().contains(" died"))
			e.setDeathMessage(e.getEntity().getName() + " " + msgCombatLog);
	}

	private void sendActionbar(String message, Player player) {
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
	}

}

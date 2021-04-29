package kestrel.heartcore.main.module.death;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import kestrel.heartcore.main.HeartCoreMain;

public class DeathHandler implements Listener {

	private short healthModifier;
	private boolean reduceOnPlayerKills;
	private boolean dropHeadOnDeath;
	private boolean dropHeadOnPlayerKill;
	private String msgHealthReduced;
	private String msgBanned;

	public DeathHandler() {
		healthModifier = (short) HeartCoreMain.getConfiguration().getInt("death.healthModifier");
		reduceOnPlayerKills = HeartCoreMain.getConfiguration().getBoolean("death.reduceOnPlayerKills");
		dropHeadOnDeath = HeartCoreMain.getConfiguration().getBoolean("death.dropHeadOnDeath");
		dropHeadOnPlayerKill = HeartCoreMain.getConfiguration().getBoolean("death.dropHeadOnPlayerKill");
		msgHealthReduced = HeartCoreMain
				.replaceColorCodes(HeartCoreMain.getConfiguration().getString("death.msgHealthReduced"));
		msgBanned = HeartCoreMain.replaceColorCodes(HeartCoreMain.getConfiguration().getString("death.msgBanned"));

	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (dropHeadOnDeath && (dropHeadOnPlayerKill || !isPlayerKill(e)))
			e.getDrops().add(HeartCoreMain.getPlayerHead(e.getEntity().getName()));
		
		if (!reduceOnPlayerKills && isPlayerKill(e))
			return;

		if (e.getEntity().getMaxHealth() < healthModifier) {
			Bukkit.getBanList(BanList.Type.NAME).addBan(e.getEntity().getName(), msgBanned, null, "HeartCore");
			e.getEntity().kickPlayer(msgBanned);
			return;
		}

		e.getEntity().setMaxHealth(e.getEntity().getMaxHealth() - healthModifier);
		e.getEntity().sendMessage(msgHealthReduced + healthModifier);
	}

	private boolean isPlayerKill(PlayerDeathEvent e) {
		String stringToCheck = e.getDeathMessage().substring(e.getEntity().getName().length());
		for (Player p : Bukkit.getOnlinePlayers())
			if (!p.getName().equals(e.getEntity().getName()))
				if (stringToCheck.contains(p.getName()))
					return true;
		return false;
	}

}

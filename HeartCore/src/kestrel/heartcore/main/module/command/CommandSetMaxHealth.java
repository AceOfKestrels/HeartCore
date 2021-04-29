package kestrel.heartcore.main.module.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import kestrel.heartcore.main.HeartCoreMain;

public class CommandSetMaxHealth implements CommandExecutor, TabCompleter {

	private String msgSuccess;
	private String msgFailArgs;

	public CommandSetMaxHealth() {
		msgSuccess = HeartCoreMain
				.replaceColorCodes(HeartCoreMain.getConfiguration().getString("commands.setMaxHealth.msgSuccess"));
		msgFailArgs = HeartCoreMain
				.replaceColorCodes(HeartCoreMain.getConfiguration().getString("commands.setMaxHealth.msgFailArgs"));
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!command.getName().equalsIgnoreCase("setmaxhealth"))
			return true;
		if (args.length != 2) {
			sender.sendMessage(msgFailArgs);
			return false;
		}
		try {
			Player target = Bukkit.getPlayer(args[0]);
			int health = Integer.parseInt(args[1]);
			target.setMaxHealth(health);
			sender.sendMessage(msgSuccess + args[1]);
			return true;
		} catch (Exception e) {
			return true;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (!command.getName().equalsIgnoreCase("setmaxhealth") || args.length != 1)
			return new ArrayList<>();
		List<String> result = new ArrayList<>();
		for(Player p : Bukkit.getOnlinePlayers())
			if(p.getName().startsWith(args[0]))
				result.add(p.getName());
		return result;
	}

}

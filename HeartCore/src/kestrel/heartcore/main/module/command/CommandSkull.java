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

public class CommandSkull implements CommandExecutor, TabCompleter {

	private String msgSuccess;
	private String msgFailArgs;

	public CommandSkull() {
		msgSuccess = HeartCoreMain
				.replaceColorCodes(HeartCoreMain.getConfiguration().getString("commands.skull.msgSuccess"));
		msgFailArgs = HeartCoreMain
				.replaceColorCodes(HeartCoreMain.getConfiguration().getString("commands.skull.msgFailArgs"));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!command.getName().equalsIgnoreCase("skull"))
			return true;
		if (!(sender instanceof Player))
			return true;
		if (args.length != 1) {
			sender.sendMessage(msgFailArgs);
			return false;
		}
		((Player) sender).getInventory().addItem(HeartCoreMain.getPlayerHead(args[0]));
		sender.sendMessage(msgSuccess);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (!command.getName().equalsIgnoreCase("skull") || args.length != 1)
			return new ArrayList<>();
		List<String> result = new ArrayList<>();
		for (Player p : Bukkit.getOnlinePlayers())
			if (p.getName().startsWith(args[0]))
				result.add(p.getName());
		return result;
	}

}

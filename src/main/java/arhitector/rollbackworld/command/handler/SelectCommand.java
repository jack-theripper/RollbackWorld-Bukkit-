package arhitector.rollbackworld.command.handler;

import arhitector.rollbackworld.BukkitPlugin;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SelectCommand extends CommandHandler {
	
	/**
	 * Command handler.
	 *
	 * @param sender Source of the command
	 * @param args   Arguments.
	 */
	public SelectCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}
	
	@Override
	public void handle() {
		Player player = (Player) getSender();
		BukkitPlugin plugin = BukkitPlugin.getInstance();
		
		if (!plugin.selectionMode.containsKey(player.getName())) {
			player.getInventory().addItem(plugin.wandItemStack);
			plugin.selectionMode.put(player.getName(), new Location[2]);
			player.sendMessage("You have entered the selection mode!");
		} else {
			player.getInventory().removeItem(plugin.wandItemStack);
			plugin.selectionMode.remove(player.getName());
			player.sendMessage("You have left the selection mode!");
		}
	}
	
}

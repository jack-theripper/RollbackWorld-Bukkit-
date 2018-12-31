package arhitector.rollbackworld.command.handler;

import arhitector.rollbackworld.BukkitPlugin;
import arhitector.rollbackworld.region.CuboidRegion;
import arhitector.rollbackworld.region.Region;
import arhitector.rollbackworld.task.BackupTask;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateCommand extends CommandHandler {
	
	/**
	 * Command handler.
	 *
	 * @param sender Source of the command
	 * @param args   Arguments.
	 */
	public CreateCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}
	
	@Override
	public void handle() {
		Player player = (Player) this.getSender();
		String regionName = this.getArgs()[0];
		BukkitPlugin plugin = BukkitPlugin.getInstance();
		
		if (plugin.selectionMode.containsKey(player.getName()) && plugin.selectionMode.get(player.getName())[0] != null && plugin.selectionMode.get(player.getName())[1] != null) {
			Region region = new CuboidRegion(player.getWorld(), plugin.selectionMode.get(player.getName())[0].toVector(),
				plugin.selectionMode.get(player.getName())[1].toVector());
			player.sendMessage("Updating the region...");
			new BackupTask(region, regionName).start();
		} else {
			player.sendMessage("You haven't selected the 2 corners yet!");
		}
	}
	
}

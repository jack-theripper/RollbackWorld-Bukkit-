package arhitector.rollbackworld;

import arhitector.rollbackworld.command.CommandManager;
import arhitector.rollbackworld.event.BackupProcessEvent;
import arhitector.rollbackworld.region.Region;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class BukkitPlugin extends JavaPlugin implements CommandExecutor, Listener {
	
	@Getter
	private static BukkitPlugin instance;
	
	@Getter
	private static Map<String, Region> regions = new ConcurrentHashMap<>();
	
	RollbackManager rollbackManager;
	
	public HashMap<String, Location[]> selectionMode = new HashMap<>();
	
	public ItemStack wandItemStack;
	
	public BukkitPlugin() {
		instance = this;
	}
	
	@Override
	public void onEnable() {
		this.rollbackManager = new RollbackManager(this);
		
		getServer().getPluginCommand("rollbackworld").setExecutor(new CommandManager());
		
		this.wandItemStack = new ItemStack(Material.WOOD_AXE);
		
		ItemMeta itemMeta = this.wandItemStack.getItemMeta();
		itemMeta.setDisplayName("Rollback selector");
		
		itemMeta.setLore(Arrays.asList(
			ChatColor.GREEN + "Left click to select the first corner",
			ChatColor.GREEN + "Right click to select the second corner"
		));
		
		this.wandItemStack.setItemMeta(itemMeta);
		
		File dir = this.getDataFolder();
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".dat");
			}
		});
		
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable() {
		
		for (String playerName : this.selectionMode.keySet()) {
			Player player = this.getServer().getPlayer(playerName);
			
			if (player != null) {
				player.getInventory().remove(this.wandItemStack);
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		
		if (this.selectionMode.containsKey(player.getName()) && this.compareItem(player.getItemInHand(), this.wandItemStack)) {
			event.setCancelled(true);
			
			int corner = event.getAction().equals(Action.LEFT_CLICK_BLOCK) ? 0 : (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) ? 1 : 2);
			
			if (corner != 2) {
				Location location = event.getClickedBlock().getLocation();
				this.selectionMode.get(player.getName())[corner] = location;
				player.sendMessage("You have set the #" + (corner + 1) + " corner at " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
			}
		}
	}
	
	@EventHandler
	public void onProcessing(BackupProcessEvent event) {
		Bukkit.broadcast("Processing ... " + event.getPercentage() + "%", "rollback.basic");
	}
	
	private boolean compareItem(ItemStack var1, ItemStack var2) {
		return var1 != null && var2 != null && var1.getType().equals(var2.getType()) && var1.getItemMeta().equals(var2.getItemMeta());
	}
 
}

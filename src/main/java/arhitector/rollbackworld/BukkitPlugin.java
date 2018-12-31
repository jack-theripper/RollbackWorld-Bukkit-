package arhitector.rollbackworld;

import arhitector.rollbackworld.command.CommandManager;
import arhitector.rollbackworld.region.Region;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
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
	
	HashMap<String, Location[]> selectionMode = new HashMap<>();
	
	ItemStack wandItemStack;
	
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
	}
	
	@Override
	public void onDisable() {
	
	}
	
}

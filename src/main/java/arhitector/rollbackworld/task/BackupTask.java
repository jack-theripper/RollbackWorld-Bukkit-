package arhitector.rollbackworld.task;

import arhitector.rollbackworld.BukkitPlugin;
import arhitector.rollbackworld.event.BackupProcessEvent;
import arhitector.rollbackworld.region.Region;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The task to save the region.
 */
public class BackupTask extends BukkitRunnable {
	
	@Getter
	private final List<String> blocks = new ArrayList<>();
	
	@Getter
	private final Region region;
	private final String regionName;
	
	private int scannedBlocks = 0;
	private int temp_counter = 0;
	private int runs = 0;
	private int scanSpeed;
	private double totalBlocks;
	private Iterator<Vector> iterator;
	
	{
		this.scanSpeed = 50000;
	}
	
	public BackupTask(@NonNull Region theRegion, String regionName) {
		this.region = theRegion;
		this.regionName = regionName;
		
		this.totalBlocks = (double) region.getSize();
		iterator = region.iterator();
	}
	
	public void start() {
		this.runTaskTimer(BukkitPlugin.getInstance(), 0L, 1L);
	}
	
	@Override
	public synchronized void run() {
		
		for (; iterator.hasNext() && this.temp_counter < this.scanSpeed; ++this.temp_counter) {
			Block block = this.getBlock(iterator.next());
			
			if (block != null) {
				blocks.add(block.getX() + ":" + block.getY() + ":" + block.getZ() + ":" + block.getTypeId() + (block.getData() != 0 ? ":" + block.getData() : ""));
			}
		}
		
		this.scannedBlocks += this.temp_counter;
		this.temp_counter = 0;
		++this.runs;
		
		if (this.runs == 20) {
			BukkitPlugin.getInstance().getServer().getPluginManager().callEvent(new BackupProcessEvent(this.getRegion(), (int) ((double) this.scannedBlocks / this.totalBlocks * 100.0D)));
			this.runs = 0;
		}
		
		if (!iterator.hasNext()) {
			this.cancel();
			
			BukkitPlugin.getInstance().getServer().getPluginManager().callEvent(new BackupProcessEvent(this.getRegion(), 100));
			File var7x = new File(BukkitPlugin.getInstance().getDataFolder(), regionName + ".dat");
			YamlConfiguration var2x = YamlConfiguration.loadConfiguration(var7x);
			
			File file = new File(BukkitPlugin.getInstance().getDataFolder() + "/region", regionName + ".dat");
			file.getParentFile().mkdirs();
			
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			BufferedWriter writer = null;
			
			try {
				writer = new BufferedWriter(new FileWriter(file));
				writer.write(String.join(",", blocks));
			} catch (IOException ignored) {
				ignored.printStackTrace();
			} finally {
				try {
					if (writer != null) {
						writer.close();
					}
				} catch (IOException ignored) {
					ignored.printStackTrace();
				}
			}
			
			try {
				var2x.set("region", regionToString());
				var2x.save(var7x);
			} catch (IOException var6x) {
				var6x.printStackTrace();
			}
			
			BukkitPlugin.getRegions().put(regionName, region);
		}
		
	}
	
	protected Block getBlock(@NonNull Vector vector) {
		Block block = this.getRegion().getWorld().getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
		
		if (block.getType() != Material.AIR) {
			return block;
		}
		
		return null;
	}
	
	private String regionToString() {
		return this.getRegion().getWorld().getName() + "," + getRegion().getMinimumPoint().toString() + "," + getRegion().getMaximumPoint().toString();
	}
	
}

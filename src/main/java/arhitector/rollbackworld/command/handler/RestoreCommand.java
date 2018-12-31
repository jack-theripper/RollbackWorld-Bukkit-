package arhitector.rollbackworld.command.handler;

import arhitector.rollbackworld.BukkitPlugin;
import arhitector.rollbackworld.region.Region;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class RestoreCommand extends CommandHandler {
	
	/**
	 * Command handler.
	 *
	 * @param sender Source of the command
	 * @param args   Arguments.
	 */
	public RestoreCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}
	
	@Override
	public void handle() {
		String regionName = this.getArgs()[1];
		
		if (!BukkitPlugin.getRegions().containsKey(regionName)) {
			this.getSender().sendMessage("No information available.");
		} else {
			Region region = BukkitPlugin.getRegions().get(regionName);
			Iterator<Vector> iterator = region.iterator();
			
			Map<VectorValue, BlockValue> blocks = new HashMap<>();
			File file = new File(BukkitPlugin.getInstance().getDataFolder() + "/region", regionName + ".dat");
			BufferedReader in = null;
			
			try {
				in = new BufferedReader(new FileReader(file));
				String read = null;
				
				while ((read = in.readLine()) != null) {
					for (String part : read.split(",")) {
						String[] sub = part.split(":");
						VectorValue vector = new VectorValue(Integer.valueOf(sub[0]), Integer.valueOf(sub[1]), Integer.valueOf(sub[2]));
						blocks.put(vector, new BlockValue(Integer.valueOf(sub[3]), sub.length > 4 ? Byte.valueOf(sub[4]) : 0));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					assert in != null;
					in.close();
				} catch (Exception ignored) {
				}
			}
			
			(new BukkitRunnable() {
				int checked = 0;
				int current = 0;
				int ticks = 0;
				
				public void run() {
					for(; iterator.hasNext() && this.current < 10000; ++this.current) {
						Vector vector = iterator.next();
						Block block = getBlock(region, vector);
						
						if (blocks.containsKey(new VectorValue(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()))) {
							BlockValue value = blocks.get(new VectorValue(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()));
							
							if (block != null && (value.type != block.getTypeId() || value.data != block.getData())) {
								block.setTypeIdAndData(value.type, value.data, false);
							}
						} else if (block != null && block.getType() != Material.AIR) {
							block.setType(Material.AIR);
						}
					}
					
					this.checked += this.current;
					this.current = 0;
					++this.ticks;
					if (this.ticks == 20) {
						getSender().sendMessage(((int)((double)this.checked / region.getSize() * 100.0D) + "%"));
						this.ticks = 0;
					}
					
					if (!iterator.hasNext()) {
						this.cancel();
						getSender().sendMessage("100 %");
					}
					
				}
			}).runTaskTimer(BukkitPlugin.getInstance(), 10L, 1L);
			
		}
	}
	
	protected Block getBlock(@NonNull Region region, @NonNull Vector vector) {
		return region.getWorld().getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
	}
	
	class BlockValue {
		
		public final int type;
		public final Byte data;
		
		BlockValue(int type, Byte data) {
			
			this.type = type;
			this.data = data;
		}
	}

	class VectorValue extends Vector {
		
		public VectorValue(Integer valueOf, Integer valueOf1, Integer valueOf2) {
			super(valueOf, valueOf1, valueOf2);
		}
		
		@Override
		public boolean equals(Object obj) {
			Vector value = (Vector) obj;
			return this.getBlockX() == value.getBlockX() && this.getBlockY() == value.getBlockY() && this.getBlockZ() == value.getBlockZ();
		}
		
		@Override
		public int hashCode() {
			return (String.valueOf(this.x) + this.y + this.z).hashCode();
		}
		
	}
	
}

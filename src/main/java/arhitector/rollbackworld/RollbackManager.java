package arhitector.rollbackworld;

import arhitector.rollbackworld.constant.RegionState;
import arhitector.rollbackworld.region.Region;
import lombok.NonNull;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The rollback manager.
 */
public class RollbackManager {
	
	private List<Region> queue = new CopyOnWriteArrayList<>();
	private List<Region> rollbacking = new CopyOnWriteArrayList<>();
	private BukkitTask task;
	private final Plugin plugin;
	
	/**
	 * Constructor.
	 */
	public RollbackManager(@NonNull Plugin plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Add a region to the rollback queue.
	 *
	 * @param region Represents the physical shape of the area.
	 */
	public void add(@NonNull Region region) {
		if (this.queue.contains(region) || this.rollbacking.contains(region)) {
			return;
		}
		
		if (this.rollbacking.size() >= 1) { // todo rollback_queue_size
			region.setState(RegionState.QUEUED); // todo call event rollback process
			this.queue.add(region);
		} else {
			this.rollback(region);
			this.rollbacking.add(region);
			
			if (this.task != null) {
				return;
			}
			
			this.task = new BukkitRunnable() {
				@Override
				public void run() {
					for (Region region : rollbacking) {
						if (region.getState() == RegionState.ROLLBACKING) {
							continue;
						}
						
						rollbacking.remove(region);
						
						if (rollbacking.isEmpty() && queue.isEmpty()) {
							this.cancel();
							task = null;
							
							return;
						}
						
						if (!queue.isEmpty()) {
							rollbacking.add((Region) queue.get(0));
							rollback(queue.get(0));
							queue.remove(0);
						}
					}
				}
			}.runTaskTimer(this.plugin, 40L, 40L);
		}
	}
	
	/**
	 * Roll back the resulting area.
	 *
	 * @param region Represents the physical shape of the area.
	 */
	public void rollback(@NonNull Region region) {
	
	}
	
	/**
	 * Clear the queue.
	 */
	public void clear() {
		this.queue.clear();
		this.rollbacking.clear();
		
		if (this.task != null) {
			this.task.cancel();
			this.task = null;
		}
	}
	
}

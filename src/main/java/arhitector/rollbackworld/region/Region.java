package arhitector.rollbackworld.region;

import arhitector.rollbackworld.constant.RegionState;
import lombok.NonNull;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * Represents a physical shape.
 */
public interface Region extends Iterable<Vector>, Cloneable {
	
	/**
	 * What is the status of the region.
	 */
	RegionState getState();
	
	/**
	 * Change the state.
	 */
	void setState(@NonNull RegionState state);
	
	/**
	 * Sets the world that the selection is in.
	 *
	 * @return the world, or null
	 */
	@NonNull
	World getWorld();
	
	/**
	 * Get the lower point of a region.
	 *
	 * @return min. point
	 */
	Vector getMinimumPoint();
	
	/**
	 * Get the upper point of a region.
	 *
	 * @return max. point
	 */
	Vector getMaximumPoint();
	
	/**
	 * Get the number of blocks in the region.
	 *
	 * @return number of blocks
	 */
	int getSize();
	
	/**
	 * Returns true based on whether the region contains the point.
	 *
	 * @param position the position
	 * @return true if contained
	 */
	boolean contains(Vector position);
	
	/**
	 * Make a clone of the region.
	 *
	 * @return a cloned version
	 */
	Region clone();
	
}

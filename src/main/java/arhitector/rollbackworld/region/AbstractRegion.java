package arhitector.rollbackworld.region;

import arhitector.rollbackworld.constant.RegionState;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Iterator;

/**
 * Abstract implementation of the region.
 */
public abstract class AbstractRegion implements Region {
	
	/**
	 * The Bukkit-world.
	 */
	protected final World world;
	
	/**
	 * The region state.
	 */
	private RegionState state;
	
	/**
	 * Basic constructor.
	 *
	 * @param world Bukkit-world, where the region is located.
	 */
	public AbstractRegion(World world) {
		this.world = world;
	}
	
	@Override
	public RegionState getState() {
		return this.state;
	}
	
	@Override
	public void setState(RegionState state) {
		this.state = state;
	}
	
	@Override
	public World getWorld() {
		return this.world;
	}
	
	@Override
	public int getSize() {
		Vector min = getMinimumPoint();
		Vector max = getMaximumPoint();
		
		return (int) ((max.getX() - min.getX() + 1) * (max.getY() - min.getY() + 1) * (max.getZ() - min.getZ() + 1));
	}
	
	@Override
	public AbstractRegion clone() {
		try {
			return (AbstractRegion) super.clone();
		} catch (CloneNotSupportedException exc) {
			return null;
		}
	}
	
}

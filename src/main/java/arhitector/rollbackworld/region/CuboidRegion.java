package arhitector.rollbackworld.region;

import lombok.NonNull;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Iterator;

/**
 * The physical region in the world in the form of a cube.
 */
public class CuboidRegion extends AbstractRegion {
	
	private Vector pos1;
	private Vector pos2;
	
	/**
	 * Construct a new instance of this cuboid using two corners of the cuboid.
	 *
	 * @param world the world
	 * @param pos1  the first position
	 * @param pos2  the second position
	 */
	public CuboidRegion(World world, @NonNull Vector pos1, @NonNull Vector pos2) {
		super(world);
		
		this.pos1 = pos1;
		this.pos2 = pos2;
		
		// Clamps the cuboid according to boundaries of the world.
		this.pos1.setY(Math.max(0, Math.min(this.getMaxY(), this.pos1.getY())));
		this.pos2.setY(Math.max(0, Math.min(this.getMaxY(), this.pos2.getY())));
	}
	
	@Override
	public Vector getMinimumPoint() {
		return new Vector(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
	}
	
	@Override
	public Vector getMaximumPoint() {
		return new Vector(Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
	}
	
	@Override
	public boolean contains(Vector position) {
		int x = position.getBlockX();
		int y = position.getBlockY();
		int z = position.getBlockZ();
		
		Vector min = getMinimumPoint();
		Vector max = getMaximumPoint();
		
		return x >= min.getBlockX() && x <= max.getBlockX() && y >= min.getBlockY() && y <= max.getBlockY() && z >= min.getBlockZ() && z <= max.getBlockZ();
	}
	
	@Override
	public Iterator<Vector> iterator() {
		return new Iterator<Vector>() {
			private Vector min = getMinimumPoint();
			private Vector max = getMaximumPoint();
			private int nextX = min.getBlockX();
			private int nextY = min.getBlockY();
			private int nextZ = min.getBlockZ();
			
			@Override
			public boolean hasNext() {
				return (nextX != Integer.MIN_VALUE);
			}
			
			@Override
			public Vector next() {
				if (!hasNext()) {
					throw new java.util.NoSuchElementException();
				}
				
				Vector answer = new Vector(nextX, nextY, nextZ);
				
				if (++nextX > max.getBlockX()) {
					nextX = min.getBlockX();
					
					if (++nextY > max.getBlockY()) {
						nextY = min.getBlockY();
						
						if (++nextZ > max.getBlockZ()) {
							nextX = Integer.MIN_VALUE;
						}
					}
				}
				
				return answer;
			}
			
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	@Override
	public CuboidRegion clone() {
		return (CuboidRegion) super.clone();
	}
	
	/**
	 * The maximum height in the world.
	 */
	private int getMaxY() {
		return getWorld().getMaxHeight() - 1;
	}
	
}

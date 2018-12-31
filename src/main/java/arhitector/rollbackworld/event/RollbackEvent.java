package arhitector.rollbackworld.event;

import arhitector.rollbackworld.region.Region;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RollbackEvent extends Event {
	
	private static final HandlerList HANDLERS = new HandlerList();
	
	@Getter
	private final Region region;
	
	public RollbackEvent(@NonNull Region region) {
		this.region = region;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	
}

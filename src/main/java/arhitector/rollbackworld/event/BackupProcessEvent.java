package arhitector.rollbackworld.event;

import arhitector.rollbackworld.region.Region;
import lombok.Getter;

public class BackupProcessEvent extends RollbackEvent {
	
	@Getter
	private int percentage;
	
	public BackupProcessEvent(Region region, int percentage) {
		super(region);
		
		this.percentage = percentage;
	}
	
}

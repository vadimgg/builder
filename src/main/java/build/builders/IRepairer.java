package build.builders;

import build.IWorldStatusUpdater;
import data.AResident;
import data.Plot;

public interface IRepairer<T extends AResident> extends IWorldStatusUpdater {

	
	public boolean isReaperNeeded(Plot<T> plot);
	
	public boolean repair(Plot<T> plot);
	
	
}

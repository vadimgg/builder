package build.builders;

import build.IWorldStatusUpdater;
import data.AResident;
import data.Plot;

public interface ISpawnUpdater<T extends AResident> extends IWorldStatusUpdater {

	public boolean needSpawnUpdate(Plot<T> plot);
	public boolean updateSpawn(Plot<T> plot);
	
}

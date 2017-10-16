package build;

import build.builders.*;
import data.AResident;
import data.WorldConfig;
import data.AWorldPlotsRepository;

public abstract class BuilderHooks<T extends AResident>
{
	// Prisons build hooks
	// Generate builder per world
	// create and initialize repository (create rest clients)
	
	protected WorldConfig m_config;
	
	public BuilderHooks(WorldConfig config)
	{
		m_config = config;
	}
	
	public WorldConfig getConfig() { return m_config; }
	
	// All clients should be initialized before those get executed
	// Configs must be reloaded
	
	public abstract AWorldPlotsRepository<T> getPlotsRepository();
	public abstract IBuildLogic<T> getBuilder();
	public abstract IBuildLogic<T> getUpgrader(); 
	public abstract IBuildLogic<T> getReseter();
	public abstract ISpawnUpdater<T> getSpawnUpdater();
	public abstract IRepairer<T> getRepairer();
	public abstract IPlotAssigner<T> getPlotAssigner();
	
}

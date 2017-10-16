package build.builders;

import build.BuilderHooks;
import data.AResident;
import data.BuildStatus;
import data.Plot;
import data.PlotSchematic;

public class Builder<T extends AResident> extends AUpgrader<T> {

	private int m_buildQueue = 0;
	
	public Builder(BuilderHooks<T> builderHooks) {
		super(builderHooks);
	}

	public void clearBuildQueue()
	{
		m_buildQueue = 0;
	}
	
	public boolean build(int amount)
	{
		m_buildQueue += amount;
		execute();
		
		return true;
	}
	
	@Override
	synchronized protected Plot<T> getNext()
	{
		Plot<T> next = super.getNext();
		if (next != null)
			return next;
		
		if(m_buildQueue <= 0)
			return null;
		
		Plot<T> newPlot = m_buildRepo.create(getWorldName(), new PlotSchematic( m_config.getBaseSchematic() ) );
		m_buildQueue--;
		
		return newPlot;
	}

	@Override
	protected int getMaxConcurrent() {
		return m_config.maxConcurrentBuilds();
	}

	@Override
	protected boolean isIncomplete(Plot<T> plot) {
		return plot.getStatus().getBuildStatus() == BuildStatus.BUILDING;
	}

	@Override
	protected IBuildLogic<T> getBuildLogic(BuilderHooks<T> builderHooks) {
		return builderHooks.getBuilder();
	}

}

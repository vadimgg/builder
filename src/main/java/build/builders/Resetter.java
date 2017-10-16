package build.builders;

import build.BuilderHooks;
import data.AResident;
import data.BuildStatus;
import data.Plot;
import data.PlotSchematic;
import data.PlotStatus;

public class Resetter<T extends AResident> extends AUpgrader<T> {

	public Resetter(BuilderHooks<T> builderHooks) {
		super(builderHooks);
	}

	public boolean reset(Plot<T> plot)
	{
		PlotSchematic toPaste = new PlotSchematic( m_config.getBaseSchematic() ); 
		m_buildRepo.updateStatus(plot, new PlotStatus(plot.getStatus().getCur(), toPaste, BuildStatus.RESETTING),  (isSuccess) -> {
			queue(plot);
		});
		
		return true;
	}
	
	@Override
	protected int getMaxConcurrent() {
		return m_config.maxConcurrentUpgrades();
	}

	@Override
	protected boolean isIncomplete(Plot<T> plot) {
		return plot.getStatus().getBuildStatus() == BuildStatus.RESETTING;
	}

	@Override
	protected IBuildLogic<T> getBuildLogic(BuilderHooks<T> builderHooks) {
		return builderHooks.getReseter();
	}

}

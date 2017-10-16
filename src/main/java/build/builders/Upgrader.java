package build.builders;

import build.BuilderHooks;
import data.AResident;
import data.BuildStatus;
import data.Plot;
import data.PlotSchematic;
import data.PlotStatus;

public class Upgrader<T extends AResident> extends AUpgrader<T> {

	public Upgrader(BuilderHooks<T> builderBooks) {
		super(builderBooks);
	}

	public boolean upgrade(Plot<T> plot, PlotSchematic upgrade)
	{
		m_buildRepo.updateStatus(plot, new PlotStatus(plot.getStatus().getCur(), upgrade, BuildStatus.UPGRADING),  (isSuccess) -> {
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
		return plot.getStatus().getBuildStatus() == BuildStatus.UPGRADING;
	}

	@Override
	protected IBuildLogic<T> getBuildLogic(BuilderHooks<T> builderHooks) {
		return builderHooks.getUpgrader();
	}

}

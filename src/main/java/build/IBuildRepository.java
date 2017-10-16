package build;

import java.util.function.Consumer;

import data.AResident;
import data.Plot;
import data.PlotSchematic;
import data.PlotStatus;

public interface IBuildRepository<T extends AResident> {

	public Plot<T> create(String worldName, PlotSchematic schematic);
	public void updateStatus(Plot<T> plot, PlotStatus newStatus, Consumer<Boolean> callback);
}

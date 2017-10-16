package build.builders;

import java.util.function.Consumer;

import data.AResident;
import data.Plot;

public interface IBuildLogic<T extends AResident> {

	// Build Params calculator
	// Schematic repository
	
	public boolean canBuild();
	
	public void execute(Plot<T> plot, Consumer<Plot<T>> onComplete);
	
}

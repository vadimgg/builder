package build;

import java.util.UUID;

import data.AResident;
import data.MemberRank;
import data.Plot;

public interface IPlotAssigner<T extends AResident> {

	public boolean canAssign(Plot<T> plot, UUID pid, MemberRank rank);
	public boolean canUnassign(Plot<T> plot, UUID pid);
	
	public boolean assign(Plot<T> plot, UUID pid, MemberRank rank);
	public boolean unassign(Plot<T> plot, UUID pid);
	
}

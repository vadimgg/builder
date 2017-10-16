package build;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import data.AResident;
import data.MemberRank;
import data.Plot;

public interface IPlotsClient<T extends AResident> {

	public List<Plot<T>> syncGetAll(String serverName, String worldName);
	
	public void upsert(Plot<T> plot, Consumer<Boolean> onComplete);
	public void assign(UUID pid, Plot<T> plot, MemberRank rank, Consumer<Boolean> onComplete);
	public void unassign(UUID pid, Plot<T> plot, Consumer<Boolean> onComplete);
	public void unassignAll(Plot<T> plot, Consumer<Boolean> onComplete);
	
}

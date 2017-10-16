package data;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.common.collect.Maps;

import build.IBuildRepository;
import build.IPlotsClient;
import build.IWorldStatusUpdater;


public abstract class AWorldPlotsRepository<T extends AResident> implements IBuildRepository<T>, IWorldStatusUpdater {
	
	private IPlotsClient<T> m_client;
	
	protected Map<Integer, Plot<T>> m_allPlots = Maps.newHashMap();
	protected Map<BuildStatus, Map<Integer, Plot<T>>> m_byBuildStatus = Maps.newHashMap();
	
	public AWorldPlotsRepository(IPlotsClient<T> client)
	{
		m_client = client;
	}
	
	protected abstract T newResident(UUID pid);
	
	private String getServerName() { return "test-server"; }
	
	
	public Plot<T> getLocal(int plotId)
	{
		// TODO severe message here, local plots should always be found
		return m_allPlots.get(plotId);
	}
	
	public Collection<Plot<T>> getAllLocal()
	{
		return m_allPlots.values();
	}
	
	public List<Plot<T>> loadAll(String worldName)
	{
		List<Plot<T>> plots = m_client.syncGetAll(getServerName(), worldName);
		for(Plot<T> plot: plots)
			insertNewPlot(plot);
		
		return plots;
	}
	
	public Plot<T> create(String worldName, PlotSchematic schematic)
	{
		Plot<T> plot = Plot.newPlot(getServerName(), worldName, schematic);
		insertNewPlot(plot);
		return plot;
	}
	
	public void updateStatus(Plot<T> plot, PlotStatus newStatus, Consumer<Boolean> callback)
	{
		if(!plot.getStatus().equals(newStatus))
		{
			callback.accept(true);
			return;
		}
		
		Map<Integer, Plot<T>> byId = m_byBuildStatus.get(plot.getStatus().getBuildStatus());
		if (byId != null)
			byId.remove(plot.getPlotId());
			
		plot.setStatus(newStatus);
		updateBuildStatusMap(plot);
		
		upsert(plot, callback);
	}
	
	public void assign(UUID pid, Plot<T> plot, MemberRank rank, Consumer<Boolean> callback)
	{
		if ( plot.getResident() == null )
			plot.setResident( newResident(pid) );
		
		plot.getResident().upsertMember(pid, rank);
		
		m_client.assign(pid, plot, rank, callback);
	}
	
	public void unassign(UUID pid, Plot<T> plot, Consumer<Boolean> callback)
	{
		MemberRank rank = plot.getResident().removeMember(pid);
		if(rank == MemberRank.OWNER)
		{
			plot.setResident(null);
			m_client.unassignAll(plot, callback);
		}
		else
		{
			m_client.unassign(pid, plot, callback);
		}
	}

	private void insertNewPlot(Plot<T> plot)
	{
		m_allPlots.put(plot.getPlotId(), plot);
		updateBuildStatusMap(plot);
		// TODO update other maps
	}
	
	private void upsert(Plot<T> plot, Consumer<Boolean> callback)
	{
		m_client.upsert(plot, callback);
	}
	
	private void updateBuildStatusMap(Plot<T> plot)
	{
		Map<Integer, Plot<T>> byId = m_byBuildStatus.get(plot.getStatus().getBuildStatus());
		if (byId == null)
		{
			byId = Maps.newHashMap();
			m_byBuildStatus.put(plot.getStatus().getBuildStatus(), byId);
		}
		
		byId.put(plot.getPlotId(), plot);
	}
	
	@Override
	public void updateStatus(WorldStatusReport status) {
		String name = getClass().getSimpleName().toLowerCase();
		
		for(Map.Entry<BuildStatus, Map<Integer, Plot<T>>> entry: m_byBuildStatus.entrySet())
			status.addPlaceholder(String.format("{%s_%s}", name, entry.getKey().name().toLowerCase()), String.valueOf(entry.getValue().size()));
	}
	
}

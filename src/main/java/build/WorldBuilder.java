package build;

import java.util.Collection;
import java.util.UUID;

import com.google.common.collect.Lists;

import build.builders.*;
import data.AResident;
import data.Plot;
import data.PlotSchematic;
import data.WorldConfig;
import data.AWorldPlotsRepository;
import data.MemberRank;
import data.WorldStatusReport;

public class WorldBuilder<T extends AResident> {
	
	private WorldConfig m_config;
	
	private Builder<T> m_builder;
	private Upgrader<T> m_upgrader; 
	private Resetter<T> m_resetter;
	
	private IRepairer<T> m_repairer;
	private ISpawnUpdater<T> m_spawnUpdater;
	private IPlotAssigner<T> m_plotAssigner;
	
	private AWorldPlotsRepository<T> m_plotsRepository;
	
	public WorldBuilder(BuilderHooks<T> builderHooks)
	{
		m_builder = new Builder<T>(builderHooks);
		m_upgrader = new Upgrader<T>(builderHooks);
		m_resetter = new Resetter<T>(builderHooks);
		
		m_spawnUpdater = builderHooks.getSpawnUpdater();
		m_repairer = builderHooks.getRepairer();
		m_plotAssigner = builderHooks.getPlotAssigner();
		
		m_config = builderHooks.getConfig();
		m_plotsRepository = builderHooks.getPlotsRepository();
		
		initialize();
	}
	
	public void updateStatus(WorldStatusReport status)
	{
		m_builder.updateStatus(status);
		m_upgrader.updateStatus(status);
		m_resetter.updateStatus(status);
		m_repairer.updateStatus(status);
		m_spawnUpdater.updateStatus(status);
		m_plotsRepository.updateStatus(status);
	}
	
	private void initialize()
	{
		Collection<Plot<T>> plots =  m_plotsRepository.loadAll(m_config.getWorldName());
		// TODO sort by build status and optimize by removing remainder 
		Collection<Plot<T>> remainder = Lists.newLinkedList();
		
		for(Plot<T> plot: plots)
		{
			m_builder.load(plot);
			m_upgrader.load(plot);
			m_resetter.load(plot);
			
			m_spawnUpdater.updateSpawn(plot);
			
			if (plot.hasResident())
			{
				if (m_repairer.isReaperNeeded(plot))
					m_repairer.repair(plot);
			}
			else
				remainder.add(plot);
		}
		
		for(Plot<T> plot: remainder)
		{
			if (m_repairer.isReaperNeeded(plot))
				m_repairer.repair(plot);
		}
		
	}
	
	public boolean assign(int plotId, UUID pid, MemberRank rank)
	{
		Plot<T> plot = m_plotsRepository.getLocal(plotId);
		if (plot == null)
			return false;
		
		if (!m_plotAssigner.canAssign(plot, pid, rank))
			return false;
		
		if ( m_plotAssigner.assign(plot, pid, rank) )
		{
			m_plotsRepository.assign(pid, plot, rank, (result) -> {} );
			return true;
		}
		
		return false;
	}
	
	public boolean unassign(int plotId, UUID pid, MemberRank rank)
	{
		Plot<T> plot = m_plotsRepository.getLocal(plotId);
		if (plot == null)
			return false;
		
		if (!m_plotAssigner.canUnassign(plot, pid))
			return false;
		
		if ( m_plotAssigner.unassign(plot, pid) )
		{
			m_plotsRepository.unassign(pid, plot, (result) -> {} );
			return true;
		}
		
		return false;
	}
	
	public boolean build(int amount)
	{
		if (!m_builder.canExecute())
			return false;
		
		return m_builder.build(amount);
	}
	
	public boolean upgrade(int plotId, PlotSchematic schematic)
	{
		if (!m_upgrader.canExecute())
			return false;
		
		Plot<T> plot = m_plotsRepository.getLocal(plotId);
		if (plot == null)
			return false;
		
		return m_upgrader.upgrade(plot, schematic);
	}
	
	public boolean reset(int plotId)
	{
		if (!m_resetter.canExecute())
			return false;
		
		Plot<T> plot = m_plotsRepository.getLocal(plotId);
		if (plot == null)
			return false;
		
		return m_resetter.reset(plot);
	}
	
	public boolean updateSpawns()
	{
		boolean success = true;
		for(Plot<T> plot: m_plotsRepository.getAllLocal())
		{
			if(m_spawnUpdater.needSpawnUpdate(plot))
				success = success && m_spawnUpdater.updateSpawn(plot);
		}
		
		return success;
	}
	
	public boolean repairRegions()
	{
		for(Plot<T> plot: m_plotsRepository.getAllLocal())
		{
			if ( m_repairer.isReaperNeeded(plot) )
				m_repairer.repair(plot);
		}
		
		return false;
	}

}

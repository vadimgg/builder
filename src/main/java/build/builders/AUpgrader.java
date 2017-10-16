package build.builders;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import build.BuilderHooks;
import build.IBuildRepository;
import build.IWorldStatusUpdater;
import data.AResident;
import data.BuildStatus;
import data.Plot;
import data.PlotStatus;
import data.WorldConfig;
import data.WorldStatusReport;

public abstract class AUpgrader<T extends AResident> implements IWorldStatusUpdater {

	private Map<Integer, Plot<T>> m_inProgress = Maps.newConcurrentMap();
	private Queue<Plot<T>> m_inQueue = new ConcurrentLinkedQueue<Plot<T>>( Lists.newLinkedList() );
	
	protected WorldConfig m_config;
	protected IBuildRepository<T> m_buildRepo;
	protected IBuildLogic<T> m_buildLogic;
	
	public AUpgrader(BuilderHooks<T> builderHooks)
	{
		m_config = builderHooks.getConfig();
		m_buildRepo = builderHooks.getPlotsRepository();
		m_buildLogic = getBuildLogic(builderHooks);
	}
	
	@Override
	public void updateStatus(WorldStatusReport status) {
		status.addPlaceholder(String.format("{%s_queue}", getClass().getSimpleName().toLowerCase()), String.valueOf(m_inQueue.size()));
		status.addPlaceholder(String.format("{%s_progress}", getClass().getSimpleName().toLowerCase()), String.valueOf(m_inProgress.size()));
	}
	
	protected abstract IBuildLogic<T> getBuildLogic(BuilderHooks<T> builderHooks);
	protected abstract int getMaxConcurrent();
	protected abstract boolean isIncomplete(Plot<T> plot);
	
	public boolean canExecute() { return m_buildLogic.canBuild(); }
	
	public void load(Plot<T> plot)
	{
		if (isIncomplete(plot))
			queue(plot);
	}
	
	protected PlotStatus completeStatus(Plot<T> plot)
	{
		if (plot.hasResident())
			return new PlotStatus(plot.getStatus().getPrev(), plot.getStatus().getCur(), BuildStatus.OCCUPIED);
		else
			return new PlotStatus(plot.getStatus().getPrev(), plot.getStatus().getCur(), BuildStatus.AVALIABLE);
	}
	
	protected String getWorldName()
	{
		return m_config.getWorldName();
	}
	
	protected void queue(Plot<T> plot)
	{
		m_inQueue.add(plot);
		System.out.println(String.format("Adding to queue, now total: %s", m_inQueue.size()));
		execute();
	}
	
	synchronized protected Plot<T> getNext()
	{
		return m_inQueue.poll();
	}
	
	protected void execute()
	{
		if (!m_buildLogic.canBuild())
			return;
		
		System.out.println(String.format("Executing, in before progress: %s/%s", m_inProgress.size(), getMaxConcurrent()));
		
		if (m_inProgress.size() > getMaxConcurrent())
			return;
		
		Plot<T> next = getNext();
		if (next == null)
			return;
		
		m_inProgress.put(next.getPlotId(), next);
		
		System.out.println(String.format("Executing, in after progress: %s/%s", m_inProgress.size(), getMaxConcurrent()));
		
		m_buildLogic.execute(next, (onComplete) -> {
			
			
			if (onComplete != null)
			{
				Plot<T> inQueue = m_inProgress.remove(onComplete.getPlotId());
				if (inQueue == null)
				{
					System.out.println("Not in queue");
					//TODO severe message: not queued
				}
				
				System.out.println(String.format("Updating status complete: %s", onComplete) );
				m_buildRepo.updateStatus(next, completeStatus(onComplete),  (updateSuccess) -> {
					if (updateSuccess)
					{
						System.out.println("Executing next");
						execute();
					}
					else
					{
						System.out.println("update failed");
						//TODO severe message: update failed
					}
				});
			} else 
			{
				System.out.println("construction failed");
				//TODO severe message : construction failed
			}
		});
		
		if (m_inProgress.size() < getMaxConcurrent())
			execute();
	}
	
}

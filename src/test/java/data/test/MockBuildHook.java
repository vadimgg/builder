package data.test;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.common.collect.Lists;

import build.BuilderHooks;
import build.IPlotAssigner;
import build.IPlotsClient;
import build.builders.IBuildLogic;
import build.builders.IRepairer;
import build.builders.ISpawnUpdater;
import data.AWorldPlotsRepository;
import data.MemberRank;
import data.Plot;
import data.WorldConfig;
import data.WorldStatusReport;

public class MockBuildHook extends BuilderHooks<TestResident> {

	private IBuildLogic<TestResident> builder = new mockBuilder("builder");
	private IBuildLogic<TestResident> upgrader = new mockBuilder("upgrader");
	private IBuildLogic<TestResident> resetter = new mockBuilder("resetter");

	private ISpawnUpdater<TestResident> spawnner = new mockSpawnUpdater();
	private IRepairer<TestResident> repairer = new mockRepairer();
	private IPlotAssigner<TestResident> assigner = new mockAssigner();
	
	private AWorldPlotsRepository repo = new AWorldPlotsRepository<TestResident>(new mockRestClient()) {
		@Override
		protected TestResident newResident(UUID pid) {
			return new TestResident(pid);
		}
	};
	
	public MockBuildHook() {
		super(new WorldConfig());
	}

	@Override
	public AWorldPlotsRepository<TestResident> getPlotsRepository() {
		return repo;
	}

	@Override
	public IBuildLogic<TestResident> getBuilder() {
		return builder;
	}

	@Override
	public IBuildLogic<TestResident> getUpgrader() {
		return upgrader;
	}

	@Override
	public IBuildLogic<TestResident> getReseter() {
		return resetter;
	}

	@Override
	public ISpawnUpdater<TestResident> getSpawnUpdater() {
		return spawnner;
	}

	@Override
	public IRepairer<TestResident> getRepairer() {
		return repairer;
	}

	@Override
	public IPlotAssigner<TestResident> getPlotAssigner() {
		return assigner;
	}

	private class mockAssigner implements IPlotAssigner<TestResident>
	{

		@Override
		public boolean canAssign(Plot<TestResident> plot, UUID pid, MemberRank rank) {
			return true;
		}

		@Override
		public boolean canUnassign(Plot<TestResident> plot, UUID pid) {
			//  Auto-generated method stub
			return true;
		}

		@Override
		public boolean assign(Plot<TestResident> plot, UUID pid, MemberRank rank) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean unassign(Plot<TestResident> plot, UUID pid) {
			return true;
		}
		
	}
	
	private class mockRepairer implements IRepairer<TestResident>
	{

		@Override
		public void updateStatus(WorldStatusReport status) {
			
		}

		@Override
		public boolean isReaperNeeded(Plot<TestResident> plot) {
			return false;
		}

		@Override
		public boolean repair(Plot<TestResident> plot) {
			return true;
		}
		
	}
	
	private class mockSpawnUpdater implements ISpawnUpdater<TestResident>
	{

		@Override
		public void updateStatus(WorldStatusReport status) {
			
		}

		@Override
		public boolean needSpawnUpdate(Plot<TestResident> plot) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean updateSpawn(Plot<TestResident> plot) {
			return true;
		}
		
	}
	
	private class mockBuilder implements IBuildLogic<TestResident>
	{

		private String m_title;
		
		public mockBuilder(String msg, Object... args)
		{
			m_title = String.format(msg, args);
		}
		
		@Override
		public boolean canBuild() {
			System.out.println(String.format("%s: Checking if can build", m_title));
			return true;
		}

		@Override
		public void execute(Plot<TestResident> plot, Consumer<Plot<TestResident>> onComplete) {
			
			
			new Thread(){
			    public void run(){
			    	try {
						Thread.sleep(1000);
						System.out.println(String.format("FINISH %s: executing %s", m_title, plot));
						onComplete.accept(plot);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
			  }.start();
			
		}
	}
	
	private class mockRestClient implements IPlotsClient<TestResident>
	{

		@Override
		public List<Plot<TestResident>> syncGetAll(String serverName, String worldName) {
			System.out.println(String.format("[Rest] Getting all plots %s:worldName", serverName, worldName));
			return Lists.newLinkedList();
		}

		@Override
		public void upsert(Plot<TestResident> plot, Consumer<Boolean> onComplete) {
			System.out.println(String.format("[Rest] Upserting %s", plot));
			onComplete.accept(true);
		}

		@Override
		public void assign(UUID pid, Plot<TestResident> plot, MemberRank rank, Consumer<Boolean> onComplete) {
			System.out.println(String.format("[Rest] Assigning %s.%s -> %s", pid, rank, plot));
			onComplete.accept(true);
		}

		@Override
		public void unassign(UUID pid, Plot<TestResident> plot, Consumer<Boolean> onComplete) {
			System.out.println(String.format("[Rest] Unassigning %s <- %s", pid, plot));
			onComplete.accept(true);
		}

		@Override
		public void unassignAll(Plot<TestResident> plot, Consumer<Boolean> onComplete) {
			System.out.println(String.format("[Rest] Unassigning all from %s", plot));
			onComplete.accept(true);		
		}
		
	}
	
}

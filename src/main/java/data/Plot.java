package data;

import java.util.Map;

import com.google.common.collect.Maps;

public class Plot<T extends AResident> {

	private static final Map<String, Integer> NumPlotsByWorldName = Maps.newConcurrentMap();
	
	synchronized public static <T extends AResident> Plot<T> newPlot(String serverName, String worldName, PlotSchematic schematic)
	{
		Plot<T> result = new Plot<T>();
		
		Integer count = NumPlotsByWorldName.get(worldName);
		NumPlotsByWorldName.put(worldName, count==null? 1 : count+1);
		
		result.m_plotId = count == null ? 0 : count;
		result.m_server = serverName;
		result.m_worldName = worldName;
		result.m_status = new PlotStatus(schematic, schematic, BuildStatus.BUILDING);
		
		return result;
	}
	
	protected String m_server;
	protected String m_worldName;
	protected int m_plotId;
	
	protected String m_name, m_description;
	
	protected PlotVisibility m_visibility = PlotVisibility.PUBLIC;
	protected PlotStatus m_status;
	
	protected T m_resident;
	
	protected Plot() {}
	
	protected void setStatus(PlotStatus status)
	{
		m_status = status;
	}
	
	protected void setResident(T resident)
	{
		m_resident = resident;
	}
	
	public PlotStatus getStatus() { return m_status; }
	public int getPlotId() { return m_plotId; }
	public boolean hasResident() { return m_resident != null; }
	public T getResident() { return m_resident; }
	
	@Override
	public String toString()
	{
		return String.format("[PlotId: %s] %s", m_plotId, m_status);
	}
}

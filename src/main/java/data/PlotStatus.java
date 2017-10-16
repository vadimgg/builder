package data;

public class PlotStatus {

	private PlotSchematic m_prev, m_cur;
	private BuildStatus m_status;
	
	public PlotStatus(PlotSchematic prev, PlotSchematic cur, BuildStatus status)
	{
		m_prev = prev;
		m_cur = cur;
		m_status = status;
	}
	
	public PlotSchematic getPrev() { return m_prev; }
	public PlotSchematic getCur() { return m_cur; }
	public BuildStatus getBuildStatus() { return m_status; }

	@Override
	public boolean equals(Object o)
	{
		if (o == null)
			return false;
		
		if ( !(o instanceof PlotStatus) )
			return false;
		
		PlotStatus other = (PlotStatus) o;
		
		return m_status == other.getBuildStatus();
	}
	
	@Override
	public String toString()
	{
		return String.format("[Build Status: %s] [Prev: %s] [Cur: %s]", m_status, m_prev, m_cur);
	}
	
}


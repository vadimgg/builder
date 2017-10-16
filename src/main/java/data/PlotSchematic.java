package data;

public class PlotSchematic {

	private String m_inner, m_outter;
	
	public PlotSchematic(String schematicType)
	{
		m_inner = schematicType;
		m_outter = schematicType;
	}
	
	public PlotSchematic(String inner, String outter)
	{
		m_inner = inner;
		m_outter = outter;
	}
	
	public String getInner() { return m_inner; }
	public String getOutter() { return m_outter; }
	
	@Override
	public String toString()
	{
		return String.format("Inner: %s, Outter: %s", m_inner, m_outter);
	}
	
}

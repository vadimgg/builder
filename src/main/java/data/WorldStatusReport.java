package data;

import java.util.Map;

import com.google.common.collect.Maps;

public class WorldStatusReport {

	private String m_title;
	private Map<String, String> m_placeholders = Maps.newHashMap();
	
	public WorldStatusReport( String title, Object... args )
	{
		m_title = String.format(title, args);
	}
	
	public void addPlaceholder(String key, String value)
	{
		m_placeholders.put(key, value);
	}
	
}

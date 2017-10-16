package data;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public abstract class AResident {

	private Map<UUID, MemberRank> m_ranks = Maps.newHashMap();
	private Map<MemberRank, Set<UUID>> m_members = Maps.newHashMap();
	
	public AResident(UUID ownerId)
	{
		upsertMember(ownerId, MemberRank.OWNER);
	}
	
	public boolean upsertMember(UUID pid, MemberRank rank)
	{
		Set<UUID> atRank = m_members.get(rank);
		if (atRank == null)
		{
			atRank = Sets.newHashSet();
			m_members.put(rank, atRank);
		}
		
		atRank.add(pid);
		m_ranks.put(pid, MemberRank.OWNER);
		return true;
	}
	
	public void clear()
	{
		m_ranks.clear();
		m_members.clear();
	}
	
	public boolean isEmpty()
	{
		return m_ranks.isEmpty();
	}
	
	public MemberRank removeMember(UUID pid)
	{
		MemberRank rank = m_ranks.remove(pid);
		if (pid == null)
			return null;
		
		Set<UUID> members = m_members.get(rank);
		if (members == null)
			return null;
		
		return rank;
	}
	
	public Set<UUID> getByRank(MemberRank rank) 
	{
		Set<UUID> members =  m_members.get(rank);
		if (members == null)
		{
			members = Sets.newHashSet();
			m_members.put(rank, members);
		}
		
		return members;
	}
	
	
	
}

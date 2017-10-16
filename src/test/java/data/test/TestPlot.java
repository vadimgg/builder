package data.test;

import org.junit.Assert;
import org.junit.Test;

import build.WorldBuilder;

public class TestPlot {

	private WorldBuilder<TestResident> m_builder = new WorldBuilder<TestResident>(new MockBuildHook());
	
	@Test
	public void testPlot()
	{
	
	 new Thread(){
	    public void run(){
	    	m_builder.build(100);
	    }
	  }.start();
		
		try {
			Thread.sleep(13000);
			System.out.println("Done sleeping");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}

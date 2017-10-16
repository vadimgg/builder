package data;

public class WorldConfig {

	public String getBaseSchematic() { return "default"; }
	public String getWorldName() { return "default_prisons"; }
	
	public int maxConcurrentBuilds() { return 10; }
	public int maxConcurrentUpgrades() { return 10; }
	public int maxConcurrentResets() { return 10; }
}

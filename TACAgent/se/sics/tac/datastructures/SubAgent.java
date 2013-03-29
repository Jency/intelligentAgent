package se.sics.tac.datastructures;

public abstract class SubAgent {
	MasterAgent masterAgent;
	
	public abstract void initialise();
	public abstract void mainLoop();
}

package se.sics.tac.datastructures;

import se.sics.tac.agent.MasterAgent;

public abstract class SubAgent {
	MasterAgent masterAgent;
	
	public abstract void initialise();
	public abstract void mainLoop();
}

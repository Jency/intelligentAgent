package se.sics.tac.agent;


public abstract class SubAgent extends Thread {
	public MasterAgent masterAgent;
	
	public abstract void initialise();
	public abstract void run(); // The main execution of the sub-agent

}

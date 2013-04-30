package se.sics.tac.agent;

import java.util.*;
import se.sics.tac.aw.AgentImpl;
import se.sics.tac.aw.Bid;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.datastructures.*;
import se.sics.tac.util.ArgEnumerator;


public class MasterAgent extends AgentImpl{
	public static void main(String[] args) {
		// Start up the agent
		TACAgent.main(args);
	}
	
	List<Client> clientList = new ArrayList<Client>();
	FlightAgent flightAgent;
	HotelAgent hotelAgent;
	EntertainmentAgent entertainmentAgent;

	@Override
	protected void init(ArgEnumerator args) {
		// TODO Auto-generated method stub
	}

	@Override
	public void bidUpdated(Bid bid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bidRejected(Bid bid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bidError(Bid bid, int error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameStarted() {
		// Load client preferences into table
		for (int i = 0; i < 8; i++){		
			Client newClient = new Client();
			
			newClient.arrivalDay = agent.getClientPreference(i, TACAgent.ARRIVAL);
			newClient.departureDay = agent.getClientPreference(i, TACAgent.DEPARTURE);
			newClient.hotelvalue = agent.getClientPreference(i, TACAgent.HOTEL_VALUE);
			newClient.aligatorWrestlingValue = agent.getClientPreference(i, TACAgent.E1);
			newClient.amusementParkValue = agent.getClientPreference(i, TACAgent.E2);
			newClient.museumValue = agent.getClientPreference(i, TACAgent.E3);
					
			clientList.add(newClient);
		}
		
		// Initialise the sub-agent objects
		flightAgent = new FlightAgent();
		flightAgent.masterAgent = this;
		flightAgent.initialise();
		
		hotelAgent = new HotelAgent();
		hotelAgent.masterAgent = this;
		hotelAgent.initialise();
		
		entertainmentAgent = new EntertainmentAgent();
		entertainmentAgent.masterAgent = this;
		entertainmentAgent.initialise();
		
		// Fire off the sub-agent thread
		flightAgent.start();
		hotelAgent.start();
		entertainmentAgent.start();
		
		
		System.out.println("Initialisd game");
		
	}

	@Override
	public void gameStopped() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void auctionClosed(int auction) {
		// TODO Auto-generated method stub
		
	}
	
}

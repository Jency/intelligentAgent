package se.sics.tac.agent;
import java.util.*;

import se.sics.tac.aw.DummyAgent;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.datastructures.ListItem;


public class MasterAgent {
	public static void main(String[] args) {
		TACAgent.main(args);
	}
	
	List<ListItem> ShoppingList = new ArrayList<ListItem>();
	FlightAgent flightAgent;
	HotelAgent hotelAgent;
	EntertainmentAgent entertainmentAgent;
	
	public DummyAgent auctionInterface;
	
	public MasterAgent(){
		// Initialise the dummy agent
		auctionInterface = new DummyAgent();
	}
	
}

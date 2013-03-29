package se.sics.tac.datastructures;

import se.sics.tac.aw.Auction;

	

public class ListItem {
	int day;
	int QuantityRequired;
	int QuantityHeld;
	Auction auction;
	
	
	public class HotelListItem extends ListItem{
		HotelSubType subType;
	}
	
	public class FlightListItem extends ListItem{
		FlightSubType subType;
	}
	public class EntertainmentListItem extends ListItem{
		EntertainmentSubType subType;
	}
	
	
	public enum HotelSubType {cheap, good};
	public enum FlightSubType {in, out};
	public enum EntertainmentSubType {aw, ap, mu};
}
 


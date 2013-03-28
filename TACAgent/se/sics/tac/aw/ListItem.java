package se.sics.tac.aw;

	

public class ListItem {
	int day;
	int subType;
	int QuantityRequired;
	int QuantityHeld;
	Auction auction;
	
	
	public class HotelListItem extends ListItem{
		public enum HotelSybType {cheap, good};
		
	}
	public class FlightListItem extends ListItem{
		public enum FlightSubType {in, out};
	}
	public class EntertainmentListItem extends ListItem{
		public enum EntertainmentSubType {aw, ap, mu};
	}
}
 


package se.sics.tac.datastructures;



public abstract class ListItem {
	public int day;
	public int QuantityRequired;
	public int QuantityHeld;
	public Auction auction;
	public int clientNum;
	public float clientValue;
	public int paid;
	
	public enum HotelSubType {cheap, good};
	public enum FlightSubType {in, out};
	public enum EntertainmentSubType {aw, ap, mu};
}

 


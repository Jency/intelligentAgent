package se.sics.tac.datastructures;


public class Client {
	public int arrivalDay;
	public int departureDay;
	public int hotelvalue;
	public int aligatorWrestlingValue;
	public int amusementParkValue;
	public int museumValue;
	
	public itemStatus arrivalDayStatus = itemStatus.fresh;
	public itemStatus departureDayStatus = itemStatus.fresh;
	public itemStatus hotelStatus = itemStatus.fresh;
	public itemStatus aligatorWrestlingStatus = itemStatus.fresh;
	public itemStatus amusementParkStatus = itemStatus.fresh;
	public itemStatus museumStatus = itemStatus.fresh;
	
	public hotelType hotelAssignment;
	public int aligatorWrestlingDay = 0;
	public int amusementParkDay = 0;
	public int museumDay = 0;
	
	public enum itemStatus{fresh, bidding, purchased, failed};
	public enum hotelType{cheapHotel, expensiveHotel};
}

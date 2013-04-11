package se.sics.tac.datastructures;

public class Auction {
	int initialPrice;
	int currentPrice;
	int bidPrice;
	int maxPrice;
	
	public Auction (int initialPrice, int currentPrice, int bidPrice, int maxPrice){
		this.initialPrice = initialPrice;
		this.currentPrice = currentPrice;
		this.bidPrice = bidPrice;
		this.maxPrice = maxPrice;
		
	}
}

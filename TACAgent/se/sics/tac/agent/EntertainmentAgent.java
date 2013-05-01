package se.sics.tac.agent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import javax.swing.Timer;

import se.sics.tac.aw.Bid;
import se.sics.tac.aw.Quote;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.aw.Transaction;
import se.sics.tac.datastructures.*;
import se.sics.tac.datastructures.Client.*;
import sun.java2d.loops.CustomComponent;

public class EntertainmentAgent extends SubAgent {
	Semaphore timingSemaphore;
	int FREQUENCY = 30; // How often to repeat the main loop in seconds
	
	int[] aligators = new int[5];
	int[] museum = new int[5];
	int[] park = new int[5];
	
	List<Bid> bidlist = new ArrayList<Bid>();
	Boolean[] transactionDirection = new Boolean[30]; // True means buy, false means sell. Index is auctionID
	
	//int TICKET_BUY_PRICE = 100;
	int TICKET_SELL_PRICE = 75;

	int ESTIMATED_CLOSE_PRICE = 80;
	
	float PURCHASE_PROFIT_MARGIN = 0.7f; //(Aim to make at least 30% on buying entertainment)
	
	@Override
	public void initialise() {	
		// Check what entertainment tickets we have been assigned
				for (int i = 1; i <= 4; i++) {
					System.out.println(masterAgent.agent.getOwn(TACAgent.getAuctionFor(TACAgent.CAT_ENTERTAINMENT,TACAgent.TYPE_ALLIGATOR_WRESTLING, i))+ "  aligator tickets for day " + i);
					System.out.println(masterAgent.agent.getOwn(TACAgent.getAuctionFor(TACAgent.CAT_ENTERTAINMENT,TACAgent.TYPE_AMUSEMENT, i))+ "  amusement park tickets for day " + i);
					System.out.println(masterAgent.agent.getOwn(TACAgent.getAuctionFor(TACAgent.CAT_ENTERTAINMENT,TACAgent.TYPE_MUSEUM, i))+ "  museum tickets for day " + i);

					// Store the assignment
					aligators[i] = masterAgent.agent.getOwn(TACAgent.getAuctionFor(TACAgent.CAT_ENTERTAINMENT,TACAgent.TYPE_ALLIGATOR_WRESTLING, i));
					museum[i] = masterAgent.agent.getOwn(TACAgent.getAuctionFor(TACAgent.CAT_ENTERTAINMENT,TACAgent.TYPE_MUSEUM, i));
					park[i] = masterAgent.agent.getOwn(TACAgent.getAuctionFor(TACAgent.CAT_ENTERTAINMENT, TACAgent.TYPE_AMUSEMENT, i));
				}
				
				
				for (Client client: masterAgent.clientList){
					List<EntertainmentValuePair> entertainmentRanking = new ArrayList<EntertainmentValuePair>();  

					// Put each entertainment type into the list
					entertainmentRanking.add(new EntertainmentValuePair(EntertainmentTypes.aligatorWrestling, client.aligatorWrestlingValue));
					entertainmentRanking.add(new EntertainmentValuePair(EntertainmentTypes.amusementPark, client.amusementParkValue));
					entertainmentRanking.add(new EntertainmentValuePair(EntertainmentTypes.museum, client.museumValue));
					
					// Rank the entertainment types, highest first
					Collections.sort(entertainmentRanking);
					Collections.reverse(entertainmentRanking);
					
					// for each day the client spends on holiday except the last
					for (int day = client.arrivalDay ; day < client.departureDay; day++ ){
						
						// Request the relevent entertainment
						if  (entertainmentRanking.size() > 0){
							EntertainmentTypes type = entertainmentRanking.get(0).type;
							
							switch (type){
							case aligatorWrestling:
								// Show a desire for alligator wrestling
								client.aligatorWrestlingStatus = itemStatus.requested;
								client.aligatorWrestlingDay = day;
								break;
								
							case amusementPark:
								// Show a desire for the amusement park
								client.amusementParkStatus = itemStatus.requested;
								client.amusementParkDay = day;
								break;
								
							case museum:
								// Show a desire for the museum
								client.museumStatus = itemStatus.requested;
								client.museumDay = day;
								break;
							}
							
							entertainmentRanking.remove(0);
						}
					}
				}
				
				// Allocate tickets currently held
				for (int i = 1; i <= 4; i++) {
					assignAlligatorTickets(i);
					assignAmusementParkTickets(i);
					assignMuseumTickets(i);
				}
				System.out.println("Initial allocation calculated");
				
				// Put out buy bids for the tickets we would like
				for (Client client : masterAgent.clientList){
					if (client.aligatorWrestlingStatus == itemStatus.requested){
						int auction = TACAgent.getAuctionFor(TACAgent.CAT_ENTERTAINMENT, TACAgent.TYPE_ALLIGATOR_WRESTLING, client.aligatorWrestlingDay);
						Bid AWBid = new Bid(auction);
						AWBid.addBidPoint(1, client.aligatorWrestlingValue * PURCHASE_PROFIT_MARGIN);
						masterAgent.agent.submitBid(AWBid);
						bidlist.add(AWBid);
						transactionDirection[auction] = true;
						
						client.aligatorWrestlingStatus = itemStatus.bidding;
						System.out.println("Buying aligator ticket on day " + client.aligatorWrestlingDay + " for " + client.aligatorWrestlingValue * PURCHASE_PROFIT_MARGIN);
					}
					if (client.amusementParkStatus == itemStatus.requested){
						int auction = TACAgent.getAuctionFor(TACAgent.CAT_ENTERTAINMENT, TACAgent.TYPE_AMUSEMENT, client.amusementParkDay);
						Bid APBid = new Bid(auction);
						APBid.addBidPoint(1, client.amusementParkValue * PURCHASE_PROFIT_MARGIN);
						masterAgent.agent.submitBid(APBid);
						bidlist.add(APBid);
						transactionDirection[auction] = true;
						
						client.amusementParkStatus = itemStatus.bidding;
						System.out.println("Buying park ticket on day " + client.amusementParkDay + " for " + client.amusementParkValue * PURCHASE_PROFIT_MARGIN);
					}
					if (client.museumStatus == itemStatus.requested){
						int auction = TACAgent.getAuctionFor(TACAgent.CAT_ENTERTAINMENT, TACAgent.TYPE_MUSEUM, client.museumDay);
						Bid MUBid = new Bid(auction);
						MUBid.addBidPoint(1, client.museumValue * PURCHASE_PROFIT_MARGIN);
						masterAgent.agent.submitBid(MUBid);
						bidlist.add(MUBid);
						transactionDirection[auction] = true;
						
						client.museumStatus = itemStatus.bidding;
						System.out.println("Buying museum ticket on day " + client.museumDay + " for " + client.museumValue * PURCHASE_PROFIT_MARGIN);
					}
				}
				
				// Put out sell bids for tickets we do not need (average price + 10%)
				for (int day = 1; day < 5; day++){
					int aligatorAuction = TACAgent.getAuctionFor(TACAgent.CAT_ENTERTAINMENT, TACAgent.TYPE_ALLIGATOR_WRESTLING, day);
					int parkAuction = TACAgent.getAuctionFor(TACAgent.CAT_ENTERTAINMENT, TACAgent.TYPE_AMUSEMENT, day);
					int museumAuction = TACAgent.getAuctionFor(TACAgent.CAT_ENTERTAINMENT, TACAgent.TYPE_MUSEUM, day);
					
					
					if (aligators[day] > 0){
						Bid ALBid = new Bid(aligatorAuction);
						ALBid.addBidPoint(-aligators[day], TICKET_SELL_PRICE);
						masterAgent.agent.submitBid(ALBid);
						bidlist.add(ALBid);
						transactionDirection[aligatorAuction] = false;
						System.out.println("Selling " + aligators[day] + " aligator ticket(s) on day " + day + " for " + TICKET_SELL_PRICE);
					}
					
					if (park[day] > 0){
						Bid PABid = new Bid(parkAuction);
						PABid.addBidPoint(-park[day], TICKET_SELL_PRICE);
						masterAgent.agent.submitBid(PABid);
						bidlist.add(PABid);
						transactionDirection[parkAuction] = false;
						System.out.println("Selling " + park[day] + " park ticket(s) on day " + day + " for " + TICKET_SELL_PRICE);
					}
					
					if (museum[day] > 0){
						Bid MUBid = new Bid(museumAuction);
						MUBid.addBidPoint(-museum[day], TICKET_SELL_PRICE);
						masterAgent.agent.submitBid(MUBid);
						bidlist.add(MUBid);
						transactionDirection[museumAuction] = false;
						System.out.println("Selling " + museum[day] + " museum ticket(s) on day " + day + " for " + TICKET_SELL_PRICE);
					}
				}
	}

	@Override
	public void run() {
		// Set up the delay timer 
		int delay = 1000 * FREQUENCY;
		timingSemaphore = new Semaphore(1);
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				timingSemaphore.release();
			}
		};
		new Timer(delay, taskPerformer).start();

		while (true) {
			try {
				timingSemaphore.acquire();
			} catch (Exception e) {
				e.printStackTrace();
			}
			doWork();
		}
	}

	private void doWork() {

		// System.out.println("Doing work");
		// Re-evaluate the entertainment allocation

		System.out.println("************************");
		// For each client
		for (Client client : masterAgent.clientList) {

			System.out.println();
			System.out.println("For client " + masterAgent.clientList.indexOf(client) + ":");			
			
			// If this client has an aligator ticket
			if (client.aligatorWrestlingStatus == itemStatus.purchased) {
				// Get the current market price for that ticket
				int auctionID = TACAgent.getAuctionFor(TACAgent.CAT_ENTERTAINMENT, TACAgent.TYPE_ALLIGATOR_WRESTLING, client.aligatorWrestlingDay);
				Quote marketPriceQuote = masterAgent.agent.getQuote(auctionID);
				float marketPrice = marketPriceQuote.getBidPrice();
				
				System.out.println(client.aligatorWrestlingValue + " : " + marketPrice + " - Aligatgors");
				
				// If there is more than 1 minute left in the game & market price is above ESTIMATED_CLOSE_PRICE OR there is less than 1 minute in the game 
				if ((masterAgent.agent.getGameTimeLeft() / 1000 > 60) && (marketPrice > ESTIMATED_CLOSE_PRICE)  || (masterAgent.agent.getGameTimeLeft() / 1000 >= 60)) {					
					// If the market price is higher than the client bonus AND
					// is higher than the estimated closing price
					if (marketPrice > client.aligatorWrestlingValue && marketPrice > ESTIMATED_CLOSE_PRICE) {
						// Sell the ticket
						Bid bid = new Bid(auctionID);
						bid.addBidPoint(-1, marketPrice);
						client.aligatorWrestlingStatus = itemStatus.requested;

						// Put out a bid at the client's price (minus profit
						// margin), just in case one becomes available
						bid.addBidPoint(1, client.aligatorWrestlingValue * PURCHASE_PROFIT_MARGIN);
						client.aligatorWrestlingStatus = itemStatus.bidding;
						masterAgent.agent.submitBid(bid);
						bidlist.add(bid);
						//TODO: Check that both bid points are being submitted here
						transactionDirection[auctionID] = true;

						System.out.println("Ruthlessly sold a client's aligator wresting ticket on day " + client.aligatorWrestlingDay + " for " + marketPrice + " as the client only offered " + client.aligatorWrestlingValue);
						System.out.println("Also put out another bid for that ticket at " + client.aligatorWrestlingValue * PURCHASE_PROFIT_MARGIN + "just in case");
					} else {
						//System.out.println("No sneaky sales to be made");
					}
				}
			}	
				
			// If this client has a park ticket
			if (client.amusementParkStatus == itemStatus.purchased) {
				// Get the current market price for that ticket
				int parkAuctionID = TACAgent.getAuctionFor(TACAgent.CAT_ENTERTAINMENT, TACAgent.TYPE_AMUSEMENT, client.amusementParkDay);
				Quote parkMarketPriceQuote = masterAgent.agent.getQuote(parkAuctionID);
				float parkMarketPrice = parkMarketPriceQuote.getAskPrice();
					
				System.out.println(client.amusementParkValue+ " : " + parkMarketPrice + " - Park");
					
				// If there is more than 1 minute left in the game & market price is above ESTIMATED_CLOSE_PRICE OR there is less than 1 minute in the game 
				if ((masterAgent.agent.getGameTimeLeft() / 1000 > 60) && (parkMarketPrice > ESTIMATED_CLOSE_PRICE)  || (masterAgent.agent.getGameTimeLeft() / 1000 >= 60)) {
					// If the market price is higher than the client bonus
					// AND is higher than the estimated closing price
					if (parkMarketPrice > client.amusementParkValue) {
						// Sell the ticket
						Bid bid = new Bid(parkAuctionID);
						bid.addBidPoint(-1, parkMarketPrice);
						client.amusementParkStatus = itemStatus.requested;

						// Put out a bid at the client's price (minus profit margin), just in case one becomes available
						bid.addBidPoint(1, client.amusementParkValue * PURCHASE_PROFIT_MARGIN);
						client.amusementParkStatus = itemStatus.bidding;
						masterAgent.agent.submitBid(bid);
						bidlist.add(bid);
						transactionDirection[parkAuctionID] = true;

						System.out.println("Ruthlessly sold a client's park wresting ticket on day " + client.amusementParkDay + " for " + parkMarketPrice + " as the client only offered " + client.amusementParkValue);
						System.out.println("Also put out another bid for that ticket at " + client.amusementParkValue * PURCHASE_PROFIT_MARGIN + "just in case");
					} else {
						//System.out.println("No sneaky sales to be made");
					}
				} else {
					System.out.println("Less than a minute left");
					// Sell the ticket
				}
			}
			
			// If this client has a museum ticket
			if (client.museumStatus == itemStatus.purchased) {
				// Get the current market price for that ticket
				int museumAuctionID = TACAgent.getAuctionFor(TACAgent.CAT_ENTERTAINMENT, TACAgent.TYPE_MUSEUM, client.amusementParkDay);
				Quote museumMarketPriceQuote = masterAgent.agent.getQuote(museumAuctionID);
				float museumMarketPrice = museumMarketPriceQuote.getAskPrice();

				
				System.out.println(client.museumValue + " : " + museumMarketPrice + " - Museum");
					
						
				// If there is more than 1 minute left in the game & market price is above ESTIMATED_CLOSE_PRICE OR there is less than 1 minute in the game 
				if ((masterAgent.agent.getGameTimeLeft() / 1000 > 60) && (museumMarketPrice > ESTIMATED_CLOSE_PRICE)  || (masterAgent.agent.getGameTimeLeft() / 1000 >= 60)) {
					// If the market price is higher than the client
					// bonus AND is higher than the estimated closing
					// price
					if (museumMarketPrice > client.museumValue) {
						// Sell the ticket
						Bid bid = new Bid(museumAuctionID);
						bid.addBidPoint(-1, museumMarketPrice);
						client.museumStatus = itemStatus.requested;

						// Put out a bid at the client's price (minus
						// profit margin), just in case one becomes
						// available
						bid.addBidPoint(1, client.museumValue * PURCHASE_PROFIT_MARGIN);
						client.museumStatus = itemStatus.bidding;
						masterAgent.agent.submitBid(bid);
						bidlist.add(bid);
						transactionDirection[museumAuctionID] = true;

						System.out.println("Ruthlessly sold a client's museum wresting ticket on day " + client.museumDay + " for " + museumMarketPrice + " as the client only offered " + client.museumValue);
						System.out.println("Also put out another bid for that ticket at " + client.museumValue * PURCHASE_PROFIT_MARGIN + "just in case");
					} else {
						//System.out.println("No sneaky sales to be made");
					}
				} else {
					System.out.println("Less than a minute left");
					// Sell the ticket
				}
			}

//			System.out.println("Tried to make sneaky sales. Values are");
//			System.out.println("Cust : Market");
//			System.out.println(client.aligatorWrestlingValue + " - Aligator");
//			System.out.println(client.amusementParkValue + " - Park");
//			System.out.println(client.museumValue + " - museum");

		}
		System.out.println("************************");
	}

	
	public void bidUpdated(Bid bid) {
		//System.out.println("Updated bid :" + bid.getBidString());
//		System.out.println("Updated bid. Appears in bid list? " + bidlist.contains(bid) + " String:" + bid.getBidString());
		if(!bidlist.contains(bid)){
//			bid.getQuantity();
		}
	}

	public void transaction(Transaction transaction){
		// A transaction has taken place. Assign the item(s) to a deserving client
		int quantity = transaction.getQuantity();
		int type = TACAgent.getAuctionType(transaction.getAuction());
		int day = TACAgent.getAuctionDay(transaction.getAuction());
		float price = transaction.getPrice();
		
		// If this is a buy transaction
		if (transactionDirection[transaction.getAuction()]){
		System.out.println("Transaction quantity (of below):" + transaction.getQuantity());
		for (int i = 0; i < quantity; i++){
			Boolean exitFlag = false;
			for (Client client : masterAgent.clientList){
				// Depending on what entertainment type
				switch (type){
				case 1: // Alligator wrestling
					// If this customer was bidding for an alligator ticket on that day AND their value is above the ticket price
					if ((client.aligatorWrestlingStatus == itemStatus.bidding) && (client.aligatorWrestlingDay == day) && (client.aligatorWrestlingValue >= price)){ 
						// Assign the ticket to them
						client.aligatorWrestlingStatus = itemStatus.purchased;
						exitFlag = true;
						System.out.println("Asigned a purchesed aligator ticket (day " + day + ") to client " + masterAgent.clientList.indexOf(client));
					}
					break;
				case 2: // Amusement park
					// If this customer was bidding for a park ticket on that day AND their value is above the ticket price
					if ((client.amusementParkStatus == itemStatus.bidding) && (client.amusementParkDay == day) && (client.amusementParkValue >= price)){ 
						// Assign the ticket to them
						client.amusementParkStatus = itemStatus.purchased;
						exitFlag = true;
						System.out.println("Asigned a purchesed park ticket (day " + day + ") to client " + masterAgent.clientList.indexOf(client));
					}
					break;
					
				case 3: // Museum
					// If this customer was bidding for a museum ticket on that day AND their value is above the ticket price
					if ((client.museumStatus == itemStatus.bidding) && (client.museumDay == day) && (client.museumValue >= price)){ 
						// Assign the ticket to them
						client.museumStatus = itemStatus.purchased;
						exitFlag = true;
						System.out.println("Asigned a purchesed museum ticket (day " + day + ") to client " + masterAgent.clientList.indexOf(client));
					}
					break;
				}
				if (exitFlag) break;
			}
		}
	}
		
		
//		System.out.println("Transaction");
//		System.out.println("Auction: " + transaction.getAuction());
//		System.out.println("Auction type: " + TACAgent.getAuctionType(transaction.getAuction()));
//		System.out.println("Auction day: " + TACAgent.getAuctionDay(transaction.getAuction()));
//		System.out.println("Price: " + transaction.getPrice());
//		System.out.println("Quantity: " + transaction.getQuantity());
	}
	
	// Rationalise the customers entertainment interests. Eg if a customer is staying 1 day, show that they are only interested in the entertainment type of highest value 
	public void rationaliseCustomerInterest(){
		for (Client client : masterAgent.clientList){ // For each customer
			if ((client.departureDay - client.arrivalDay) == 1){ // If they'll be here for one night
				// Clear all but the highest value entertainment type (Test each value. If something is bigger that it, it is not the biggest)
				if (client.aligatorWrestlingValue > client.amusementParkValue) {client.amusementParkStatus = itemStatus.failed;} else {client.aligatorWrestlingStatus = itemStatus.failed;}
				if (client.amusementParkValue > client.museumValue) {client.museumStatus = itemStatus.failed;} else {client.amusementParkStatus = itemStatus.failed;}
				if (client.aligatorWrestlingValue > client.museumValue) {client.museumStatus = itemStatus.failed;} else {client.aligatorWrestlingStatus = itemStatus.failed;}
				client.arrivalDayStatus = itemStatus.fresh;
			}
			else if ((client.departureDay - client.arrivalDay) == 2){ // If they'll be here for two nights
				// Clear the lowest value entertainment type (If both the other items are bigger than you, you are the smallest)
				if (client.aligatorWrestlingValue < client.amusementParkValue && client.aligatorWrestlingValue < client.museumValue){client.aligatorWrestlingStatus = itemStatus.failed;}
				if (client.amusementParkValue < client.aligatorWrestlingValue && client.amusementParkValue < client.museumValue){client.amusementParkStatus = itemStatus.failed;}
				if (client.museumValue < client.aligatorWrestlingValue && client.museumValue < client.amusementParkValue){client.museumStatus = itemStatus.failed;}
			}
		}
	}
	
	// Looks up the currently held alligator tickets for the current day and assigns them to the highest "bidder"
	public void assignAlligatorTickets(int day) {
		// List to store interested clients and their respective values
		List<ClientValuePair> aligatorInterest = new ArrayList<ClientValuePair>();

		// Find each customer that could use an alligator ticket that day
		for (int ii = 0; ii < 9; ii++) {
			if (ii < 8) {
				Client client = masterAgent.clientList.get(ii);

				// If the customer could use the ticket
				if (day >= client.arrivalDay
						&& day < client.departureDay
						&& client.aligatorWrestlingStatus == itemStatus.requested
						&& client.museumDay != day
						&& client.amusementParkDay != day) {
					// add that customer to the list
					ClientValuePair newPoint = new ClientValuePair(client,
							client.aligatorWrestlingValue);
					aligatorInterest.add(newPoint);
				}
			} else {// Customer 8 is the open market
				// TODO: Check current market price
			}
		}

		// Sort the people with interest in the ticket, highest first
		Collections.sort(aligatorInterest);
		Collections.reverse(aligatorInterest);

		// Assign the tickets
		int aligatorTicketsHeld = aligators[day];
		int interestedParties = aligatorInterest.size();
		for (int ii = 0; ii < Math.min(aligatorTicketsHeld, interestedParties); ii++) {
			// If the client will pay more than the average ticket value, assign it to them
			if (aligatorInterest.get(ii).client.amusementParkValue > ESTIMATED_CLOSE_PRICE) {
				aligatorInterest.get(ii).client.aligatorWrestlingStatus = itemStatus.purchased;
				aligatorInterest.get(ii).client.aligatorWrestlingDay = day;
				aligators[day] -= 1;
			}
		}
	}

	// Looks up the currently held alligator tickets for the current day and assigns them to the highest "bidder"
	public void assignMuseumTickets(int day) {
		// LIst to store interested clients and their respective values
		List<ClientValuePair> museumInterest = new ArrayList<ClientValuePair>();

		// Find each customer that could use an alligator ticket that day
		for (int ii = 0; ii < 9; ii++) {
			if (ii < 8) {
				Client client = masterAgent.clientList.get(ii);

				// If the customer could use the ticket
				if (day >= client.arrivalDay && day < client.departureDay
						&& client.museumStatus == itemStatus.requested
						&& client.aligatorWrestlingDay != day
						&& client.amusementParkDay != day) {
					// add that customer to the list
					ClientValuePair newPoint = new ClientValuePair(client,
							client.museumValue);
					museumInterest.add(newPoint);
				}
			} else {// Customer 8 is the open market
				int auctionID = TACAgent.getAuctionFor(TACAgent.CAT_ENTERTAINMENT, TACAgent.TYPE_MUSEUM, day);
				Quote currentPriceQuote = masterAgent.agent.getQuote(auctionID);
				currentPriceQuote.getAskPrice();
			}
		}

		// Sort the people with interest in the ticket, highest first
		Collections.sort(museumInterest);
		Collections.reverse(museumInterest);

		// Assign the tickets
		int museumTicketsHeld = museum[day];
		int interestedParties = museumInterest.size();
		for (int ii = 0; ii < Math.min(museumTicketsHeld, interestedParties); ii++) {
			// If the client will pay more than the average ticket value, assign
			// it to them
			if (museumInterest.get(ii).client.amusementParkValue > ESTIMATED_CLOSE_PRICE) {
				museumInterest.get(ii).client.museumStatus = itemStatus.purchased;
				museumInterest.get(ii).client.museumDay = day;
				museum[day] -= 1;
			}
		}
	}

	// Looks up the currently held amusement park tickets for the current day and assigns them to the highest "bidder"
	public void assignAmusementParkTickets(int day) {
		// LIst to store interested clients and their respective values
		List<ClientValuePair> amusementParkInterest = new ArrayList<ClientValuePair>();

		// Find each customer that could use an alligator ticket that day
		for (int ii = 0; ii < 9; ii++) {
			if (ii < 8) {
				Client client = masterAgent.clientList.get(ii);

				// If the customer could use the ticket
				if (day >= client.arrivalDay && day < client.departureDay
						&& client.amusementParkStatus == itemStatus.requested
						&& client.museumDay != day
						&& client.aligatorWrestlingDay != day) {
					// add that customer to the list
					ClientValuePair newPoint = new ClientValuePair(client,
							client.museumValue);
					amusementParkInterest.add(newPoint);
				}
			} else {// Customer 8 is the open market
				// TODO: Check current market price
			}
		}

		// Sort the people with interest in the ticket, highest first
		Collections.sort(amusementParkInterest);
		Collections.reverse(amusementParkInterest);

		// Assign the tickets
		int amusementParkTicketsHeld = park[day];
		int interestedParties = amusementParkInterest.size();
		for (int ii = 0; ii < Math.min(amusementParkTicketsHeld, interestedParties); ii++) {
			// If the client will pay more than the average ticket value, assign it to them
			if (amusementParkInterest.get(ii).client.amusementParkValue > ESTIMATED_CLOSE_PRICE) {
				amusementParkInterest.get(ii).client.amusementParkStatus = itemStatus.purchased;
				amusementParkInterest.get(ii).client.amusementParkDay = day;
				park[day] -= 1;
			}
		}
	}

}

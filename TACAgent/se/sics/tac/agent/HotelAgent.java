package se.sics.tac.agent;

import se.sics.tac.aw.Bid;
import se.sics.tac.aw.Quote;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.datastructures.Client;
import se.sics.tac.datastructures.Client.hotelType;
import se.sics.tac.datastructures.Client.itemStatus;

public class HotelAgent extends SubAgent {
Bid[] expensiveHotelBids = new Bid[5];
Bid[] cheapHotelBids = new Bid[5];

static int HOTEL_BUDGET = 600;

	@Override
	public void initialise() 
	{	
		// Initialise the bid arrays
		for (int i = 1 ; i <= 4; i++){
			int expensiveAuctionID = TACAgent.getAuctionFor(TACAgent.CAT_HOTEL, TACAgent.TYPE_GOOD_HOTEL, i);
			Bid expensiveBid = new Bid(expensiveAuctionID);
			int cheapAuctionID = TACAgent.getAuctionFor(TACAgent.CAT_HOTEL, TACAgent.TYPE_CHEAP_HOTEL, i);
			Bid cheapBid = new Bid(cheapAuctionID);
			
			expensiveHotelBids[i] = expensiveBid;
			cheapHotelBids[i] = cheapBid;
		}
		
		// Bid the price and Assign type of hotel to the customer
		for (int i = 0; i < 8 ; i++)
		{
			Client client = masterAgent.clientList.get(i);
			int hotelval = masterAgent.agent.getClientPreference(i, TACAgent.HOTEL_VALUE);
		
		// If customer's value is more than 70, we will try to buy expensive hotel rooms			
			if (hotelval > 70){
				client.hotelAssignment = hotelType.expensiveHotel;
			}
			else{
				client.hotelAssignment = hotelType.cheapHotel;
			}	
			
			
			// Assign Hotel for each night
			for (int j = client.arrivalDay; j < client.departureDay; j++)
					{
				int auctionID;
				if (client.hotelAssignment == hotelType.expensiveHotel){
					auctionID = TACAgent.getAuctionFor(TACAgent.CAT_HOTEL, TACAgent.TYPE_GOOD_HOTEL, j);
					// calculate the price per room for this customer (add the hotel bonus if booking expensive hotel)
					float budgetPerNight = (HOTEL_BUDGET + client.hotelvalue) / (client.departureDay - client.arrivalDay);
					expensiveHotelBids[j].addBidPoint(1, budgetPerNight);
					System.out.println("Submitted bid for expensive hotel on day " + j);
				}
				else {
					auctionID = TACAgent.getAuctionFor(TACAgent.CAT_HOTEL, TACAgent.TYPE_CHEAP_HOTEL, j);
					// calculate the price per room for this customer
					float budgetPerNight = HOTEL_BUDGET / (client.departureDay - client.arrivalDay);
					cheapHotelBids[j].addBidPoint(1, budgetPerNight);
					System.out.println("Submitted bid for cheap hotel on day " + j);
				}
			}

		}
		
		// Submit the completed bids
		for (int i = 1 ; i <= 4; i++){
			masterAgent.agent.submitBid(expensiveHotelBids[i]);
			masterAgent.agent.submitBid(cheapHotelBids[i]);
		}


						//int auctionID = TACAgent.getAuctionFor(TACAgent.CAT_HOTEL, TACAgent.TYPE_GOOD_HOTEL, j);
						
						//Quote T = new Quote(TACAgent.getAuctionFor(TACAgent.CAT_HOTEL, TACAgent.TYPE_GOOD_HOTEL, j));
						//float bidprice = T.getBidPrice();
						//float aprice = T.getAskPrice();
						//System.out.println("bidprice is :"+ bidprice);
						//System.out.println("askPrice is :"+ aprice);
						//if (bidprice +1 < aprice)
						//{
							//B.addBidPoint(1, aprice);
							//masterAgent.agent.submitBid(B);
						//}
//					}
//			}
//			else 
//			{
//				//client.hotelAssignment = hotelType.expensiveHotel;
//				for (int j = client.arrivalDay; j < client.departureDay; j++)
//				{
//					int auctionID = TACAgent.getAuctionFor(TACAgent.CAT_HOTEL, TACAgent.TYPE_CHEAP_HOTEL, j);
//					Bid B = new Bid(auctionID);
//					Quote T = new Quote(TACAgent.getAuctionFor(TACAgent.CAT_HOTEL, TACAgent.TYPE_CHEAP_HOTEL, j));
//					float bidprice = T.getBidPrice();
//					float aprice = T.getAskPrice();
//					if (aprice > bidprice)
//					{
//						B.addBidPoint(1, aprice);
//						masterAgent.agent.submitBid(B);
//					}
//				}
//			}
//			// Check the price customer is ready to pay
//			float askprice = 0, total = 0;
//			for (int cust = 0; cust < 8 ;cust++)
//			{
//				for (int night = client.arrivalDay; night < client.departureDay; night++ )
//				{
//					Quote Q = new Quote(TACAgent.getAuctionFor(TACAgent.CAT_HOTEL, TACAgent.TYPE_GOOD_HOTEL, night));
//					askprice = Q.getAskPrice();
//					total = total + askprice;
//				}
//				System.out.println(total);
//			}
//			
//			int auctionID = 0;
//			if (total < 500)
//			{
//				auctionID = TACAgent.getAuctionCategory(TACAgent.TYPE_CHEAP_HOTEL);
//			}
//			else
//			{
//				auctionID = TACAgent.getAuctionCategory(TACAgent.TYPE_GOOD_HOTEL);
//			}
//			if (auctionID == TACAgent.getAuctionCategory(TACAgent.TYPE_CHEAP_HOTEL))
//			{
//				
//			}
//		}
//		System.out.println("HOTEL SUB AGENT finihed INTITIALISE");
	}

	
	@Override
	public void run()
	{
		while (true)
		{
			// TODO: Write agent logic
		}
	}
}

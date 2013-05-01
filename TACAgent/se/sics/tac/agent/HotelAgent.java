package se.sics.tac.agent;

import se.sics.tac.aw.Bid;
import se.sics.tac.aw.Quote;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.datastructures.Client;
import se.sics.tac.datastructures.Client.hotelType;
//import se.sics.tac.datastructures.Client.itemStatus;

public  class HotelAgent extends SubAgent 
{
	Bid[] expensiveHotelBids = new Bid[5];
	Bid[] cheapHotelBids = new Bid[5];
	static int HOTEL_BUDGET = 200;

	@Override
	public void initialise() 
	{	
		
		
		// Initialize the bid arrays
		for (int i = 1 ; i <= 4; i++)
		{
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
			if (hotelval > 70)
			{
				client.hotelAssignment = hotelType.expensiveHotel;
				System.out.println("Expensive hotel requested by client"+ i);
			}
			else
			{
				client.hotelAssignment = hotelType.cheapHotel;
				System.out.println("Cheap hotel requested by client"+ i);
			}	
						
			// Assign Hotel for each night
			for (int j = client.arrivalDay; j < client.departureDay; j++)
			{
				//int auctionID;
				if (client.hotelAssignment == hotelType.expensiveHotel)
				{
					//auctionID = TACAgent.getAuctionFor(TACAgent.CAT_HOTEL, TACAgent.TYPE_GOOD_HOTEL, j);
					// calculate the price per room for this customer (add the hotel bonus if booking expensive hotel)
					float budgetPerNight = (HOTEL_BUDGET + client.hotelvalue) / (client.departureDay - client.arrivalDay);
					expensiveHotelBids[j].addBidPoint(1, budgetPerNight);
					System.out.println("Submitted bid for expensive hotel on day " + j);
				}
				else 
				{
					//auctionID = TACAgent.getAuctionFor(TACAgent.CAT_HOTEL, TACAgent.TYPE_CHEAP_HOTEL, j);
					// calculate the price per room for this customer
					float budgetPerNight = HOTEL_BUDGET / (client.departureDay - client.arrivalDay);
					cheapHotelBids[j].addBidPoint(1, budgetPerNight);
					System.out.println("Submitted bid for cheap hotel on day " + j);
				}
			}
		}
		
		// Submit the completed bids
		for (int i = 1 ; i <= 4; i++)
		{
			masterAgent.agent.submitBid(expensiveHotelBids[i]);
			masterAgent.agent.submitBid(cheapHotelBids[i]);
//			System.out.println(expensiveHotelBids[i].getQuantity());
//			System.out.println(cheapHotelBids[i].getQuantity());
		}
		
		//Allocation
//		for (int i = 0; i < 5; i++)
//		{
//			int alloc = masterAgent.agent.getAllocation(i) - masterAgent.agent.getOwn(i);
//			System.out.println("alloc"+ alloc);
//		}
//		
//		System.out.println("I am There");
//		// Check for number of rooms about to win
//		Quote[] CH = new Quote[90];
//		Quote[] EH = new Quote[90];
//		int[] hqCheaphotel = new int[4];
//		int[] hqGoodhotel = new int[4];
//		System.out.println("I am here");
//		for (int day = 1; day <5; day++)
//		{	
//			CH[day] = new Quote(TACAgent.getAuctionFor(TACAgent.CAT_HOTEL, TACAgent.TYPE_CHEAP_HOTEL, day));
//			hqCheaphotel[day] = CH[day].getHQW();
//			System.out.println("Number of cheapHotels about to win : "+ hqCheaphotel[day] +"on day"+ day);
//			EH[day] = new Quote(TACAgent.getAuctionFor(TACAgent.CAT_HOTEL, TACAgent.TYPE_GOOD_HOTEL, day));
//			hqGoodhotel[day] = EH[day].getHQW();
//			System.out.println("Number of cheapHotels about to win : "+ hqGoodhotel[day] +"on day"+ day );
//		}
//		int day = 1;
//		System.out.println("I am somewhere");
//		while (day < 5)
//		{
//			int auctionID = TACAgent.getAuctionFor(TACAgent.CAT_HOTEL, TACAgent.TYPE_CHEAP_HOTEL, day);
//			Bid B = new Bid(auctionID);
//			while(CH[day].getHQW() < hqCheaphotel[day])
//			{
//				System.out.println("increase the bid for cheap hotels");
//				float bidprice = CH[day].getBidPrice();
//				B.addBidPoint(1, bidprice + 10);
//				masterAgent.agent.submitBid(B);
//			}
//			while (EH[day].getHQW() < hqGoodhotel[day])
//			{
//				System.out.println("increase the bid for good hotels");
//				float bidprice = EH[day].getBidPrice();
//				B.addBidPoint(1, bidprice + 10);
//				masterAgent.agent.submitBid(B);
//			}
//		}
	}
//					int auctionID = TACAgent.getAuctionFor(TACAgent.CAT_HOTEL, TACAgent.TYPE_GOOD_HOTEL, j);
	
//					Quote T = new Quote(TACAgent.getAuctionFor(TACAgent.CAT_HOTEL, TACAgent.TYPE_GOOD_HOTEL, j));
//					float bidprice = T.getBidPrice();
//					float aprice = T.getAskPrice();
//					System.out.println("bidprice is :"+ bidprice);
//					System.out.println("askPrice is :"+ aprice);
//					if (bidprice +1 < aprice)
//					{
//						B.addBidPoint(1, aprice);
//						masterAgent.agent.submitBid(B);
//					}
//				}
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

	
	@Override
	public void run()
	{
		while (true)
		{
			// TODO: Write agent logic
		}
	}
}

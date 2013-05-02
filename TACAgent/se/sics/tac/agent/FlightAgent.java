package se.sics.tac.agent;
import java.util.*;
import se.sics.tac.aw.Quote;
import se.sics.tac.aw.Bid;
import se.sics.tac.datastructures.*;
import se.sics.tac.aw.*;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;

import javax.swing.Timer;

public class FlightAgent extends SubAgent {
	Semaphore timingSemaphore;
	int FREQUENCY = 10; // How often to repeat the main loop in seconds
	
float[] price= new float [50];
//	float bidPrice = 0.2f;	
	

		
		
	@Override
	public void initialise() {
		
		for (int custNo = 0 ; custNo < 8; custNo++){
			int arrivalDay = masterAgent.agent.getClientPreference(custNo, TACAgent.ARRIVAL);
			int departureDay = masterAgent.agent.getClientPreference(custNo, TACAgent.DEPARTURE);
			
			int arrivalAuctionNo = TACAgent.getAuctionFor(TACAgent.CAT_FLIGHT, TACAgent.TYPE_INFLIGHT, arrivalDay);
			int departureAuctionNo = TACAgent.getAuctionFor(TACAgent.CAT_FLIGHT, TACAgent.TYPE_OUTFLIGHT, departureDay);
			
			masterAgent.agent.setAllocation(arrivalAuctionNo, masterAgent.agent.getAllocation(arrivalAuctionNo)+1);
			masterAgent.agent.setAllocation(departureAuctionNo, masterAgent.agent.getAllocation(departureAuctionNo)+1);
		}
		
		for (int day = 1; day < 5; day++){
			int inFlightAuctionNo = TACAgent.getAuctionFor(TACAgent.CAT_FLIGHT, TACAgent.TYPE_INFLIGHT, day);
			int numberOfTickets = masterAgent.agent.getAllocation(inFlightAuctionNo);
			int ownTickets= masterAgent.agent.getOwn(inFlightAuctionNo);
			
		//	Quote quote = new Quote(inFlightAuctionNo);
		//	float currentPrice = quote.getAskPrice();
			
			Bid bid  = new Bid(inFlightAuctionNo);
			 if((numberOfTickets-ownTickets)>0)
			    {
				// bid.addBidPoint((numberOfTickets-ownTickets), currentPrice + 50);
				 bid.addBidPoint((numberOfTickets-ownTickets), 300);
			    }
			//bid.addBidPoint(numberOfTickets, currentPrice + 50);
			
			masterAgent.agent.submitBid(bid);
			System.out.println("Bidded for inflight on day " + day + " " + numberOfTickets + " times");
		}
		for (int day = 2; day <= 5; day++){
			int outFlightAuctionNo = TACAgent.getAuctionFor(TACAgent.CAT_FLIGHT, TACAgent.TYPE_OUTFLIGHT, day);
			int numberOfTickets = masterAgent.agent.getAllocation(outFlightAuctionNo);
		    int ownTickets= masterAgent.agent.getOwn(outFlightAuctionNo);
		    
		 //   Quote quote = new Quote(outFlightAuctionNo);
		//	float currentPrice = quote.getAskPrice();
			Bid bid  = new Bid(outFlightAuctionNo);
		    if((numberOfTickets-ownTickets)>0)
		    {
				//bid.addBidPoint((numberOfTickets-ownTickets), currentPrice + 50);
		    	bid.addBidPoint((numberOfTickets-ownTickets), 300);
		    }
			masterAgent.agent.submitBid(bid);
			System.out.println("Bidded for outflight on day " + day + " " + numberOfTickets + " times");
		}
	
	} // initialise() end
	

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
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		for (int day=1; day<=4; day++)
		{
			int inFlightAuctionNo = TACAgent.getAuctionFor(TACAgent.CAT_FLIGHT, TACAgent.TYPE_INFLIGHT, day);
			int numberOfTicketsIn = masterAgent.agent.getAllocation(inFlightAuctionNo);
			int ownTicketsIn= masterAgent.agent.getOwn(inFlightAuctionNo);
			int ticketsReqIn = numberOfTicketsIn - ownTicketsIn;
		//	masterAgent.agent.getQuote(inFlightAuctionNo);
			Quote quote = masterAgent.agent.getQuote(inFlightAuctionNo);
			
			float currentPrice = quote.getAskPrice();
			
		//	float lastBidPrice = quote.getBidPrice();
			if(ticketsReqIn >0)
			{
				Bid b = new Bid(inFlightAuctionNo);
				
			////	if ((lastBidPrice-currentPrice)<0)
			//	{
					price[inFlightAuctionNo] = currentPrice+5;
				
			//	}
			//	else if ((lastBidPrice-currentPrice)>0) 
			//	{
			//		price[inFlightAuctionNo] = currentPrice-5;
				
			//	}
			//	else
			//	{price[inFlightAuctionNo] = currentPrice;}
				
				b.addBidPoint(ticketsReqIn, price[inFlightAuctionNo]);
			//	masterAgent.agent.submitBid(b);
				if (quote.getBid() == null){
					System.out.println("NULL BID");
				}
				masterAgent.agent.replaceBid(quote.getBid(), b);
				String time = masterAgent.agent.getGameTimeLeftAsString();
				
				if (Integer.parseInt(time) <= 60)
				{
					b.addBidPoint(ticketsReqIn, currentPrice);
					masterAgent.agent.submitBid(b);
				}
				else
				{
					masterAgent.agent.replaceBid(quote.getBid(), b);
				}
			}
		}
		for (int day=2; day<=5; day++)
		{
			int outFlightAuctionNo = TACAgent.getAuctionFor(TACAgent.CAT_FLIGHT, TACAgent.TYPE_OUTFLIGHT, day);
			int numberOfTicketsOut = masterAgent.agent.getAllocation(outFlightAuctionNo);
		    int ownTicketsOut= masterAgent.agent.getOwn(outFlightAuctionNo);
		    int ticketsReqOut = numberOfTicketsOut - ownTicketsOut;
		    Quote quote = masterAgent.agent.getQuote(outFlightAuctionNo);
			float currentPrice = quote.getAskPrice();
			
			float lastBidPrice = quote.getBidPrice();
			if(ticketsReqOut >0)
			{
				Bid b = new Bid(outFlightAuctionNo);
				if ((lastBidPrice-currentPrice)<0)
				{
					price[outFlightAuctionNo] = currentPrice+5;
				}
				else if ((lastBidPrice-currentPrice)>0) 
				{
					price[outFlightAuctionNo] = currentPrice-5;
				}
				else {price[outFlightAuctionNo] = currentPrice;}
				b.addBidPoint(ticketsReqOut, price[outFlightAuctionNo]);
			//	masterAgent.agent.submitBid(b);
			masterAgent.agent.replaceBid(quote.getBid(), b);
			}
			
		}
		
	}// while() 
	}// run() 
	
}// FlightAgent() end

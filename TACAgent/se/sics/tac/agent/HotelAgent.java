package se.sics.tac.agent;


import java.util.concurrent.Semaphore;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;

public class HotelAgent extends SubAgent {
	Semaphore timingSemaphore;
	int FREQUENCY = 1; // How often to repeat the main loop in seconds

	@Override
	public void initialise() {
		
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
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
			// Program logic here
			
			
		}
	}
}

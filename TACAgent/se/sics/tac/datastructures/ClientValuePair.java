package se.sics.tac.datastructures;

// A datatype to store a customer and value in a pair. This class will sort by value
public class ClientValuePair implements Comparable<ClientValuePair>{
	public Client client;
	public float value;
	
	public ClientValuePair(Client client, float value) {
		this.client = client;
		this.value = value;
	}

	@Override
	public int compareTo(ClientValuePair o) {
		if (this.value < o.value) {
			return -1;
		} else if (this.value > o.value) {
			return 1;
		} else {
			return 0;
		}
	}
}

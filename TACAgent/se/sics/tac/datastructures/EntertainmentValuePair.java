package se.sics.tac.datastructures;

//A datatype to store a customer and value in a pair. This class will sort by value
public class EntertainmentValuePair implements Comparable<EntertainmentValuePair>{
	public EntertainmentTypes type;
	public float value;
	
	public EntertainmentValuePair(EntertainmentTypes type, float value) {
		this.type = type;
		this.value = value;
	}

	@Override
	public int compareTo(EntertainmentValuePair o) {
		if (this.value < o.value) {
			return -1;
		} else if (this.value > o.value) {
			return 1;
		} else {
			return 0;
		}
	}
}
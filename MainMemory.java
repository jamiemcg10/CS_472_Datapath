
public class MainMemory {
	private int mainMemory[] = new int[2048];  // declare array to hold values in main memory
	
	public MainMemory() {
		
		int j = 0;
		for (int i=0;i<mainMemory.length;i++) {  // loop through positions in array and assign values of 0x0-0xff cyclically
				mainMemory[i] = j;
				j++;
				
				if (j > 0xFF) { // reset assigned value to 0 when value to write is > 0xff 
					j = 0;
				} // end if (j > 0xFF)
				
		} // end for (int i=0;i<mainMemory.length;i++)
		
	} // end MainMemory()
	
	public int getFrom(int location) {
		//System.out.println("Location: " + Integer.toHexString(location) + " Value: " + Integer.toHexString(mainMemory[location]));
		return mainMemory[location];
	}
	
	public void writeTo(int location, int value) {
		mainMemory[location] = value; 
	}
	
	public String toString() {
		
		String mm = "";
		
		for (Integer i=0;i<mainMemory.length;i++) {
			mm += "MM[" + Integer.toHexString(i) +"]: " + Integer.toHexString(mainMemory[i]) + "\n";
			
		}
		
		return mm;
		
	}
	
}

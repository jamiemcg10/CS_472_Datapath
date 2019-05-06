
public class Driver {

	public static void main(String[] args) {
		Driver Driver = new Driver();
		Driver.drive();

	}

	public void drive() {
		//int program[] = {0xa1020000, 0x810AFFFC, 0x00831820, 0x01263820, 0x01224820, 0x81180000, 0x81510010, 0x00624022, 0x00000000, 0x00000000, 0x00000000, 0x00000000};
		int program[] = {0x032BA020, 0x00000000, 0x00000000, 0x00000000};


	Datapath datapath = new Datapath();
	
	int i = 1;
	for(int instruction:program) {
			System.out.println("\n\n\n\nCLOCK CYCLE " + i + "\n_____________");
			datapath.clockCycle(instruction);
			i++;
	}
	
	}  // end drive()
	
}  // end Driver class

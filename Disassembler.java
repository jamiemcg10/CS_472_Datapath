// Jamie Smart
// CS 472 Project 1
// 10-1-2018

public class Disassembler {
	
	//public static int PC = 0x9A040; // initialize program counter at initial address
	public static int PC = 0; // initialize program counter at initial index
	
//	public static void main(String[] args) {
//		
//		Disassembler me = new Disassembler();
//		me.Driver();
//		
//	}
//	
	public static RIFormat disassemble(int instruc) {
		RIFormat instruction = new RIFormat(instruc); 	
		return instruction;
	}
	
	

//		
//	public void Driver () {
//		int[] codedInstructions = {0x032BA020, 0x8CE90014, 0x12A90003, 0x022DA822, 0xADB30020, 0x02697824, 0xAE8FFFF4, 
//				0x018C6020, 0x02A4A825, 0x158FFFF7, 0x8ECDFFF0};  // create array of coded instructions
//
//		for (int instruc : codedInstructions) { // loop through coded instructions
//				RIFormat instruction = new RIFormat(instruc); 
//				System.out.println(instruction); // print the decoded instruction
//				changeAddress(this); // increment the PC
//				
//			} 
//
//		}
	
	public void changeAddress(Disassembler disassembler) {
		// method to increment the disassembler's program counter
		disassembler.PC += 1;
	}
	
	
}


class RIFormat {
	
	private int address;  // variable to hold the PC counter location
	private int opcode;  // variable to hold the opcode that is passed in from the Disassembler class
	private int codedInstruction;
	private String decodedInstruction;
	private int src1;   // variable to hold the 1st source register
	private short offset;  // variable to hold the offset
	private int src2;   // variable to hold the 2nd source or destination register
	private int dest;   // variable to hold the destination register
	private int func;   
	
	
	int opcodeMask = 0xFc000000; 
	private final int src1Mask = 0x03E00000;
	private final int offsetMask = 0x0000FFFF;
	private final int src2Mask = 0x001F0000;
	private final int destMask = 0x0000F800;
	private final int funcMask = 0x0000003F;
	
	
	public RIFormat(int codedInstruction) {  // PCadd removed from constructor
	
		this.codedInstruction= codedInstruction;
		//address = PCadd + 1;  // increments the PC counter for use in the instruction object only 
		opcode = determineOPCODE (codedInstruction);
		src1 = (codedInstruction & src1Mask) >>> 21;
		src2 = (codedInstruction & src2Mask) >>> 16;
		offset = (short) (codedInstruction & offsetMask); // does not need shifting because is already in least significant bits
		dest = (codedInstruction & destMask) >>> 11;
		func = (codedInstruction & funcMask);  // does not need shifting because is already in least significant bits
		decodedInstruction = decodeInstruction();
	
	}
	
	public int getOpcode() {
		return opcode;
	}

	public String getDecodedInstruction() {
		return decodedInstruction;
	}
	
	public int getCodedInstruction() {
		return codedInstruction;
	}
	
	public int getSrc1() {
		return src1;
	}


	public short getOffset() {
		return offset;
	}

	public int getSrc2() {
		return src2;
	}

	public int getDest() {
		return dest;
	}

	public int getFunc() {
		return func;
	}



	public int determineOPCODE (int hexCode){
		int opcodeUnshifted = hexCode & opcodeMask;
		//System.out.println("unshifted opcode: " + Integer.toBinaryString(opcodeUnshifted));
		
		int opcodeShifted = opcodeUnshifted >>> 26;
		
		//System.out.println("shifted opcode: " + Integer.toBinaryString(opcodeShifted));
		return opcodeShifted;
		
		
	}
	
	public String determineIOperation(int opcodeCode) {
		
		String returnString = "";
		int branchAddress;  // declare variable to hold the branch address for any bne or beq instructions
		
		switch (opcodeCode) {
			case 0b100011: // lw
				returnString = "lw $" + src2 + ", " + offset + "($" + src1 + ")";
				break;
			case 0b101011: // sw
				returnString = "sw $" + src2 + ", " + offset + "($" + src1 + ")";
				break;
			case 0x20: // lb
				returnString = "lb $" + src2 + ", " + offset + "($" + src1 + ")";
				break;
			case 0x28: // sb
				returnString = "sb $" + src2 + ", " + offset + "($" + src1 + ")";
				break;				
			case 0b000100: // beq
				branchAddress = address + (offset << 2);  // set branch address
				returnString = "beq $" + src1 + ", $" + src2 + ", address " + Integer.toHexString(branchAddress);
				break;
			case 0b000101: // bne
				branchAddress = address + (offset << 2);  // set branch address
				returnString = "bne $" + src1 + ", $" + src2 + ", address " + Integer.toHexString(branchAddress);
				break;
		
		}
		
		return returnString;
		
	}
	
	public String determineROperation(int funcCode) {
		String returnString = "";
		
		switch (funcCode) {
			case 0b000000: // nop
				returnString = "nop";
				break;
			case 0b100000: // add
				returnString = "add $" + dest + ", $" + src1 + ", $" + src2;
				break;
			case 0b100010: // sub
				returnString = "sub $" + dest + ", $" + src1 + ", $" + src2;
				break;
			case 0b100100: // and
				returnString = "and $" + dest + ", $" + src1 + ", $" + src2;
				break;
			case 0b100101: // or
				returnString = "or $" + dest + ", $" + src1 + ", $" + src2;
				break;
			case 0b101010: // slt
				returnString = "slt $" + dest + ", $" + src1 + ", $" + src2;
				break;
		}
		
		return returnString;
		
	}

	public String decodeInstruction() {
		if (opcode == 0) {
			return determineROperation(func);
		} else {
			return determineIOperation(opcode);
		}
	}
	

	public String toString() {
		
		if (opcode == 0) {
			return determineROperation(func);
		} else {
			return determineIOperation(opcode);
		}
	}
	
}

















public class Datapath{
	private IF_ID_Register IF_ID_Read;
	private IF_ID_Register IF_ID_Write;
	private ID_EX_Register ID_EX_Read;
	private ID_EX_Register ID_EX_Write;
	private EX_MEM_Register EX_MEM_Read;
	private EX_MEM_Register EX_MEM_Write;
	private MEM_WB_Register MEM_WB_Read;
	private MEM_WB_Register MEM_WB_Write;
	private int Main_Mem[] = new int[2048];  // declare array to hold values in main memory
	private int Regs[] = new int[32];  // declare array to hold values in main memory



	public Datapath () {

		IF_ID_Read = new IF_ID_Register();
		IF_ID_Write = new IF_ID_Register();
		ID_EX_Read = new ID_EX_Register();
		ID_EX_Write = new ID_EX_Register();
		EX_MEM_Read = new EX_MEM_Register();
		EX_MEM_Write = new EX_MEM_Register();
		MEM_WB_Read = new MEM_WB_Register();
		MEM_WB_Write = new MEM_WB_Register();
	}

	
	{

		int j = 0;
		for (int i=0;i<Main_Mem.length;i++) {  // loop through positions in array and assign values of 0x0-0xff cyclically
				Main_Mem[i] = j;
				j++;
				
				if (j > 0xFF) { // reset assigned value to 0 when value to write is > 0xff 
					j = 0;
				} // end if (j > 0xFF)
				
		} // end for (int i=0;i<mainMemory.length;i++)
		
	

	Regs[0] = 0;
	int k = 0x101;
	for (int l=1;l<Regs.length;l++) {  // loop through positions in array and assign values of 0x0-0xff cyclically
		Regs[l] = k;
		k++;		

	} // end for (int i=0;i<mainMemory.length;i++)
	
	}
	
	public void clockCycle(int newInstruction) {
		System.out.println("beginning of cycle");
		Print_out_everything();
		IF_Stage(newInstruction);
		System.out.println("after IF stage");
		Print_out_everything();
		ID_Stage();
		System.out.println("after ID stage");
		Print_out_everything();
		EX_Stage();
		//MEM_Stage();
		Copy_write_to_read();
		System.out.println("end of cycle");
		Print_out_everything();
		
	}
	
	
	public void Copy_write_to_read() {
		try {
		IF_ID_Read = IF_ID_Write.clone();
		ID_EX_Read = ID_EX_Write.clone();
		EX_MEM_Read = EX_MEM_Write.clone();
		//MEM_WB_Read = MEM_WB_Write.clone();	
		}
		catch (CloneNotSupportedException e) {
			;
		}
	}  // end Copy_write_to_read()
	
	
	public void Print_out_everything() {
		System.out.println("\nREGISTERS\nIF/ID Write:");
		System.out.println(IF_ID_Write);
		System.out.println("\nIF/ID Read:");
		System.out.println(IF_ID_Read);
		System.out.println("\nID/EX Write:");
		System.out.println(ID_EX_Write);
		System.out.println("\nID/EX Read:");
		System.out.println(ID_EX_Read);
		System.out.println("\nEX/MEM Write:");
		System.out.println(EX_MEM_Write);
		System.out.println("\nEX/MEM Read:");
		System.out.println(EX_MEM_Read);
//		System.out.println("\nMEM/WB Write:");
//		System.out.println(MEM_WB_Write);
//		System.out.println("\nMEM/WB Read:");
//		System.out.println(MEM_WB_Read);
	}

	
	
	public void IF_Stage(int instruction) {
		IF_ID_Write.setInstruction(instruction);
		
	}
	
	private class IF_ID_Register implements Cloneable {
		private int instruction;
		
		public void setInstruction (int instruction) {
			this.instruction = instruction;
		}
		
		public int getInstruction() {
			return instruction;
		}
		
		public IF_ID_Register clone() throws CloneNotSupportedException{
			try {
				return (IF_ID_Register) super.clone();
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}
		
		public String toString() {
			return "Instruction: " + Integer.toHexString(instruction); 
		}
		
	}
	
	
	public void ID_Stage() {
		RIFormat instruction = Disassembler.disassemble(IF_ID_Read.getInstruction());
		
		
			ID_EX_Write.setPipelineInstruction(instruction);
			ID_EX_Write.setInstructionText(instruction);
			ID_EX_Write.setWriteReg_20_16(instruction.getSrc1());
			ID_EX_Write.setWriteReg_15_11(instruction.getSrc2());
			ID_EX_Write.setSEOffset(instruction.getOffset());
			ID_EX_Write.setReadReg1Value(Regs[ID_EX_Write.getWriteReg_20_16()]);
			ID_EX_Write.setReadReg2Value(Regs[ID_EX_Write.getWriteReg_15_11()]);
			ID_EX_Write.setFunction(instruction.getFunc());

			// set control signals
			ID_EX_Write.control.setALUOp(instruction.getOpcode());
			ID_EX_Write.control.setALUSrc();
			ID_EX_Write.control.setRegDst();
			ID_EX_Write.control.isValid(instruction.getCodedInstruction());

		
	}
	
	private class ID_EX_Register implements Cloneable {

		private RIFormat pipelineInstruction;
		private String instructionText;
		private int ReadReg1Value;
		private int ReadReg2Value;
		private int SEOffset;
		private int WriteReg_20_16;
		private int WriteReg_15_11;
		private int Function;
		private ControlSignals control = new ControlSignals();
		
		public ID_EX_Register() {
			pipelineInstruction = new RIFormat(0x00000000);
			setInstructionText(pipelineInstruction);
		}
		
		public void setPipelineInstruction(RIFormat instruction) { this.pipelineInstruction = instruction;}
		public void setInstructionText(RIFormat instruction) { this.instructionText = instruction.getDecodedInstruction();}
		public String getInstructionText() {return this.instructionText;}
		public int getReadReg1Value() { return ReadReg1Value;}
		public void setReadReg1Value(int readReg1Value) { ReadReg1Value = readReg1Value;}
		public int getReadReg2Value() { return ReadReg2Value;}
		public void setReadReg2Value(int readReg2Value) { ReadReg2Value = readReg2Value;}
		public int getSEOffset() { return SEOffset;}
		public void setSEOffset(int sEOffset) {	SEOffset = sEOffset;}
		public int getWriteReg_20_16() { return WriteReg_20_16;}
		public void setWriteReg_20_16(int writeReg_20_16) {	WriteReg_20_16 = writeReg_20_16;}
		public int getWriteReg_15_11() {return WriteReg_15_11;}
		public void setWriteReg_15_11(int writeReg_15_11) {	WriteReg_15_11 = writeReg_15_11;}
		public int getFunction() {return Function;}
		public void setFunction(int function) { Function = function;}
		
		public ID_EX_Register clone() {
			try {
				ID_EX_Register clone = (ID_EX_Register) super.clone();
				clone.control = control.clone();
				return clone;
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}
		
		public String toString() {
			return "Instruction: " + Integer.toHexString(pipelineInstruction.getCodedInstruction()) + 
					"\nDecoded instruction: " + instructionText + 
					"\nReg 1: "	+ WriteReg_20_16 + 
					"\nReg 1 Value: " + Integer.toHexString(ReadReg1Value) + 
					"\nReg 2: " + WriteReg_15_11 + 
					"\nReg 2 Value: " + Integer.toHexString(ReadReg2Value) + 
					"\nFunction code: " +  Integer.toHexString(Function) + 
					"\nSEOffset: " + Integer.toHexString(SEOffset) + 
					"\nControl Signals: " + control; 
		}
		
		
		
	}  // end private ID_EX Register class
	
	
	public void EX_Stage() {
		EX_MEM_Write.setControlSignals(ID_EX_Read.control);
		EX_MEM_Write.setinstructionText(ID_EX_Read);
		switch (ID_EX_Read.control.getALUOp()) {
		case 0x00: // RType
			System.out.println("Why is RegWrite true????" + Integer.toHexString(ID_EX_Read.getFunction()));
			switch (ID_EX_Read.getFunction()) {
				case 0x20: // add
					EX_MEM_Write.setALUResult(ID_EX_Read.getReadReg1Value() + ID_EX_Read.getReadReg2Value());
					EX_MEM_Write.control.setRegWrite();
					break; 
				case 0x22: // sub
					EX_MEM_Write.setALUResult(ID_EX_Read.getReadReg1Value() - ID_EX_Read.getReadReg2Value());
					EX_MEM_Write.control.setRegWrite();
					break;
				case 0x00:
					break;
			}
		case 0x20: // lb
			EX_MEM_Write.setALUResult(ID_EX_Read.getReadReg1Value() + ID_EX_Read.getSEOffset());
			EX_MEM_Write.control.setRegWrite();
			break;
		case 0x28: // sb
			EX_MEM_Write.setALUResult(ID_EX_Read.getReadReg1Value() + ID_EX_Read.getSEOffset());
			break;
		default:
			break;
		}
		
		
	}
	

	private class EX_MEM_Register implements Cloneable {
		private String instructionText;
		private int ALUResult;
		private int SBValue;
		private int WriteRegNum;
		private ControlSignals control = new ControlSignals();
		
		public EX_MEM_Register () {
			this.instructionText = "nop";
		}
		
		public void setALUResult(int result){
			this.ALUResult = result;
		}
		
		public void setinstructionText(ID_EX_Register reg){
			this.instructionText = reg.getInstructionText();
		}
		
		public void setControlSignals(ControlSignals controlFromRegister) {
			control = controlFromRegister.clone();
		}
		
		public int getALUResult() {
			return ALUResult;
		}
		
		public EX_MEM_Register clone() {
			try {
				EX_MEM_Register clone = (EX_MEM_Register) super.clone();
				clone.control = control.clone();
				return clone;
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}  // end clone
		
		public String toString() {
			return "\nInstruction text: " + instructionText +
					"\nALU Result: " + Integer.toHexString(ALUResult) + 
					"\nSB Value: " + SBValue +
					"\nWrite Reg Num: " + WriteRegNum + 
					"\nControl Signals: " + control; 
		}
		
	}
	
	
	public void MEM_Stage() {
		//System.out.println("other test: " + MEM_WB_Write);
		//MEM_WB_Write.setControlSignals(EX_MEM_Read.control);
		//MEM_WB_Write.control = 
		//System.out.println("!!!!!!!!!!!!!!!!!!EX/MEM Read (test): " + EX_MEM_Read);
		//if (EX_MEM_Read.control.getMemRead()) {
			//load byte from memory
			//MEM_WB_Write.setLBDataValue(Main_Mem[EX_MEM_Read.getALUResult()]);
			//MEM_WB_Write.setLBDataValue(Main_Mem[EX_MEM_Read.getALUResult()]);
		//}
	}
	
	
	private class MEM_WB_Register implements Cloneable{
		private int LBDataValue;
		private int ALUResult;
		private int WriteRegNum;
		private ControlSignals control;
		
		public void setLBDataValue(int value) {
			LBDataValue = value;
		}
		
		public void setControlSignals(ControlSignals controlFromRegister) {
			control = controlFromRegister.clone();
		}
		
		public MEM_WB_Register clone() {
			try {
				MEM_WB_Register clone = (MEM_WB_Register) super.clone();
				//clone.control = control.clone();
				return clone;
			} catch (CloneNotSupportedException e) {
				return null;
			}
		} // end clone()
		
		public String toString() {
			return "LB Data Value: " + LBDataValue + 
					"\nALU Result: " + ALUResult + 
					"\nWrite Reg Nul: " + WriteRegNum + 
					"\nControl Signals: " + control;
		}
	}

	
	public void WB_stage() {}
	
	
	class ControlSignals implements Cloneable{
		private boolean RegDst;
		private boolean ALUSrc;
		private int ALUOp;
		private boolean MemRead;
		private boolean MemWrite;
		private boolean MemToReg;
		private boolean RegWrite;
		private boolean valid;
		
		
		public boolean isValid(int instruction) {
			if (instruction == 0) {
				valid = false;
				return valid;
			}
			
			valid = true;
			return valid;
		}
		public boolean getRegDst() {	return RegDst;}
		public void setRegDst() { 
			if (ALUOp == 0) {
				RegDst = true;
				}
			}
		public boolean getALUSrc() {	return ALUSrc;}
		public void setALUSrc() { 
			if (ALUOp > 0){
				ALUSrc = true;}
			}
		public int getALUOp() {	return ALUOp;}
		public void setALUOp(int ALUOp) {this.ALUOp = ALUOp;}
		public boolean getMemRead() { return MemRead;}
		public void setMemRead() { MemRead = true;}
		public boolean getMemWrite() { return MemWrite;}
		public void setMemWrite() {	MemWrite = true;}
		public boolean getMemToReg() {return MemToReg;}
		public void setMemToReg() {	MemToReg = true;}
		public boolean getRegWrite() { return RegWrite;}
		public void setRegWrite() {	RegWrite = true;}

		public ControlSignals clone(){
			try {
				return (ControlSignals) super.clone();
			} catch (CloneNotSupportedException e) {
				return null;
			}
		}

		public String toString() {
			return 	"\n\tRegDst: " + RegDst + 
					"\n\tALUSrc: " + ALUSrc + 
					"\n\tALUOp: " + ALUOp + 
					"\n\tMemRead: " + MemRead + 
					"\n\tMemWrite: " + MemWrite + 
					"\n\tMemToReg: " + MemToReg + 
					"\n\tRegWrite: " + RegWrite + 
					"\n\tValid: " + valid;
		}
	}
	
	
} // end Datapath class

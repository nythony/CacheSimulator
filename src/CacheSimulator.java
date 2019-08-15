/* Name: Alina Chaiyasarikul
 * Date: July 9th, 2019
 * 
 * Cache Simulator:
 * This is a software simulation of a cache memory subsystem. 
 * This program uses an array of data to act as the memory, and an array of slot object to act as the cache.
 * 
 * Direct-Mapped, Write-Back Cache
 * Block size: 16 bytes (16 addresses)
 * Number of slots: 16
 * Block offset: 4 bits = 1 byte
 */

import java.util.*;

public class CacheSimulator {

	public static void main(String[] args) {
		CacheSimulator sim = new CacheSimulator();
		sim.run();
	}
	
	
	public void run() {
		
		Scanner kb = new Scanner(System.in);
		
		
		//Instantiating the cache as an array of slots
		Slot[] cache = new Slot[16];
		
		for (int i = 0; i < cache.length; i++) {
			cache[i] = new Slot();
		}
	
		//Instantiating the main memory
		
		short[] ram = new short[2048];
		
		int value = 0;
		
		for (int i = 0; i < ram.length; i++) {
			ram[i] = (short)value;
			value++;
			if (value > 255) {
				value = 0;
			}
		}

		
		//Read, Write, Display
		int input = 0;
		
		do{
			
			System.out.println("\nPlease choose the number corresponding to your choice. Press any other key to exit: ");
			System.out.println("1. Read\n2. Write\n3. Display\n");
			System.out.print("Choice: ");
			
			String choice = kb.next();
			
			try{
				//Choice is a number
				input = Integer.parseInt(choice);
			}catch (NumberFormatException ex) {
				//Choice is not a number, exit
				break;
			}
			
			switch (input) {
				
			case 1: //READ
			
				//Search for address
				short addressR = validInputAddress();
				System.out.printf("%s%X%s", "Value at ", addressR, " is: ");
				System.out.printf("%X%n", read(cache, ram, addressR));
				
				break;
				
			case 2: //Writing to an address
				
				short addressW = validInputAddress();
				
				System.out.print("Enter the data: ");
				String dataInput = kb.next();
				
				while (!validateHex(dataInput) || Integer.parseInt(dataInput, 16) > 0xFF) {
					System.out.printf("%s%X%s", "Error. Please enter a hexadecimal number under ", 0x100, ": ");
					dataInput = kb.next();
				}
				
				short data = (short)(Integer.parseInt(dataInput, 16));
				
				System.out.printf("%s%X%s", "Value at ", addressW, " is now: ");
				System.out.printf("%X%n", write(cache, ram, addressW, data));
				
				break;
				
			case 3: //Prints out cache
				
				display(cache);
				
				break;
				
			default: //Exit loop
				
				input = 0;
				
				break;
			}
		
		}while(input != 0);
		
		kb.close();
	
	}

	//METHODS:
	
	//Returns the data value at a particular memory address. Indicates a cache hit or miss for particular address:
	public int read(Slot[] c, short[] m, short address) {
		//Breaking the address down to its pieces
		short[] a = parse(address);
		short tag 			= a[0];
		short slotNum 		= a[1];
		short offset 		= a[2];
		
		Slot slot = c[slotNum];

		
		//If it is not in the cache:
		if (slot.isValid() == 0 || tag != slot.getTag()) {
			System.out.print("(Cache Miss) ");
			
			//Writing back to memory due to data value changes:
			//Precaution, dirty bit would only be true if already valid since the change is made in a cache with valid data.
			if((slot.isValid() == 1) && (slot.isDirtyBit() == 1)) {
				//Values in the block
				short[] values = slot.getBlock();
				
				//Reassembling address location
				short oldBlockStart = (short) ((slot.getTag() << 8) + (slotNum << 4));
			
				for (short i = 0; i < values.length; i++) {
					m[oldBlockStart + i] = values[i];
				}
				
				//Now that we updated main memory, the slot is no longer dirty
				slot.setDirtyBit(false);
				
			}
			

			//Bringing new memory block to cache slot
			short blockStart = (short) (address & 0xFFF0);
			short[] block = new short[c.length];
			
			for (short i = 0; i < block.length; i++) {
				block[i] = m[blockStart + i];
			}
			
			slot.setBlock(block);
			slot.setValid(true);
			slot.setTag(tag);
		}
		
		//It is found in the cache:
		else {
			System.out.print("(Cache Hit) ");
		}
		
		//"Returning" the value at that byte.
		return slot.getOffsetData(offset);
		
	}
	
	//Since this is a write-back method, we bring the address to the cache, and change the value in the cache.
	//When the address is going to be removed from the cache, the write to memory will occur (see read method).
	public short write(Slot[] c, short[] m, short address, short data) {
		
		//Find in cache, or bring to cache if cache miss:
		read(c, m, address);
		
		//Breaking the address down to its pieces
		short[] a = parse(address);
			//short tag 			= a[0];
		short slotNum 		= a[1];
		short offset 		= a[2];
		
		Slot slot = c[slotNum];
		
		//Updating data values in the cache:
		slot.setDirtyBit(true);
		slot.setOffsetData(offset, data);
		
		return slot.getOffsetData(offset);
	}
	
	//Prints the cache out nicely
	public void display(Slot[] c) {
		String rtn = "\nSlot Valid DirtyBit Tag		Data\n";
		
		
		short[] temp = new short[c[0].getBlock().length];
		
		for (short i = 0; i < c.length; i++) {
			temp = c[i].getBlock();
			rtn += String.format("%2X%6d%8d%6X", i, c[i].isValid(), c[i].isDirtyBit(), c[i].getTag());
			rtn += String.format("%12X%4X%4X%4X%4X%4X%4X%4X%4X%4X%4X%4X%4X%4X%4X%4X%n", 
					temp[0], temp[1], temp[2], temp[3], temp[4], temp[5], temp[6], temp[7],
					temp[8], temp[9], temp[10], temp[11], temp[12], temp[13], temp[14], temp[15]);

		}
		System.out.print(rtn);
	}
	
	//Breaks address down to cache bits
	//[0] = tag; [1] = slot; [2] = offset
	public short[] parse(short address) {
		short[] rtn = new short[3];
		
		short tag 		= (short) ((short)(address & 0xFF00) >>> 8);
		short slot 		= (short) ((short)(address & 0x00F0) >>> 4);
		short offset 	= (short)(address & 0x000F);
		
		rtn[0] = tag;
		rtn[1] = slot;
		rtn[2] = offset;

		
		return rtn;
		
	}
	
	//Validates hex input
	public boolean validateHex(String input) {
		try {
			short hex = (short)(Integer.parseInt(input, 16));
			return true;
			
		}catch(NumberFormatException e) {
			return false;
		}
		
	}
	
	//Convert string hex to short hex
	public short validInputAddress() {
		
		Scanner kb2 = new Scanner(System.in);
		
		String saddress;
		System.out.print("Enter the address: ");
		saddress = kb2.next();

		//Validating input as a hex value 
		while(!validateHex(saddress) || Integer.parseInt(saddress, 16) >= 0x800){
			//Since a 2K memory array only goes from 0 to 7FF, 800 is the limit
			System.out.print("Error. Please enter a hexadecimal number under 800: ");
			saddress = kb2.next();
		}
		
		//Search for address
		short address = (short)(Integer.parseInt(saddress, 16));
		return address;
		
	}

}

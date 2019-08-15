/* The slot object represents one slot in a cache.
 * 
 * Tag: 24 bits (3 bytes); From Address size-(Slot# bits + Block offset bits)
 * Slot#: 4 bits (half a byte); From 16 slots = 2^4
 * Block offset: 4 bits (half a byte); From block size of 16 = 2^4
 */

public class Slot {
	
	private boolean valid = false; //True if data in slot is valid
	private boolean dirtyBit = false;

	private short tag = 0;		
	private short[] block = new short[16]; //each object in a block is a data value at an address, the index of this object is the offset
	
	
	//Valid:
	public int isValid() {
		if (valid) {
			return 1;
		}
		else {
			return 0;
		}
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	
	//Dirty Bit:
	public int isDirtyBit() {
		if (dirtyBit) {
			return 1;
		}
		else {
			return 0;
		}
	}
	public void setDirtyBit(boolean dirtyBit) {
		this.dirtyBit = dirtyBit;
	}
	
	
	//Tag:
	public short getTag() {
		return tag;
	}
	public void setTag(short tag) {
		this.tag = tag;
	}

	
	//Data at a specific address:
	public short getOffsetData(int offset) {
		return block[offset];
	}
	public void setOffsetData(short offset, short dataValue) {
		block[offset] = dataValue;
	}
	
	
	//Block:
	public short[] getBlock() {
		return block;
	}
	public void setBlock(short[] data) {
		this.block = data;
	}
	
}



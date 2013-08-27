package time.travelers.core;

/**
 * Class encapsulating either a Terain's possible traversal types, or a units ability to travers objects.
 * @author Grevor
 *
 */
public class Traversal {
	private static final byte walkingMask = 0x1 << 0;
	private static final byte swimmingMask = 0x1 << 1;
	private static final byte flyingMask = 0x1 << 2;
	//These are simple placeholders. These mechanics may be better suited for other places.
	@SuppressWarnings("unused")
	private static final byte teamMask1 = 0x1 << 3;
	@SuppressWarnings("unused")
	private static final byte teamMask2 = 0x1 << 4;
	
	byte travBit;
	
	public Traversal(boolean canWalk, boolean canSwim, boolean canFly) {
		this.changeTravBit(canWalk, canSwim, canFly);
	}
	
	// The copy constructor.
	private Traversal(byte travByte) {
		this.travBit = travByte;
	}
	
	private void changeTravBit(boolean canWalk, boolean canSwim, boolean canFly) {
		travBit = 0x0;
		if(canWalk) {
			travBit |= walkingMask;
		} 
		if(canSwim) {
			travBit |= swimmingMask;
		}
		if(canFly) {
			travBit |= flyingMask;
		}
	}
	
	public boolean canTraverse(Traversal movementType) {
		return (this.travBit & movementType.travBit) != 0;
	}
	
	public Traversal copy() {
		return new Traversal(this.travBit);
	}
}

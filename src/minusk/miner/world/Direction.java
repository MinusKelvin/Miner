package minusk.miner.world;

/**
 * Created by MinusKelvin on 2015-08-08.
 */
public enum Direction {
	X_PLUS (0),
	X_MINUS(1),
	Y_PLUS (2),
	Y_MINUS(3);
	
	public static final int X_PLUS_BIT  = 1;
	public static final int X_MINUS_BIT = 2;
	public static final int Y_PLUS_BIT  = 4;
	public static final int Y_MINUS_BIT = 8;
	
	public final int id;
	public final int bit;
	
	Direction(int id) {
		this.id = id;
		switch (id) {
			case 0:
				bit = X_PLUS_BIT;
				break;
			case 1:
				bit = X_MINUS_BIT;
				break;
			case 2:
				bit = Y_PLUS_BIT;
				break;
			case 3:
				bit = Y_MINUS_BIT;
				break;
			default:
				bit = 0;
		}
		assert(bit != 0);
	}
	
	public boolean inDirection(int bitfield) {
		return (bitfield & bit) != 0;
	}
}

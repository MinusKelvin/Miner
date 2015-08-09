package minusk.miner.world;

import minusk.miner.Miner;

import static minusk.miner.world.Direction.*;

/**
 * Created by MinusKelvin on 2015-07-12.
 */
public class Tile {
	public final int x,y;
	
	int id = 0;
	private int connected;
	
	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void calcConnected() {
		if (id == 0)
			return;
		
		connected  = Miner.world.getTile(x+1,y).id != 0 ? X_PLUS_BIT  : 0;
		connected |= Miner.world.getTile(x-1,y).id != 0 ? X_MINUS_BIT : 0;
		connected |= Miner.world.getTile(x,y+1).id != 0 ? Y_PLUS_BIT  : 0;
		connected |= Miner.world.getTile(x,y-1).id != 0 ? Y_MINUS_BIT : 0;
		assert(connected < 0x10 && connected >= 0);
		
		int field;
		field  = Miner.world.getTile(x+1,y+1).id != 0 ? 0x1 : 0;
		field |= Miner.world.getTile(x-1,y+1).id != 0 ? 0x2 : 0;
		field |= Miner.world.getTile(x+1,y-1).id != 0 ? 0x4 : 0;
		field |= Miner.world.getTile(x-1,y-1).id != 0 ? 0x8 : 0;
		assert(field < 0x10 && field >= 0);
		
		if (X_PLUS.inDirection(connected)) {
			if (X_MINUS.inDirection(connected)) {
				if (Y_PLUS.inDirection(connected)) {
					if (Y_MINUS.inDirection(connected))
						// | X_PLUS, X_MINUS, Y_PLUS, Y_MINUS |
						connected += field;
					else {
						// | X_PLUS, X_MINUS, Y_PLUS,         |
						if ((field & 0x1)  != 0) {
							if ((field & 0x2) != 0)
								connected = 31;
							else
								connected = 33;
						} else if ((field & 0x2) != 0)
							connected = 32;
					}
				} else if (Y_MINUS.inDirection(connected)) {
					// | X_PLUS, X_MINUS,       , Y_MINUS |
					if ((field & 0x4) != 0) {
						if ((field & 0x8) != 0)
							connected = 34;
						else
							connected = 35;
					} else if ((field & 0x8) != 0)
						connected = 36;
				}
				// NO-OP | X_PLUS, X_MINUS,       ,         |
			} else if (Y_PLUS.inDirection(connected)) {
				if (Y_MINUS.inDirection(connected)) {
					// | X_PLUS,        , Y_PLUS, Y_MINUS |
					if ((field & 0x1) != 0) {
						if ((field & 0x4) != 0)
							connected = 37;
						else
							connected = 38;
					} else if ((field & 0x4) != 0)
						connected = 39;
				} else {
					// | X_PLUS,        , Y_PLUS,         |
					if ((field & 0x1) != 0)
						connected = 43;
				}
			} else if (Y_MINUS.inDirection(connected)) {
				// | X_PLUS,        ,       , Y_MINUS |
				if ((field & 0x4) != 0)
					connected = 44;
			}
			// NO-OP | X_PLUS,        ,       ,         |
		} else if (X_MINUS.inDirection(connected)) {
			if (Y_PLUS.inDirection(connected)) {
				if (Y_MINUS.inDirection(connected)) {
					// |       , X_MINUS, Y_PLUS, Y_MINUS |
					if ((field & 0x2) != 0) {
						if ((field & 0x8) != 0)
							connected = 40;
						else
							connected = 41;
					} else if ((field & 0x8) != 0)
						connected = 42;
				} else {
					// |       , X_MINUS, Y_PLUS,         |
					if ((field & 0x2) != 0)
						connected = 45;
				}
			} else if (Y_MINUS.inDirection(connected)) {
				// |       , X_MINUS,       , Y_MINUS |
				if ((field & 0x8) != 0)
					connected = 46;
			}
			// NO-OP |       , X_MINUS,       ,         |
		}
		// NO-OP |       ,        , ??????, ??????? |
		assert(connected >= 0 && connected < 47);
	}
	
	int getConnected() {
		return connected;
	}
}

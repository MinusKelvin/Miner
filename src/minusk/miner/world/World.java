package minusk.miner.world;

import minusk.miner.Graphics;

import java.util.ArrayList;

/**
 * Created by MinusKelvin on 2015-07-11.
 */
public class World {
	private ArrayList<Entity> entities = new ArrayList<>();

	public void render() {
		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 32; j++) {
				Graphics.drawTile(i,j,0,0);
			}
		}
	}
}

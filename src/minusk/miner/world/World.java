package minusk.miner.world;

import minusk.miner.Graphics;
import minusk.miner.world.friendly.Player;

import java.util.ArrayList;

/**
 * Created by MinusKelvin on 2015-07-11.
 */
public class World {
	public static final int WORLD_HEIGHT = 1024;
	public final Player player = new Player();
	
	private final ArrayList<Entity> entities = new ArrayList<>();
	private final ArrayList<Tile[]> xplus = new ArrayList<>(256);
	private final ArrayList<Tile[]> xminus = new ArrayList<>(256);
	
	public void update() {
		player.update(null);
		
		int minx = (int) Math.floor(player.getCenterX() - Graphics.getWidth() / 32f - 32);
		int maxx = (int) Math.ceil(player.getCenterX() + Graphics.getWidth() / 32f + 32);
		for (int x = xminus.size() + minx; x <= 0; x++) {
			Tile[] tiles = new Tile[WORLD_HEIGHT];
			for (int i = 0; i < WORLD_HEIGHT; i++) {
				tiles[i] = new Tile();
			}
			xminus.add(tiles);
		}
		for (int x = maxx - xplus.size(); x >= 0; x--) {
			Tile[] tiles = new Tile[WORLD_HEIGHT];
			for (int i = 0; i < WORLD_HEIGHT; i++) {
				tiles[i] = new Tile();
			}
			xplus.add(tiles);
		}
	}
	
	public void render() {
		for (int i = (int) Math.floor(player.getCenterX() - Graphics.getWidth() / 32f);
		     i < (int) Math.ceil(player.getCenterX() + Graphics.getWidth() / 32f); i++) {
			Tile[] column = i < 0 ? xminus.get(-i) : xplus.get(i);
			for (int j = (int) Math.max(0, Math.floor(player.getCenterY() - Graphics.getHeight() / 32f));
			     j < (int) Math.min(WORLD_HEIGHT, Math.ceil(player.getCenterY() + Graphics.getHeight() / 32f)); j++) {
				Graphics.drawTile(i, j, column[j].id, 0);
			}
		}
	}
}

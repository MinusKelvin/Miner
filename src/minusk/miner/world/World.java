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
	
	public void init() {
		xminus.add(genTiles(-1));
		xplus.add(genTiles(0));
		ensureSize((int) Math.floor(player.getCenterX() - Graphics.getWidth() / 32f - 32),
				(int) Math.ceil(player.getCenterX() + Graphics.getWidth() / 32f + 32));
	}
	
	public void update() {
		player.update(null);
		
		ensureSize((int) Math.floor(player.getCenterX() - Graphics.getWidth() / 32f - 32),
				(int) Math.ceil(player.getCenterX() + Graphics.getWidth() / 32f + 32));
	}
	
	private void ensureSize(int minx, int maxx) {
		for (int x = xminus.size() + minx; x < 0; x++) {
			Tile[] last = xminus.get(xminus.size()-1);
			xminus.add(genTiles(-1-xminus.size()));
			for (Tile t : last) {
				t.calcConnected();
			}
		}
		for (int x = maxx - xplus.size(); x >= 0; x--) {
			Tile[] last = xplus.get(xplus.size()-1);
			xplus.add(genTiles(xplus.size()));
			for (Tile t : last) {
				t.calcConnected();
			}
		}
	}
	
	private  Tile[] genTiles(int x) {
		Tile[] tiles = new Tile[WORLD_HEIGHT];
		for (int i = 0; i < WORLD_HEIGHT; i++) {
			tiles[i] = new Tile(x,i);
			tiles[i].id = /*(int) (Math.random()*1.3);//*/i < 700 ? 1 : i < 720 ? 2 : 0;
		}
		return tiles;
	}
	
	public void render() {
		for (int i = (int) Math.floor(player.getCenterX() - Graphics.getWidth() / 32f);
		     // i = leftmost tile that could need to be rendered
		     i < (int) Math.ceil(player.getCenterX() + Graphics.getWidth() / 32f);
		     // i < rightmost tile that could need to be rendered
		     i++) {
			Tile[] column = getColumn(i);
			for (int j = (int) Math.max(0, Math.floor(player.getCenterY() - Graphics.getHeight() / 32f));
			     j < (int) Math.min(WORLD_HEIGHT, Math.ceil(player.getCenterY() + Graphics.getHeight() / 32f)); j++) {
				if (column[j].id > 0)
					Graphics.drawTile(i, j, column[j].id-1, column[j].getConnected());
			}
		}
		
		player.render();
	}
	
	public Tile[] getColumn(int x) {
		ensureSize(x,x);
		return x < 0 ? xminus.get(-1-x) : xplus.get(x);
	}
	
	public Tile getTile(int x, int y) {
		if (y >= WORLD_HEIGHT || y < 0) {
			Tile tile = new Tile(x,y);
			tile.id = -1;
			return tile;
		}
		return getColumn(x)[y];
	}
}

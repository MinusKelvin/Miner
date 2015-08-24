package minusk.miner.world;

import minusk.miner.Graphics;
import minusk.miner.OpenSimplexNoise;
import minusk.miner.world.friendly.Player;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

/**
 * Created by MinusKelvin on 2015-07-11.
 */
public class World {
	public static final int WORLD_HEIGHT = 1024;
	public final Player player = new Player();
	
	private final ArrayList<Entity> entities = new ArrayList<>();
	private final ArrayList<Tile[]> xplus = new ArrayList<>(256);
	private final ArrayList<Tile[]> xminus = new ArrayList<>(256);
	private final OpenSimplexNoise terrainNoise = new OpenSimplexNoise(System.currentTimeMillis());
	
	public void init() {
		xminus.add(genTiles(-1));
		xplus.add(genTiles(0));
		ensureSize((int) Math.floor(player.getCenterX() - Graphics.getWidth() / 32f - 32),
				(int) Math.ceil(player.getCenterX() + Graphics.getWidth() / 32f + 32));
	}
	
	public void update() {
		if (Graphics.mousePressed(GLFW_MOUSE_BUTTON_LEFT))
			setTile((int)Math.floor(getMouseX()),(int)Math.floor(getMouseY()),2);
		
		player.update(null);
		
		ensureSize((int) Math.floor(player.getCenterX() - Graphics.getWidth() / 32f - 32),
				(int) Math.ceil(player.getCenterX() + Graphics.getWidth() / 32f + 32));
	}
	
	private void ensureSize(int minx, int maxx) {
		for (int x = xminus.size() + minx; x < 0; x++) {
			Tile[] last = xminus.get(xminus.size()-1);
			xminus.add(genTiles(-1-xminus.size()));
			for (int i = 0 ; i < last.length; i++) {
				last[i].calcConnected(1-xminus.size(), i);
			}
		}
		for (int x = maxx - xplus.size(); x >= 0; x--) {
			Tile[] last = xplus.get(xplus.size()-1);
			xplus.add(genTiles(xplus.size()));
			for (int i = 0 ; i < last.length; i++) {
				last[i].calcConnected(xplus.size()-2, i);
			}
		}
	}
	
	private  Tile[] genTiles(int x) {
		Tile[] tiles = new Tile[WORLD_HEIGHT];
		int h = (int) (15*terrainNoise.eval(x / 25.0, 0) + 100*terrainNoise.eval(x / 250.0, 0));
		int hl = Math.min(Math.min((int) (15*terrainNoise.eval((x+1) / 25.0, 0) + 100*terrainNoise.eval((x+1) / 250.0, 0)),
				(int) (15*terrainNoise.eval((x-1) / 25.0, 0) + 100*terrainNoise.eval((x-1) / 250.0, 0))), h);
		for (int i = 0; i < WORLD_HEIGHT; i++) {
			tiles[i] = new Tile();
			tiles[i].id = i-hl < 700 ? 1 : i-h <= 700 ? 2 : 0;
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
		ensureSize(x, x);
		return x < 0 ? xminus.get(-1-x) : xplus.get(x);
	}
	
	public Tile getTile(int x, int y) {
		if (y >= WORLD_HEIGHT || y < 0) {
			Tile tile = new Tile();
			tile.id = -1;
			return tile;
		}
		return getColumn(x)[y];
	}
	
	public void setTile(int x, int y, int id) {
		getTile(x,y).id = id;
		for (int i = x-1; i <= x+1; i++)
			for (int j = y-1; j <= y+1; j++)
				getTile(i,j).calcConnected(i,j);
	}
	
	public float getMouseX() {
		return Graphics.getMouseX()/16 + player.getCenterX() - Graphics.getWidth()/32f;
	}
	
	public float getMouseY() {
		return Graphics.getHeight()/32f + player.getCenterY() - Graphics.getMouseY()/16;
	}
}

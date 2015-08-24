package minusk.miner.world;

import minusk.miner.Miner;
import org.joml.Vector2f;

import java.util.Iterator;

/**
 * Created by MinusKelvin on 2015-07-11.
 */
public abstract class Entity {
	private boolean dead = false;
	
	public abstract float getX();
	
	public abstract float getY();
	
	public abstract float getWidth();
	
	public abstract float getHeight();
	
	public abstract void render();
	
	protected abstract void update();
	
	public void update(Iterator<Entity> iter) {
		if (!dead)
			update();
		if (dead && iter != null) {
			onDeath();
			iter.remove();
		}
	}
	
	public void kill() {
		dead = true;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	protected void onDeath() {
	}
	
	public float getCenterX() {
		return getX() + getWidth() / 2;
	}
	
	public float getCenterY() {
		return getY() + getHeight() / 2;
	}
	
	protected boolean[] move(Vector2f velocity, Vector2f position) {
		int xCycles = (int) velocity.x;
		int yCycles = (int) velocity.y;
		float leftoverX = velocity.x - xCycles;
		float leftoverY = velocity.y - yCycles;
		float xDirection = Math.signum(velocity.x);
		float yDirection = Math.signum(velocity.y);
		boolean[] collides = new boolean[4];
		
		for (xCycles = Math.abs(xCycles); xCycles >= 0; xCycles--) {
			position.x += xCycles == 0 ? leftoverX : xDirection;
			if (intersects(position.x,position.y)) {
				if (xDirection == 1) {
					position.x = (float) Math.ceil(position.x + getWidth() - 1) - getWidth();
					collides[Direction.X_PLUS.id] = true;
					velocity.x = 0;
					break;
				} else {
					position.x = (float) Math.floor(position.x + 1);
					collides[Direction.X_MINUS.id] = true;
					velocity.x = 0;
					break;
				}
			}
		}
		
		for (yCycles = Math.abs(yCycles); yCycles >= 0; yCycles--) {
			position.y += yCycles == 0 ? leftoverY : yDirection;
			if (intersects(position.x,position.y)) {
				if (yDirection == 1) {
					position.y = (float) Math.ceil(position.y + getHeight() - 1) - getHeight();
					collides[Direction.Y_PLUS.id] = true;
					velocity.y = 0;
					break;
				} else {
					position.y = (float) Math.floor(position.y + 1);
					collides[Direction.Y_MINUS.id] = true;
					velocity.y = 0;
					break;
				}
			}
		}
		
		return collides;
	}
	
	private boolean intersects(float x, float y) {
		int minx = (int) Math.floor(x);
		int miny = (int) Math.floor(y);
		int maxx = (int) Math.ceil(x + getWidth());
		int maxy = (int) Math.ceil(y + getHeight());
		for (int i = minx; i < maxx; i++) {
			Tile[] column = Miner.world.getColumn(i);
			for (int j = miny; j < maxy; j++)
				if (j < 0 || j >= World.WORLD_HEIGHT || column[j].id != 0)
					return true;
		}
		return false;
	}
}

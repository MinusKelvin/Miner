package minusk.miner.world;

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
}

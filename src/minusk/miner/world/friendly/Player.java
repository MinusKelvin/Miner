package minusk.miner.world.friendly;

import minusk.miner.Graphics;
import minusk.miner.world.Entity;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by MinusKelvin on 2015-07-12.
 */
public class Player extends Entity {
	private float x=0, y=0;
	
	@Override
	public float getX() {
		return x;
	}
	
	@Override
	public float getY() {
		return y;
	}
	
	@Override
	public float getWidth() {
		return 0.85f;
	}
	
	@Override
	public float getHeight() {
		return 1.75f;
	}
	
	protected void update() {
		if (Graphics.keyPressed(GLFW_KEY_A))
			x -= 0.0625f;
		if (Graphics.keyPressed(GLFW_KEY_D))
			x += 0.0625f;
		if (Graphics.keyPressed(GLFW_KEY_W))
			y += 0.0625f;
		if (Graphics.keyPressed(GLFW_KEY_S))
			y -= 0.0625f;
	}
}

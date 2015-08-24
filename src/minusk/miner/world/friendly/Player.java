package minusk.miner.world.friendly;

import minusk.miner.Graphics;
import minusk.miner.world.Direction;
import minusk.miner.world.Entity;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by MinusKelvin on 2015-07-12.
 */
public class Player extends Entity {
	private int jumpLength = 12;
	private float acceleration = 0.03f;
	private float jumpSpeed = 0.8f;
	private float maxSpeed = 0.5f;
	private float gravity = 0.08f;
	
	private Vector2f position = new Vector2f(-getWidth()/2,730-getHeight()/2);
	private Vector2f velocity = new Vector2f();
	private int jumpTimer = jumpLength;
	
	@Override
	public float getX() {
		return position.x;
	}
	
	@Override
	public float getY() {
		return position.y;
	}
	
	@Override
	public float getWidth() {
		return 0.85f;
	}
	
	@Override
	public float getHeight() {
		return 1.75f;
	}
	
	@Override
	protected void update() {
		if (Graphics.keyPressed(GLFW_KEY_A))
			velocity.x -= acceleration;
		if (Graphics.keyPressed(GLFW_KEY_D))
			velocity.x += acceleration;
		if (!(Graphics.keyPressed(GLFW_KEY_D) ^ Graphics.keyPressed(GLFW_KEY_A)))
			velocity.x *= 0.7f;
		
		if (Graphics.keyPressed(GLFW_KEY_SPACE) && jumpTimer > 0) {
			jumpTimer--;
			velocity.y = jumpSpeed;
		} else
			jumpTimer = 0;
		
		velocity.y -= gravity;
		
		if (velocity.y > 2)
			velocity.y = 2;
		else if (velocity.y < -2)
			velocity.y = -2;
		
		if (velocity.x > maxSpeed)
			velocity.x = maxSpeed;
		else if (velocity.x < -maxSpeed)
			velocity.x = -maxSpeed;
		
		if (move(velocity, position)[Direction.Y_MINUS.id])
			jumpTimer = jumpLength;
	}
	
	@Override
	public void render() {
		Graphics.drawEntity(0,getX(),getY(),getWidth(),getHeight());
	}
}

package minusk.miner;

import minusk.miner.world.World;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;

/**
 * Created by MinusKelvin on 2015-07-11.
 */
public class Miner {
	private static boolean looping = true;
	
	public static final World world = new World();
	
	public static void main(String[] args) {
		Graphics.initialize();
		world.init();
		
		while (looping) {
			world.update();
			
			render();
			
			glfwPollEvents();
		}
	}
	
	static void render() {
		Graphics.beginFrame();
		
		world.render();
		
		Graphics.endFrame();
	}
	
	public static void endLoop() {
		looping = false;
	}
}

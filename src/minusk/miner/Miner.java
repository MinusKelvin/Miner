package minusk.miner;

import minusk.miner.world.World;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by MinusKelvin on 2015-07-11.
 */
public class Miner {
	private static boolean looping = true;
	
	public static final World world = new World();
	
	private static int cpos = 0;
	private static double[] lastFrames = new double[15];
	private static double lastTime = 0;
	
	public static void main(String[] args) throws InterruptedException {
		Graphics.initialize();
		world.init();
		
		double updateInterval = 1.0 / 60;
		double lastup = glfwGetTime();
		
		while (looping) {
			double time = glfwGetTime();
			
			if ((time-lastup)/updateInterval >= 1) {
				
				double updatetime = glfwGetTime();
				int iters = 0;
				while ((int) ((time-lastup)/updateInterval) > 0 && iters <= 10) {
					world.update();
					lastup += updateInterval;
					iters++;
				}
				double currenttime = glfwGetTime();
				if (currenttime - updatetime > iters * updateInterval)
					lastup = currenttime;
				
				render();
				
				double polltime = glfwGetTime();
				glfwPollEvents();
				lastup += glfwGetTime() - polltime;
				
				currenttime = glfwGetTime();
				if ((int) ((updateInterval - (currenttime - lastup)) * 1000) > 0) {
					double waittime = (updateInterval - (currenttime - lastup));
					Thread.sleep((long) (waittime*1000));
				}
			}
		}
	}
	
	static void render() {
		cpos = (cpos + 1) % lastFrames.length;
		double t = glfwGetTime();
		lastFrames[cpos] = t - lastTime;
		lastTime = t;
		System.out.printf("FPS: %.1f\n", 1/Util.average(lastFrames));
		
		Graphics.beginFrame();
		
		world.render();
		
		Graphics.endFrame();
	}
	
	public static void endLoop() {
		looping = false;
	}
}

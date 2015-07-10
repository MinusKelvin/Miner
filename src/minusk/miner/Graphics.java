package minusk.miner;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.opengl.GLContext;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL30.glClearBufferfv;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created by MinusKelvin on 2015-07-11.
 */
public class Graphics {
	private static long window;
	private static FloatBuffer clearcolor = (FloatBuffer) BufferUtils.createFloatBuffer(4).put(new float[] {0,0,0,1}).flip();

	static void initialize() {
		if (glfwInit() != GL_TRUE) {
			System.err.println("Could not initialize GLFW");
			System.exit(1);
		}

		glfwSetErrorCallback(Callbacks.errorCallbackPrint());

		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

		window = glfwCreateWindow(1024, 578, "Miner", NULL, NULL);
		if (window == NULL)
			System.exit(2);

		glfwMakeContextCurrent(window);
		GLContext.createFromCurrent();
	}

	static void beginFrame() {
		glClearBufferfv(GL_COLOR, 0, clearcolor);
	}

	static void endFrame() {
		glfwSwapBuffers(window);
		glfwPollEvents();
		if (glfwWindowShouldClose(window) == GL_TRUE)
			Miner.endLoop();
	}
}

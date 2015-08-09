package minusk.miner;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.glfw.GLFWWindowRefreshCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GLContext;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LEVEL;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created by MinusKelvin on 2015-07-11.
 */
public class Graphics {
	private static final int DEFAULT_WIDTH = 1024, DEFAULT_HEIGHT = 576;
	
	private static long window;
	private static int tilesBuffer, entityBuffer, tileVertexCount, entityVertexCount;
	private static int program, projloc;
	private static int tilesTex;
	private static int width = DEFAULT_WIDTH, height = DEFAULT_HEIGHT;
	private static ByteBuffer tiles, entities;
	private static Matrix4f proj = new Matrix4f();
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16),
			clearColor = (FloatBuffer) BufferUtils.createFloatBuffer(4).put(new float[] {0.4f, 0.6f, 1, 1}).flip();
	
	@SuppressWarnings("unused")
	private static GLFWWindowSizeCallback persistWindowSize;
	@SuppressWarnings("unused")
	private static GLFWWindowCloseCallback persistWindowClose;
	@SuppressWarnings("unused")
	private static GLFWWindowRefreshCallback persistWindowRefresh;
	
	public static void drawTile(int x, int y, int graphic, int connectType) {
		tiles.putFloat(x);
		tiles.putFloat(y);
		tiles.putFloat(TILE_TEXCOORD_X_LOOKUP[connectType]);
		tiles.putFloat(TILE_TEXCOORD_Y_LOOKUP[connectType] + 0.1249999f);
		tiles.putFloat(graphic);
		tileVertexCount++;
		
		tiles.putFloat(x + 1);
		tiles.putFloat(y);
		tiles.putFloat(TILE_TEXCOORD_X_LOOKUP[connectType] + 0.1249999f);
		tiles.putFloat(TILE_TEXCOORD_Y_LOOKUP[connectType] + 0.1249999f);
		tiles.putFloat(graphic);
		tileVertexCount++;
		
		tiles.putFloat(x + 1);
		tiles.putFloat(y + 1);
		tiles.putFloat(TILE_TEXCOORD_X_LOOKUP[connectType] + 0.1249999f);
		tiles.putFloat(TILE_TEXCOORD_Y_LOOKUP[connectType]);
		tiles.putFloat(graphic);
		tileVertexCount++;
		
		tiles.putFloat(x);
		tiles.putFloat(y);
		tiles.putFloat(TILE_TEXCOORD_X_LOOKUP[connectType]);
		tiles.putFloat(TILE_TEXCOORD_Y_LOOKUP[connectType] + 0.1249999f);
		tiles.putFloat(graphic);
		tileVertexCount++;
		
		tiles.putFloat(x + 1);
		tiles.putFloat(y + 1);
		tiles.putFloat(TILE_TEXCOORD_X_LOOKUP[connectType] + 0.1249999f);
		tiles.putFloat(TILE_TEXCOORD_Y_LOOKUP[connectType]);
		tiles.putFloat(graphic);
		tileVertexCount++;
		
		tiles.putFloat(x);
		tiles.putFloat(y + 1);
		tiles.putFloat(TILE_TEXCOORD_X_LOOKUP[connectType]);
		tiles.putFloat(TILE_TEXCOORD_Y_LOOKUP[connectType]);
		tiles.putFloat(graphic);
		tileVertexCount++;
	}
	
	public static void drawEntity(int id, float x, float y, float width, float height) {
		entities.putFloat(x);
		entities.putFloat(y);
		entityVertexCount++;
		
		entities.putFloat(x + width);
		entities.putFloat(y);
		entityVertexCount++;
		
		entities.putFloat(x + width);
		entities.putFloat(y + height);
		entityVertexCount++;
		
		entities.putFloat(x);
		entities.putFloat(y);
		entityVertexCount++;
		
		entities.putFloat(x + width);
		entities.putFloat(y + height);
		entityVertexCount++;
		
		entities.putFloat(x);
		entities.putFloat(y + height);
		entityVertexCount++;
	}
	
	static void beginFrame() {
		glClearBufferfv(GL_COLOR, 0, clearColor);
		
		tiles = glMapBufferRange(GL_ARRAY_BUFFER, 0, 1001880, GL_MAP_WRITE_BIT, tiles);
		tiles.position(0);
		tileVertexCount = 0;
		
		glBindBuffer(GL_ARRAY_BUFFER, entityBuffer);
		entities = glMapBufferRange(GL_ARRAY_BUFFER, 0, 48, GL_MAP_WRITE_BIT, entities);
		entities.position(0);
		entityVertexCount = 0;
	}
	
	static void endFrame() {
		proj.identity().orthoSymmetric(width / 16f, height / 16f, -1, 1).translate(-Miner.world.player.getCenterX(), -Miner.world.player.getCenterY(), 0);
		matrixBuffer.position(0);
		proj.get(matrixBuffer);
		matrixBuffer.position(0);
		glUniformMatrix4fv(projloc, false, matrixBuffer);
		
		// Entities Buffer
		glUnmapBuffer(GL_ARRAY_BUFFER);
		
		glDisableVertexAttribArray(1);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
		
		glDrawArrays(GL_TRIANGLES, 0, entityVertexCount);
		
		// Tiles Buffer
		glBindBuffer(GL_ARRAY_BUFFER, tilesBuffer);
		glUnmapBuffer(GL_ARRAY_BUFFER);
		
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 20, 0);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 20, 8);
		glEnableVertexAttribArray(1);
		
		glDrawArrays(GL_TRIANGLES, 0, tileVertexCount);
		
		glfwSwapBuffers(window);
	}
	
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
		glfwWindowHint(GLFW_SAMPLES, 0);
		
		window = glfwCreateWindow(width, height, "Miner", NULL, NULL);
		if (window == NULL)
			System.exit(2);
		
		glfwMakeContextCurrent(window);
		GLContext.createFromCurrent();
//		glfwSwapInterval(0);
		
		glfwSetWindowCloseCallback(window, persistWindowClose = GLFWWindowCloseCallback(Graphics::closeRequested));
		glfwSetWindowSizeCallback(window, persistWindowSize = GLFWWindowSizeCallback(Graphics::windowSize));
		glfwSetWindowRefreshCallback(window, persistWindowRefresh = GLFWWindowRefreshCallback(Graphics::refresh));
		
		glBindVertexArray(glGenVertexArrays());
		
		tilesBuffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, tilesBuffer);
		glBufferData(GL_ARRAY_BUFFER, 1001880, GL_STREAM_DRAW);
		
		entityBuffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, entityBuffer);
		glBufferData(GL_ARRAY_BUFFER, 48, GL_STREAM_DRAW);
		
		glBindBuffer(GL_ARRAY_BUFFER, tilesBuffer);
		glEnableVertexAttribArray(0);
		
		int vs = glCreateShader(GL_VERTEX_SHADER);
		int fs = glCreateShader(GL_FRAGMENT_SHADER);
		program = glCreateProgram();
		String vsource = Util.read(Graphics.class.getResourceAsStream("/minusk/miner/res/simple.vs.glsl"));
		String fsource = Util.read(Graphics.class.getResourceAsStream("/minusk/miner/res/simple.fs.glsl"));
		glShaderSource(vs, vsource);
		glShaderSource(fs, fsource);
		
		glCompileShader(vs);
		if (glGetShaderi(vs, GL_COMPILE_STATUS) != GL_TRUE) {
			System.err.println("Vertex Shader failed to compile");
			System.err.println(glGetShaderInfoLog(vs));
			System.exit(3);
		}
		
		glCompileShader(fs);
		if (glGetShaderi(fs, GL_COMPILE_STATUS) != GL_TRUE) {
			System.err.println("Fragment Shader failed to compile");
			System.err.println(glGetShaderInfoLog(fs));
			System.exit(4);
		}
		
		glAttachShader(program, vs);
		glAttachShader(program, fs);
		glLinkProgram(program);
		if (glGetProgrami(program, GL_LINK_STATUS) != GL_TRUE) {
			System.err.println("Link failed");
			System.err.println(glGetProgramInfoLog(program));
			System.exit(5);
		}
		glDeleteShader(vs);
		glDeleteShader(fs);
		
		glUseProgram(program);
		projloc = glGetUniformLocation(program, "proj");
		
		tilesTex = glGenTextures();
		glBindTexture(GL_TEXTURE_2D_ARRAY, tilesTex);
		
		IntBuffer data = BufferUtils.createIntBuffer(3);
		ByteBuffer imageContents = stbi_load("res/tiles.png", ((IntBuffer) data.position(0)).slice(),
				((IntBuffer) data.position(1)).slice(), ((IntBuffer) data.position(2)).slice(), 4);
		
		glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_RGBA8, 128, 128, 16, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageContents);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAX_LEVEL, 0);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	}
	
	static void windowSize(long window, int width, int height) {
		if (width > 1920 || height > 1080) {
			width = Math.min(width, 1920);
			height = Math.min(height, 1080);
			glfwSetWindowSize(window, width, height);
		}
		Graphics.width = width;
		Graphics.height = height;
		glViewport(0, 0, width, height);
	}
	
	static void closeRequested(long window) {
		Miner.endLoop();
	}
	
	static void refresh(long window) {
		Miner.render();
	}
	
	public static int getWidth() {
		return width;
	}
	
	public static int getHeight() {
		return height;
	}
	
	public static boolean keyPressed(int key) {
		return glfwGetKey(window, key) == GLFW_PRESS;
	}
	
	private static final float[] TILE_TEXCOORD_X_LOOKUP = {
			0.625f, 0,      0.25f,  0.125f, 0.375f, 0.5f,   0.75f,  0.625f,
			0.375f, 0.5f,   0.75f,  0.625f, 0.375f, 0.5f,   0.75f,  0.125f,
			0.5f,   0.375f, 0.125f, 0.5f,   0.25f,  0.625f, 0.25f,  0.375f,
			0.625f, 0,      0,      0.125f, 0.25f,  0,      0.125f, 0.125f,
			0.375f, 0.75f,  0.125f, 0.5f,   0.625f, 0,      0.375f, 0.625f,
			0.25f,  0.75f,  0.5f,   0,      0,      0.25f,  0.25f,
	};
	
	private static final float[] TILE_TEXCOORD_Y_LOOKUP = {
			0.125f, 0.375f, 0.375f, 0.375f, 0.25f,  0.25f,  0.25f,  0.25f,
			0,      0,      0,      0,      0.125f, 0.125f, 0.125f, 0.625f,
			0.625f, 0.625f, 0.5f,   0.75f,  0.625f, 0.625f, 0.5f,   0.75f,
			0.75f,  0.625f, 0.5f,   0.75f,  0.75f,  0.75f,  0.125f, 0.25f,
			0.5f,   0.5f,   0,      0.375f, 0.375f, 0.125f, 0.375f, 0.5f,
			0.125f, 0.375f, 0.5f,   0.25f,  0,      0.25f,  0,
	};
}

package minusk.miner;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.Callbacks;
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
	private static long window;
	private static FloatBuffer clearcolor = (FloatBuffer) BufferUtils.createFloatBuffer(4).put(new float[] {0,0,0,1}).flip();
	private static int tilesBuffer, tileVertexCount;
	private static int program, projloc;
	private static int tilesTex;
	private static ByteBuffer tiles;
	private static Matrix4f proj = new Matrix4f();
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

	public static void drawTile(int x, int y, int graphic, int connectType) {
		tiles.putFloat(x);
		tiles.putFloat(y);
		tiles.putFloat(0);
		tiles.putFloat(0.125f);
		tiles.putFloat(graphic);
		tileVertexCount++;

		tiles.putFloat(x+1);
		tiles.putFloat(y);
		tiles.putFloat(0.125f);
		tiles.putFloat(0.125f);
		tiles.putFloat(graphic);
		tileVertexCount++;

		tiles.putFloat(x+1);
		tiles.putFloat(y+1);
		tiles.putFloat(0.125f);
		tiles.putFloat(0);
		tiles.putFloat(graphic);
		tileVertexCount++;

		tiles.putFloat(x);
		tiles.putFloat(y);
		tiles.putFloat(0);
		tiles.putFloat(0.125f);
		tiles.putFloat(graphic);
		tileVertexCount++;

		tiles.putFloat(x+1);
		tiles.putFloat(y+1);
		tiles.putFloat(0.125f);
		tiles.putFloat(0);
		tiles.putFloat(graphic);
		tileVertexCount++;

		tiles.putFloat(x);
		tiles.putFloat(y+1);
		tiles.putFloat(0);
		tiles.putFloat(0);
		tiles.putFloat(graphic);
		tileVertexCount++;
	}

	static void beginFrame() {
		glClearBufferfv(GL_COLOR, 0, clearcolor);
		tiles = glMapBufferRange(GL_ARRAY_BUFFER, 0, 1001880, GL_MAP_WRITE_BIT, tiles);
		tiles.position(0);
		tileVertexCount = 0;

		matrixBuffer.position(0);
		proj.get(matrixBuffer);
		matrixBuffer.position(0);
		glUniformMatrix4fv(projloc, false, matrixBuffer);
	}

	static void endFrame() {
		glUnmapBuffer(GL_ARRAY_BUFFER);

		glDrawArrays(GL_TRIANGLES, 0, tileVertexCount);

		glfwSwapBuffers(window);
		glfwPollEvents();
		if (glfwWindowShouldClose(window) == GL_TRUE)
			Miner.endLoop();
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

		window = glfwCreateWindow(1024, 578, "Miner", NULL, NULL);
		if (window == NULL)
			System.exit(2);

		glfwMakeContextCurrent(window);
		GLContext.createFromCurrent();

		glBindVertexArray(glGenVertexArrays());

		tilesBuffer = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, tilesBuffer);
		glBufferData(GL_ARRAY_BUFFER, 1001880, GL_STREAM_DRAW);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 20, 0);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 20, 8);
		glEnableVertexAttribArray(1);

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

		proj.setOrthoSymmetric(1024/16f, 576/16f, -1, 1);
	}
}

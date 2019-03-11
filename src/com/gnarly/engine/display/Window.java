package com.gnarly.engine.display;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_DECORATED;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;

public class Window {

	public static int
		SCREEN_WIDTH,
		SCREEN_HEIGHT;
	
	public static float SCALE;
	
	private long window;
	private int width, height;
	private boolean resized;
	
	private int[] mouseButtons = new int[GLFW_MOUSE_BUTTON_LAST + 1];
	private int[] keys = new int[GLFW_KEY_LAST + 1];
	
	private StringBuilder curKeys = new StringBuilder();
	
	public Window(String title, boolean vSync) {
		init(0, 0, title, vSync, false, false, false);
	}
	
	public Window(String title, boolean vSync, boolean resizable, boolean decorated) {
		init(800, 500, title, vSync, resizable, decorated, true);
	}
	
	public Window(int width, int height, String title, boolean vSync, boolean resizable, boolean decorated) {
		init(width, height, title, vSync, resizable, decorated, false);
	}
	
	public void init(int lwidth, int lheight, String title, boolean vSync, boolean resizable, boolean decorated, boolean maximized) {
		glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
		
		for (int i = 0; i < mouseButtons.length; i++)
			mouseButtons[i] = 0;
		
		if(!glfwInit()) {
			System.err.println("GLFW failed to initialize!");
			System.exit(-1);
		}
		
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		
		glfwWindowHint(GLFW_RESIZABLE, resizable ? GLFW_TRUE : GLFW_FALSE);
		glfwWindowHint(GLFW_DECORATED, decorated ? GLFW_TRUE : GLFW_FALSE);
		glfwWindowHint(GLFW_MAXIMIZED, maximized ? GLFW_TRUE : GLFW_FALSE);
		
		GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		SCREEN_WIDTH = vidMode.width();
		SCREEN_HEIGHT = vidMode.height();
		SCALE = SCREEN_HEIGHT / 1080f;
		if(lwidth == 0 || lheight == 0) {
			width = vidMode.width();
			height = vidMode.height();
			window = glfwCreateWindow(width, height, title, glfwGetPrimaryMonitor(), 0);
		}
		else {
			this.width = lwidth;
			this.height = lheight;
			window = glfwCreateWindow(width, height, title, 0, 0);
		}
		
		glfwMakeContextCurrent(window);
		createCapabilities();
		 
		glfwSwapInterval(vSync ? 1 : 0);
		
		glfwSetWindowSizeCallback(window, (long window, int w, int h) -> {
			width = w;
			height = h;
			resized = true;
			glViewport(0, 0, width, height);
		});
		
		glfwSetMouseButtonCallback(window, (long window, int button, int action, int mods) -> {
			mouseButtons[button] = action;
		});
		
		glfwSetCharCallback(window, (long window, int codepoint) -> {
			curKeys.append((char) codepoint);
		});
		
		glfwSetKeyCallback(window, (long window, int key, int scancode, int action, int mods) -> {
			keys[key] = action;
			if(action != GLFW_RELEASE && (key == GLFW_KEY_ENTER || key == GLFW_KEY_DELETE || key == GLFW_KEY_BACKSPACE || key == GLFW_KEY_RIGHT || key == GLFW_KEY_LEFT))
				curKeys.append((char) key);
		});
		
		glClearColor(0, 0, 0, 1);
		
		glEnable(GL_TEXTURE_2D);
		
		glEnable(GL_DEPTH_TEST);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		int[] awidth = new int[1], aheight = new int[1];
		glfwGetWindowSize(window, awidth, aheight);
		width = awidth[0];
		height = aheight[0];
	}
	
	public void update() {
		for (int i = 0; i < mouseButtons.length; i++)
			if (mouseButtons[i] == 1)
				++mouseButtons[i];
		for (int i = 0; i < keys.length; i++)
			if (keys[i] == 1)
				++keys[i];
		curKeys.setLength(0);
		resized = false;
		glfwPollEvents();
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
	
	public void swap() {
		glfwSwapBuffers(window);
	}
	
	public void close() {
		glfwSetWindowShouldClose(window, true);
	}
	
	public static void terminate() {
		glfwTerminate();
	}
	
	public boolean shouldClose() {
		return glfwWindowShouldClose(window);
	}
	
	public String getKeys() {
		return curKeys.toString();
	}
	
	public int keyPressed(int keyCode) {
		return keys[keyCode];
	}
	
	public Vector3f getMouseCoords(Camera camera) {
		double[] x = new double[1], y = new double[1];
		glfwGetCursorPos(window, x, y);
		Vector3f ret = new Vector3f((float) x[0], (float) y[0], 0);
		return ret.mul(camera.getWidth() / this.width, camera.getHeight() / this.height, 1);
	}
	
	public int mousePressed(int button) {
		return mouseButtons[button];
	}
	
	public boolean wasResized() {
		return resized;
	}
}

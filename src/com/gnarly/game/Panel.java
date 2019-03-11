package com.gnarly.game;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.display.Window;
import com.gnarly.engine.model.ColRect;
import com.gnarly.engine.model.CSRect;

public class Panel {
  
	private Window window;
	private Camera camera;
	
	private Map map;
	
	public Panel(Window window, Camera camera) {
		this.window = window;
		this.camera = camera;
		
		map = new Map(window, camera, "res/levels/tree/level.txt");
	}
	
	public void update() {
		map.update();
		if(window.keyPressed(GLFW_KEY_LEFT_CONTROL) > GLFW_RELEASE && window.keyPressed(GLFW_KEY_R) > GLFW_RELEASE)
			map.refresh();
	}
	
	public void render() {
		map.render();
	}
}
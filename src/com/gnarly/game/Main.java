package com.gnarly.game;

import com.gnarly.engine.audio.ALManagement;
import com.gnarly.engine.display.Camera;
import com.gnarly.engine.display.Window;
import com.gnarly.engine.shaders.Shader;
import com.gnarly.game.console.Console;

public class Main {

	private int FPS = 60;
	
	public static double dtime;
	public static double ttime;
	
	private ALManagement al;
	
	private Window window;
	private Camera camera;
	
	private Console console;
	
	private int state = 0;
	
	public void start() {
		long curTime, pastTime, startTime, nspf = 1000000000 / FPS;
		init();
		pastTime = System.nanoTime();
		startTime = pastTime;
		while(!window.shouldClose()) {
			curTime = System.nanoTime();
			if(curTime - pastTime > nspf) {
				dtime = (curTime - pastTime) / 1000000000d;
				ttime = (curTime - startTime) / 1000000000d;
				update();
				render();
				pastTime = curTime;
			}
		}
		al.destroy();
		Window.terminate();
	}
	
	private void init() {
		al = new ALManagement();
		window = new Window("Virulant", true, true, false);
		camera = new Camera(87.2727272725f * 11, 31.7647058824f * 17);
		Shader.init();
		console = new Console(window, camera);
	}
	
	private void update() {
		window.update();
		console.update();
		camera.update();
	}
	
	private void render() {
		window.clear();
		console.render();
		window.swap();
	}
	
	public static void main(String[] args) {
		new Main().start();
	}
}

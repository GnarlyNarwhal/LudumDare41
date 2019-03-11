package com.gnarly.game.console;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.display.Window;

public class Console {

	private final int WIDTH = 87, HEIGHT = 31;
	private final float CHAR_WIDTH = 11, CHAR_HEIGHT = 17;

	private Window window;
	private Camera camera;
	
	private ConsoleAction scene;
	
	public Console(Window window, Camera camera) {
		this.window = window;
		this.camera = camera;
		scene = newAction(ConsoleAction.loadSave());
	}
	
	public void update() {
		scene.update();
		if(scene.hasNext()) {
			String next = scene.getNext();
			scene = newAction(next);
		}
	}
	
	public void render() {
		scene.render();
	}

	public ConsoleAction newAction(String next) {
		switch (getType(next)) {
			case 0:
				return new ConsoleAnim(window, camera, next);
			case 1:
				return new ConsolePrompt(window, camera, next);
			case 2:
				return new Map(window, camera, next);
			case 3:
				return new AutoType(window, camera, next);
		};
		return null;
	}
	
	public int getType(String next) {
		try {
			Scanner input = new Scanner(new File(ConsoleAction.PATH + next + "/type.txt"));
			int type = input.nextInt();
			input.close();
			return type;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
		
	}
}

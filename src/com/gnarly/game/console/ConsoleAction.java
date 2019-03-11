package com.gnarly.game.console;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.display.Window;
import com.gnarly.engine.model.CSRect;

public abstract class ConsoleAction {

	protected static final int WIDTH = 87, HEIGHT = 31;
	protected static final float CHAR_WIDTH = 11, CHAR_HEIGHT = 17;
	
	protected static final String PATH = "res/story/";
	
	protected static final HashMap<String, String> VARS = new HashMap<>();
	
	protected Window window;
	protected Camera camera;

	protected static CSRect rect = null;
	protected float[][][] colors;
	
	protected String[] actions;
	
	protected ConsoleAction(Window window, Camera camera) {
		this.window = window;
		this.camera = camera;
		if(rect == null)
			rect = new CSRect(camera, 16, 16, "res/font/default.png", 0, 0, 0, CHAR_WIDTH, CHAR_HEIGHT, 0, false);
	}
	
	public abstract void update();

	public abstract void render();
	
	public abstract boolean hasNext();
	
	public abstract String getNext();
	
	protected abstract void load(String path);
	
	public static String loadSave() {
		try {
			Scanner input = new Scanner(new File("res/save/start.txt"));
			String start = input.nextLine();
			input.close();
			input = new Scanner(new File("res/save/defines.txt"));
			while(input.hasNextLine())
				VARS.put(input.nextLine(), input.nextLine());
			input.close();
			return start;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}

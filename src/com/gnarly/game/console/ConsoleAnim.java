package com.gnarly.game.console;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.display.Window;

public class ConsoleAnim extends ConsoleAction {

	private char[][][] console;
	private long[] npfs;
	private long time;
	private int frames, curFrame;
	
	private String next;
	
	public ConsoleAnim(Window window, Camera camera, String path) {
		super(window, camera);
		load(path);
	}

	public void update() {
		if(window.getKeys().length() > 0) {
			curFrame = frames - 1;
			time = System.nanoTime();
		}
		if(curFrame < frames && System.nanoTime() - time > npfs[curFrame]) {
			time += npfs[curFrame];
			++curFrame;
		}
	}
	
	public void render() {
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				if(console[curFrame][i][j] != ' ') {
					rect.setFrame(console[curFrame][i][j]);
					rect.setPosition(i * CHAR_WIDTH, j * CHAR_HEIGHT, 0);
					rect.setColor(colors[i][j]);
					rect.render();
				}
			}
		}
	}

	public boolean hasNext() {
		return curFrame == frames;
	}

	public String getNext() {
		return next;
	}
	
	public void skip() {
		curFrame = frames - 1;
	}
	
	public void restart() {
		time = System.nanoTime();
		curFrame = 0;
	}
	
	public void load(String path) {
		try {
			Scanner input = new Scanner(new FileInputStream(PATH + path + "/anim.txt"), "UTF-8");
			frames = input.nextInt();
			int numVars = input.nextInt();
			input.nextLine();
			next = input.nextLine();
			String[] vars = null;
			if(numVars > 0) {
				vars = new String[numVars];
				for (int i = 0; i < numVars; i++)
					vars[i] = input.nextLine();
			}
			npfs = new long[frames];
			console = new char[frames][WIDTH][HEIGHT];
			for (int i = 0; i < frames; i++) {
				npfs[i] = (long) (input.nextDouble() * 1000000000);
				input.nextLine();
				for (int j = 0; j < HEIGHT; j++) {
					String rawLine = input.nextLine();
					if(numVars > 0)
						for (int k = 0; k < numVars; k++)
							rawLine = rawLine.replaceAll(vars[k], VARS.get(vars[k]));
					char[] line = rawLine.toCharArray();
					for (int j2 = 0; j2 < WIDTH; j2++) {
						if(line[j2] == 9617)
							console[i][j2][j] = 176;
						else if(line[j2] == 9618)
							console[i][j2][j] = 177;
						else if(line[j2] == 9619)
							console[i][j2][j] = 178;
						else if(line[j2] == 9474)
							console[i][j2][j] = 179;
						else
							console[i][j2][j] = line[j2];
					}
				}
			}
			colors = new float[WIDTH][HEIGHT][4];
			for (int i = 0; i < WIDTH; i++) {
				for (int j = 0; j < HEIGHT; j++) {
					colors[i][j][0] = 0.8f;
					colors[i][j][1] = 0.8f;
					colors[i][j][2] = 0.8f;
					colors[i][j][3] = 1;
				}
			}
			
			input.close();
			restart();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

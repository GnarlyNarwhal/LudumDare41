package com.gnarly.game.console;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.display.Window;
import com.gnarly.engine.model.ColRect;

public class AutoType extends ConsoleAction {

	private static char[][] console;
	
	private long time;
	
	private int x, y;
	
	private String next;
	
	private StringBuilder message;
	private long[] nspf;
	private int cur = 0;
	
	protected AutoType(Window window, Camera camera, String path) {
		super(window, camera);
		if(console == null)
			console = new char[WIDTH][HEIGHT];
		clear();
		x = 0;
		y = 0;
		load(path);
	}

	private void clear() {
		for (int i = 0; i < WIDTH; i++)
			for (int j = 0; j < HEIGHT; j++)
				console[i][j] = ' ';
	}

	public void update() {
		if(System.nanoTime() - time > nspf[cur]) {
			appendChar(message.charAt(cur));
			++cur;
			time = System.nanoTime();
		}
	}

	public void render() {
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				if(console[i][j] != ' ') {
					rect.setFrame(console[i][j]);
					rect.setPosition(i * CHAR_WIDTH, j * CHAR_HEIGHT, 0);
					rect.setColor(colors[i][j]);
					rect.render();
				}
			}
		}
	}
	
	private void appendChar(char c) {
		if(c == '\n') {
			++y;
			x = 0;
		}
		else if (c != '\0') {
			console[x][y] = c;
			++x;
		}
	}
	
	private void appendString(String string) {
		char[] chars = string.toCharArray();
		for (char c : chars)
			appendChar(c);
	}

	public boolean hasNext() {
		return cur == message.length();
	}

	public String getNext() {
		return next;
	}

	protected void load(String path) {
		try {
			Scanner input = new Scanner(new File(PATH + path + "/auto.txt"));
			next = input.nextLine();
			int numVars = input.nextInt();
			input.nextLine();
			String[] vars = null;
			if(numVars > 0) {
				vars = new String[numVars];
				for (int i = 0; i < numVars; i++)
					vars[i] = input.nextLine();
			}
			int start = input.nextInt();
			input.nextLine();
			for (int i = 0; i < start; i++) {
				String rawLine = input.nextLine();
				for (int j = 0; j < numVars; j++)
					rawLine = rawLine.replaceAll(vars[j], VARS.get(vars[j]));
				appendString(rawLine + "\n");
			}
			message = new StringBuilder();
			while (input.hasNextLine()) {
				int type = input.nextInt();
				long speed = input.nextLong();
				int length = 0;
				if(type < 3) {
					int lines = input.nextInt();
					input.nextLine();
					for (int i = 0; i < lines; i++) {
						String rawLine = input.nextLine();
						for (int j = 0; j < numVars; j++)
							rawLine = rawLine.replaceAll(vars[j], VARS.get(vars[j]));
						if(rawLine.length() > 1 && rawLine.substring(rawLine.length() - 2, rawLine.length()).equals("-n")) {
							message.append(rawLine.substring(0, rawLine.length() - 2));
							length += rawLine.length() - 1;
						}
						else {
							message.append(rawLine);
							message.append('\n');
							length += rawLine.length() + 1;
						}
					}
					if(nspf != null)
						length += nspf.length;
					long[] lns = new long[length];
					if(nspf != null)
						for (int i = 0; i < nspf.length; i++)
							lns[i] = nspf[i];
					int s = nspf == null ? 0 : nspf.length;
					for (int i = s; i < length; i++) {
						if(type == 1)
							lns[i] = speed;
						else
							lns[i] = (long) (speed * (Math.random() * 0.35871946761 + 0.86602540378));
					}
					nspf = lns;
				}
				else {
					length = 1;
					message.append('\0');
					if(nspf != null)
						length += nspf.length;
					long[] lns = new long[length];
					if(nspf != null)
						for (int i = 0; i < nspf.length; i++)
							lns[i] = nspf[i];
					int s = nspf == null ? 0 : nspf.length;
					for (int i = s; i < length; i++)
						lns[i] = speed;
					nspf = lns;
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
			time = System.nanoTime();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}

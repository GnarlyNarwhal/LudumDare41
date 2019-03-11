package com.gnarly.game.console;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.display.Window;
import com.gnarly.engine.model.ColRect;

public class ConsolePrompt extends ConsoleAction {

	private static long SPEED = 5000000, CURSOR_SPEED = 750000000, DELAY = 500000000;

	private static int ERROR = 0, DEFINE = 1, GOTO = 2, RUN = 3, CHMOD = 4;
	
	private static char[][] console;
	
	private static HashMap<String, Action> global = new HashMap<>();
	
	private long time;
	
	private int x, y, typingRow;
	private StringBuilder message, typed;
	private int eot = 0;
	
	private ColRect cursor;
	private boolean showCursor = false;

	private class Action {
		
		public int action, limit;
		public String data;
		
		public Action(int action, int limit, String data) {
			this.action = action;
			this.limit = limit;
			this.data = data;
		}
	}
	
	private HashMap<String, Action[]> actions;
	
	boolean hasNext = false;
	String next = null;
	
	public ConsolePrompt(Window window, Camera camera, String path) {
		super(window, camera);
		if(console == null)
			console = new char[WIDTH][HEIGHT];
		cursor = new ColRect(camera, 0, 0, 0, CHAR_WIDTH, CHAR_HEIGHT, 1, 1, 1, 1, true);
		clear();
		x = 0;
		y = 0;
		typed = new StringBuilder();
		try {
			PrintWriter writer = new PrintWriter("res/save/start.txt");
			writer.println(path.equals("victory") ? "start" : path);
			writer.close();
			if(path.equals("victory")) {
				writer = new PrintWriter("res/save/defines.txt");
				writer.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		load(path);
	}

	private void clear() {
		for (int i = 0; i < WIDTH; i++)
			for (int j = 0; j < HEIGHT; j++)
				console[i][j] = ' ';
	}
	
	public void update() {
		if(window.getKeys().length() > 0 && message.length() > 0)
			skip();
		else if(message.length() > 0 && System.nanoTime() - time > SPEED) {
			time += SPEED;
			appendChar(message.charAt(0));
			message.delete(0, 1);
			showCursor = false;
		}
		else if(message.length() == 0) {
			if(eot == 2) {
				String input = window.getKeys();
				if(input.length() > 0) {
					showCursor = true;
					time = System.nanoTime();
					type(input);
				}
				else if(System.nanoTime() - time > CURSOR_SPEED) {
					showCursor = !showCursor;
					time += CURSOR_SPEED;
				}
				for (int i = 2; i < WIDTH; i++) {
					if(i - 2 < typed.length())
						console[i][typingRow] = typed.charAt(i - 2);
					else
						console[i][typingRow] = ' ';
				}
			}
			else if(eot == 1) {
				x = 2;
				y = typingRow;
				eot = 2;
			}
			else if(System.nanoTime() - time > DELAY) {
				time += DELAY;
				eot = 2;
				appendString("> ");
			}
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
		if(showCursor) {
			cursor.setPosition(x * CHAR_WIDTH, y * CHAR_HEIGHT, 0);
			cursor.render();
		}
	}
	
	public void skip() {
		if(message.length() > 0) {
			appendString(message.toString());
			message.delete(0, message.length());
			typed.setLength(0);
			if(eot == 0) {
				eot = 2;
				appendString("> ");
			}
			if(window.getKeys().charAt(0) != ' ') {
				typed.append(window.getKeys());
				x += typed.length();
			}
		}
	}
	
	private void appendChar(char c) {
		if(c == '\n') {
			++y;
			x = 0;
		}
		else {
			console[x][y] = c;
			++x;
		}
	}
	
	private void type(String string) {
		char[] chars = string.toCharArray();
		for (char c : chars)
			type(c);
	}
	
	private void type(char c) {
		if(c == GLFW_KEY_BACKSPACE && typed.length() > 0) {
			--x;
			typed.delete(x - 2, x - 1);
		}
		else if(c == GLFW_KEY_DELETE && x - 2 < typed.length()) {
			typed.delete(x - 2, x - 1);
		}
		else if(c == GLFW_KEY_LEFT && x > 2) {
			--x;
		}
		else if(c == GLFW_KEY_RIGHT && x - 2 < typed.length()) {
			++x;
		}
		else if(c == GLFW_KEY_ENTER) {
			enter();
		}
		else if(c >= ' ' && c <= '~' && typed.length() < WIDTH - 3) {
			typed.insert(x - 2, c);
			++x;
		}
	}
	
	private void appendString(String string) {
		char[] chars = string.toCharArray();
		for (char c : chars)
			appendChar(c);
	}
	
	public void enter() {
		String typed = this.typed.toString();
		Action[] acts = null;
		boolean def = false;
		if(actions.containsKey(typed))
			acts = actions.get(typed);
		else if(global.containsKey(typed))
			acts = actions.get(typed);
		else if(typed.equals("quit"))
			window.close();
		else {
			acts = actions.get("default");
			def = true;
		}
		if(acts != null) {
			for (int i = 0; i < acts.length; i++) {
				if(acts[i].action == ERROR) {
					eot = 1;
					this.typed.setLength(0);
					appendString("\n\n");
					for (int j = 0; j < WIDTH; j++)
						console[j][y] = ' ';
					time = System.nanoTime();
					showCursor = false;
					if(typed.length() > WIDTH - 16)
						typed = typed.substring(0, WIDTH - 19) + "...";
					message.append("Sorry cannot '" + typed + "'.");
				}
				else if(acts[i].action == DEFINE) {
					if(typed.length() > acts[i].limit) {
						eot = 1;
						this.typed.setLength(0);
						appendString("\n\n");
						for (int j = 0; j < WIDTH; j++)
							console[j][y] = ' ';
						time = System.nanoTime();
						showCursor = false;
						message.append("Sorry that exceeded the character limit.");
						def = false;
						i = acts.length;
					}
					else {
						ConsoleAction.VARS.put(acts[i].data, typed);
						actions.remove(typed);
						try {
							PrintWriter writer = new PrintWriter("res/save/defines.txt");
							for (Entry<String, String> entry : VARS.entrySet()) {
								writer.println(entry.getKey());
								writer.println(entry.getValue());
							}
							writer.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
				else if(acts[i].action == GOTO) {
					hasNext = true;
					next = acts[i].data;
				}
				else if(acts[i].action == CHMOD) {
					global.put(acts[i].data, null);
					eot = 1;
					this.typed.setLength(0);
					appendString("\n\n");
					for (int j = 0; j < WIDTH; j++)
						console[j][y] = ' ';
					time = System.nanoTime();
					showCursor = false;
					message.append("Successfully added '" + acts[i].data + "' to commands!");
				}
			}
		}
		if(def)
			actions.replace("default", new Action[] { new Action (ERROR, 0, null) });
	}
	
	public void getConsole(char[][] console, float[][][] colors) {
		for (int i = 0; i < WIDTH; i++)
			for (int j = 0; j < HEIGHT; j++)
				console[i][j] = console[i][j];
	}

	public boolean hasNext() {
		return hasNext;
	}

	public String getNext() {
		return next;
	}

	public void load(String path) {
		try {
			typingRow = 0;
			Scanner input = new Scanner(new File(PATH + path + "/prompt.txt"));
			int numLines = input.nextInt();
			int numVars = input.nextInt();
			int next = input.nextInt();
			input.nextLine();
			String[] vars = null;
			if(numVars > 0) {
				vars = new String[numVars];
				for (int i = 0; i < numVars; i++)
					vars[i] = input.nextLine();
			}
			message = new StringBuilder();
			for(int i = 0; i < numLines; ++i) {
				String rawLine = input.nextLine();
				for (int j = 0; j < numVars; j++)
					rawLine = rawLine.replaceAll(vars[j], VARS.get(vars[j]));
				message.append(rawLine);
				message.append('\n');
				++typingRow;
			}
			actions = new HashMap<>();
			boolean def = false;
			for (int i = 0; i < next; i++) {
				String trigger = input.nextLine();
				if(!def && trigger.equals("default"))
					def = true;
				int num = input.nextInt();
				Action[] actions = new Action[num];
				for (int j = 0; j < actions.length; j++) {
					int n1 = input.nextInt();
					int n2 = input.nextInt();
					input.nextLine();
					actions[j] = new Action(n1, n2, input.nextLine());
				}
				this.actions.put(trigger, actions);
			}
			if(!def)
				actions.put("default", new Action[] { new Action (ERROR, 0, null) });
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

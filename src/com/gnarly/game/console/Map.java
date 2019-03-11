package com.gnarly.game.console;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.joml.Vector3f;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.display.Window;
import com.gnarly.engine.model.CSRect;
import com.gnarly.game.Hitbox;
import com.gnarly.game.Player;

public class Map extends ConsoleAction {
	
	private final float CHAR_WIDTH = 11, CHAR_HEIGHT = 17;
	
	private char[][] console;
	
	private int width, height, rWidth, rHeight;
	
	private Player player;
	
	private boolean hasNext = false;
	private String next;
	
	public Map(Window window, Camera camera, String path) {
		super(window, camera);
		load(path);
	}
	
	public void update() {
		player.update();
		while(checkPlayerCollision());
		if(player.getX() < (camera.getX() + camera.getWidth() / 3f))
			camera.setX(player.getX() - camera.getWidth() / 3f);
		else if(player.getX() > (camera.getX() + camera.getWidth() * 2f / 3f))
			camera.setX(player.getX() - camera.getWidth() * 2f / 3f);
		if(player.getY() < (camera.getY() + camera.getHeight() / 3f))
			camera.setY(player.getY() - camera.getHeight() / 3f);
		else if(player.getY() > (camera.getY() + camera.getHeight() * 2f / 3f))
			camera.setY(player.getY() - camera.getHeight() * 2f / 3f);
		if (camera.getX() < 0)
			camera.setX(0);
		else if(camera.getX() + camera.getWidth() > width * CHAR_WIDTH)
			camera.setX(width * CHAR_WIDTH - camera.getWidth());
		if (camera.getY() < 0)
			camera.setY(0);
		else if(camera.getY() + camera.getHeight() > height * CHAR_HEIGHT)
			camera.setY(height * CHAR_HEIGHT - camera.getHeight());
	}
	
	private boolean checkPlayerCollision() {
		Hitbox playerHitbox = player.getHitbox();
		int minX = (int) (player.getX() / CHAR_WIDTH);
		int maxX = (int) ((player.getX() + player.getWidth()) / CHAR_WIDTH);
		int minY = (int) (player.getY() / CHAR_HEIGHT);
		int maxY = (int) ((player.getY() + player.getHeight()) / CHAR_HEIGHT);
		Hitbox closest = null;
		Hitbox current = null;
		Vector3f length1 = null;
		int x = 0, y = 0;
		for (int i = Math.max(minX, 0); i <= maxX && i < console.length; i++) {
			for (int j = Math.max(minY, 0); j <= maxY && j < console[0].length; j++) {
				if (console[i][j] == '0' || console[i][j] == '1' || console[i][j] == '#' || console[i][j] == 'E') {
					if (closest == null) {
						closest = new Hitbox(i * CHAR_WIDTH, j * CHAR_HEIGHT, CHAR_WIDTH, CHAR_HEIGHT);
						current = new Hitbox(i * CHAR_WIDTH, j * CHAR_HEIGHT, CHAR_WIDTH, CHAR_HEIGHT);
						length1 = closest.getCenter().sub(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2, 0, new Vector3f()).div(CHAR_WIDTH, CHAR_HEIGHT, 1);
						x = i;
						y = j;
					} else {
						current.setPosition(i * CHAR_WIDTH, j * CHAR_HEIGHT);
						Vector3f length2 = current.getCenter().sub(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2, 0, new Vector3f()).div(CHAR_WIDTH, CHAR_HEIGHT, 1);
						if (length1.lengthSquared() > length2.lengthSquared()) {
							closest.setPosition(current.getX(), current.getY());
							length1 = length2;
							x = i;
							y = j;
						}
					}
				}
			}
		}
		boolean ret = false;
		if (closest != null) {
			if (closest.collides(playerHitbox)) {
				if(console[x][y] == 'E')
					hasNext = true;
				else if(console[x][y] == '#')
					reset();
				else {
					Vector3f transform = closest.getTransform(playerHitbox);
					if ((player.getVelocity().y < 0 && transform.y < 0) || (player.getVelocity().y > 0 && transform.y > 0)) {
						transform.x = closest.getTransformX(playerHitbox);
						transform.y = 0;
					}
					else if ((player.getVelocity().x < 0 && transform.x < 0) || (player.getVelocity().x > 0 && transform.x > 0)) {
						transform.y = closest.getTransformY(playerHitbox);
						transform.x = 0;
					}
					player.translate(transform);
					if (transform.y < 0)
						player.hitBottom();
					else if (transform.x > 0)
						player.hitLeft();
					else if (transform.x < 0)
						player.hitRight();
					else
						player.hitTop();
					ret = true;
				}
			}
		}
		return ret;
	}
	
	public void reset() {
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
				if(console[i][j] == 'S')
					player.setPosition(i * CHAR_WIDTH, j * CHAR_HEIGHT, 0);
	}
	
	public void render() {
		player.render();
        int minX = (int) (camera.getX() / CHAR_WIDTH);
        int maxX = Math.min(minX + rWidth, console.length);
        int minY = (int) (camera.getY() / CHAR_HEIGHT);
        int maxY = Math.min(minY + rHeight, console[0].length);
		for (int i = minX; i < maxX; i++) {
			for (int j = minY; j < maxY; j++) {
				if (colors[i][j][3] > 0) {
					rect.setFrame(console[i][j]);
					rect.setPosition(i * CHAR_WIDTH,  j * CHAR_HEIGHT, -0.1f);
					rect.setColor(colors[i][j]);
					rect.render();
				}
			}
		}
	}
	
	public void load(String path) {
		try {
			Scanner input = new Scanner(new File(PATH + path + "/level.txt"));
			next = input.nextLine();
			width = input.nextInt();
			height = input.nextInt();
			input.nextLine();
	        rWidth = (int) Math.ceil((camera.getX() + camera.getWidth()) / CHAR_WIDTH) + 1;
	        rHeight = (int) Math.ceil((camera.getY() + camera.getHeight()) / CHAR_HEIGHT) + 1;
			console = new char[width][height];
			for (int i = 0; i < height; i++) {
				char[] line = input.nextLine().toCharArray();
				for (int j = 0; j < width; j++) {
					console[j][i] = line[j];
					if(line[j] == 'S')
						player = new Player(window, camera, PATH + path + "/player.txt", j * CHAR_WIDTH, i * CHAR_HEIGHT, 0, 11, 17);
				}
			}
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		colors = new float[width][height][4];
		try {
			BufferedImage image = ImageIO.read(new File(PATH + path + "/colors.png"));
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					int color = image.getRGB(i, j);
					colors[i][j][0] = (color >> 16 & 0xFF) / 255f;
					colors[i][j][1] = (color >> 8  & 0xFF) / 255f;
					colors[i][j][2] = (color       & 0xFF) / 255f;
					colors[i][j][3] = (color >> 24 & 0xFF) / 255f;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getConsole(char[][] console, float[][][] colors) {}

	public boolean hasNext() {
		return hasNext;
	}

	public String getNext() {
		camera.setPosition(0, 0);
		return next;
	}
}

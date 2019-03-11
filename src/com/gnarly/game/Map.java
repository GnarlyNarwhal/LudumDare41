package com.gnarly.game;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.joml.Vector3f;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.display.Window;
import com.gnarly.engine.model.CSRect;

public class Map {

	private final float WIDTH = 11, HEIGHT = 17;
	
	private int width, height;
	
	private CSRect rect;
	private char[][] map;
	private float[][][] colors;

	private Player player;
	
	private String path;
	
	public Map(Window window, Camera camera, String path) {
		this.path = path;
		rect = new CSRect(camera, 16, 16, "res/font/default.png", 0, 0, 0, WIDTH, HEIGHT, 0, false);
		InputReader input = new InputReader(path);
		width = input.nextInt();
		height = input.nextInt();
		map = new char[width][height];
		for (int i = 0; i < height; i++) {
			char[] line = input.nextLine().toCharArray();
			for (int j = 0; j < width; j++)
				map[j][i] = line[j];
		}
		input.close();
		colors = new float[width][height][3];
		try {
			BufferedImage image = ImageIO.read(new File("res/levels/tree/colors.png"));
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					int color = image.getRGB(i, j);
					colors[i][j][0] = (color >> 16 & 0xFF) / 255f;
					colors[i][j][1] = (color >> 8  & 0xFF) / 255f;
					colors[i][j][2] = (color	   & 0xFF) / 255f;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		player = new Player(window, camera, "res/levels/tree/player.txt", 20, 20, 0, 22, 34);
	}
	
	public void update() {
		player.update();
		while(checkPlayerCollision());
	}
	
	private boolean checkPlayerCollision() {
		Hitbox playerHitbox = player.getHitbox();
		int minX = (int) (player.getX() / WIDTH);
		int maxX = (int) ((player.getX() + player.getWidth()) / WIDTH);
		int minY = (int) (player.getY() / HEIGHT);
		int maxY = (int) ((player.getY() + player.getHeight()) / HEIGHT);
		Hitbox closest = null;
		Hitbox current = null;
		Vector3f length1 = null;
		for (int i = Math.max(minX, 0); i <= maxX && i < map.length; i++) {
			for (int j = Math.max(minY, 0); j <= maxY && j < map[0].length; j++) {
				if (map[i][j] != ' ') {
					if (closest == null) {
						closest = new Hitbox(i * WIDTH, j * HEIGHT, WIDTH, HEIGHT);
						current = new Hitbox(i * WIDTH, j * HEIGHT, WIDTH, HEIGHT);
						length1 = closest.getCenter().sub(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2, 0, new Vector3f()).div(WIDTH, HEIGHT, 1);
					} else {
						current.setPosition(i * WIDTH, j * HEIGHT);
						Vector3f length2 = current.getCenter().sub(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2, 0, new Vector3f()).div(WIDTH, HEIGHT, 1);
						if (length1.lengthSquared() > length2.lengthSquared()) {
							closest.setPosition(current.getX(), current.getY());
							length1 = length2;
						}
					}
				}
			}
		}
		boolean ret = false;
		if (closest != null) {
			if (closest.collides(playerHitbox)) {
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
		return ret;
	}
	
	public void render() {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if(map[i][j] != ' ') {
					rect.setFrame(map[i][j]);
					rect.setPosition(i * 11,  j * 17, -0.1f);
					rect.setColor(colors[i][j][0], colors[i][j][1], colors[i][j][2]);
					rect.render();
				}
			}
		}
		player.render();
	}
	
	public void refresh() {
		InputReader input = new InputReader(path);
		width = input.nextInt();
		height = input.nextInt();
		map = new char[width][height];
		for (int i = 0; i < height; i++) {
			char[] line = input.nextLine().toCharArray();
			for (int j = 0; j < width; j++)
				map[j][i] = line[j];
		}
		input.close();
		colors = new float[width][height][3];
		try {
			BufferedImage image = ImageIO.read(new File("res/levels/tree/colors.png"));
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					int color = image.getRGB(i, j);
					colors[i][j][0] = (color >> 16 & 0xFF) / 255f;
					colors[i][j][1] = (color >> 8  & 0xFF) / 255f;
					colors[i][j][2] = (color       & 0xFF) / 255f;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

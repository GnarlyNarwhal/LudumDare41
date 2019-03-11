package com.gnarly.game;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.joml.Vector3f;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.display.Window;
import com.gnarly.engine.model.TexRect;

public class Player {
	
	public static float r = 1, g = 1, b = 1;
	
	private final float TOP_SPEED = 10.9999f;
	
	private final int SPEED, SPACE_HELD, WALL_SLIDE, JUMP, CONITUED_JUMP, WALL_JUMP, GRAVITY;

	private Window window;
	private Camera camera;
	
	private TexRect rect;
	private Hitbox hitbox;
	
	private Vector3f position, dims;
	
	private boolean grounded = true, jump = false;
	private int space = 0;
	private Vector3f velocity;
	
	public Player(Window window, Camera camera, String path, float x, float y, float z, float width, float height) {
		this.window = window;
		this.camera = camera;
		position = new Vector3f(x, y, z);
		dims = new Vector3f(width, height, 1);
		rect = new TexRect(camera, "res/img/player/player.png", 0, 0, 0, 0, 0, 0, false);
		hitbox = new Hitbox(0, 0, 0, 0);
		rect.sync(position, dims);
		hitbox.sync(position, dims);
		velocity = new Vector3f();
		Scanner input = null;
		try {
			input = new Scanner(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		SPEED = input.nextInt();
		SPACE_HELD = input.nextInt();
		WALL_SLIDE = input.nextInt();
		JUMP = input.nextInt();
		CONITUED_JUMP = input.nextInt();
		WALL_JUMP = input.nextInt();
		GRAVITY = input.nextInt();
		input.close();
	}
		
	public void update() {
		boolean direction = false;
		if (window.keyPressed(GLFW_KEY_D) != 0 && window.keyPressed(GLFW_KEY_A) == 0) {
			if(grounded)
				velocity.x = SPEED;
			else if (velocity.x < SPEED) {
				velocity.x += SPEED * 0.1;
				if(velocity.x > SPEED)
					velocity.x = SPEED;
			}
			direction = true;
		}
		if (window.keyPressed(GLFW_KEY_A) != 0 && window.keyPressed(GLFW_KEY_D) == 0) {
			if(grounded)
				velocity.x = -SPEED;
			else if (velocity.x > -SPEED) {
				velocity.x -= SPEED * 0.1;
				if (velocity.x < -SPEED)
					velocity.x = -SPEED;
			}
			direction = true;
		}
		if(!direction && grounded)
			velocity.x = 0;
		if (window.keyPressed(GLFW_KEY_SPACE) != 0) {
			if (grounded) {
				jump = true;
				velocity.y = -JUMP;
				grounded = false;
				space = 1;
			}
			else if(space > 0 && space < SPACE_HELD && jump)
				velocity.y -= CONITUED_JUMP * (SPACE_HELD - space + 1 ) / SPACE_HELD;
			++space;
		}
		else {
			space = 0;
			jump = false;
		}
		if(velocity.y < 0)
			velocity.y += GRAVITY;
		else
			velocity.y += GRAVITY * 2;
		Vector3f tv = velocity.mul((float) Main.dtime, new Vector3f());
		if(tv.x > TOP_SPEED)
			tv.x = TOP_SPEED;
		if(tv.x < -TOP_SPEED)
			tv.x = -TOP_SPEED;
		if(tv.y > TOP_SPEED)
			tv.y = TOP_SPEED;
		if(tv.y < -TOP_SPEED)
			tv.y = -TOP_SPEED;
		position.add(tv);
		grounded = false;
	}

	public void hitBottom() {
		velocity.y = 0;
		grounded = true;
	}

	public void hitLeft() {
		velocity.x = 0;
		if (velocity.y > WALL_SLIDE)
			velocity.y = WALL_SLIDE;
		if (!grounded && space == 1) {
			jump = true;
			space = 12;
			velocity.x = SPEED * 2;
			velocity.y = -WALL_JUMP;
		}
	}

	public void hitRight() {
		velocity.x = 0;
		if (velocity.y > WALL_SLIDE)
			velocity.y = WALL_SLIDE;
		if (!grounded && space == 1) {
			jump = true;
			space = 12;
			velocity.x = -SPEED * 2;
			velocity.y = -WALL_JUMP;
		}
	}

	public void hitTop() {
		velocity.y = 0;
	}

	public void render() {
		rect.setColor(r, g, b);
		rect.render();
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public Vector3f getVelocity() {
		return velocity;
	}
	
	public float getX() { 
		return position.x;
	}
	
	public float getY() { 
		return position.y;
	}
	
	public float getWidth() { 
		return dims.x;
	}
	
	public float getHeight() { 
		return dims.y;
	}
	
	public Hitbox getHitbox() {
		return hitbox;
	}

	public void setPosition(float x, float y, float z) {
		this.position.set(x, y, z);
	}
	
	public void setPosition(Vector3f position) {
		this.position.set(position);
	}

	public void translate(float x, float y, float z) {
		this.position.add(x, y, z);
	}
	
	public void translate(Vector3f position) {
		this.position.add(position);
	}

	public void setDims(float x, float y, float z) {
		this.dims.set(x, y, z);
	}
	
	public void setDims(Vector3f dims) {
		this.dims.set(dims);
	}
	
	public void setVelocityX(float dx) {
		velocity.x = dx;
	}
	
	public void setVelocityY(float dy) {
		velocity.y = dy;
	}
	
	public void setVelocity(Vector3f velocity) {
		this.velocity.set(velocity);
	}
}

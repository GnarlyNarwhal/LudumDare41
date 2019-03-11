package com.gnarly.game;

import org.joml.Vector3f;

public class Hitbox {

	private Vector3f position, dims;
	
	public Hitbox(float x, float y, float width, float height) {
		position = new Vector3f(x, y, 0);
		dims = new Vector3f(width, height, 0);
	}
	
	public boolean collides(Hitbox hitbox) {
		Vector3f center = getCenter();
		Vector3f center2 = hitbox.getCenter();
		Vector3f halfExtent = getHalfExtent();
		Vector3f halfExtent2 = hitbox.getHalfExtent();
		return Math.abs(center.x - center2.x) < Math.abs(halfExtent.x + halfExtent2.x) &&
			   Math.abs(center.y - center2.y) < Math.abs(halfExtent.y + halfExtent2.y);
	}

	public Vector3f getTransform(Hitbox hitbox) {
		Vector3f center = getCenter();
		Vector3f center2 = hitbox.getCenter();
		Vector3f halfExtent = getHalfExtent();
		Vector3f halfExtent2 = hitbox.getHalfExtent();
		float dx = Math.abs(center.x - center2.x) - Math.abs(halfExtent.x + halfExtent2.x);
		float dy = Math.abs(center.y - center2.y) - Math.abs(halfExtent.y + halfExtent2.y);
		if (Math.abs(dx) < Math.abs(dy))
			dy = 0;
		else
			dx = 0;
		if(center.x < center2.x) dx = -dx;
		else if(center.y < center2.y) dy = -dy;
		return new Vector3f(dx, dy, 0);
	}

	public float getTransformX(Hitbox hitbox) {
		Vector3f center = getCenter();
		Vector3f center2 = hitbox.getCenter();
		Vector3f halfExtent = getHalfExtent();
		Vector3f halfExtent2 = hitbox.getHalfExtent();
		float dx = Math.abs(center.x - center2.x) - Math.abs(halfExtent.x + halfExtent2.x);
		if (center.x < center2.x) dx = -dx;
		return dx;
	}
	
	public float getTransformY(Hitbox hitbox) {
		Vector3f center = getCenter();
		Vector3f center2 = hitbox.getCenter();
		Vector3f halfExtent = getHalfExtent();
		Vector3f halfExtent2 = hitbox.getHalfExtent();
		float dy = Math.abs(center.y - center2.y) - Math.abs(halfExtent.y + halfExtent2.y);
		if (center.y < center2.y) dy = -dy;
		return dy;
	}
	
	public static boolean intersect(Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4) {
		float cmax = p3.x - p1.x, cmay = p3.y - p1.y, rx = p2.x - p1.x, ry = p2.y - p1.y, sx = p4.x - p3.x, sy = p4.y - p3.y;
		float rxs = rx * sy - ry * sx;
		float t = (cmax * sy - cmay * sx) / rxs;
		float u = (cmax * ry - cmay * rx) / rxs;
		return (u >= 0 && u <= 1 && t >= 0 && t <= 1);
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(float x, float y) {
		position.x = x;
		position.y = y;
	}
	
	public void translate(Vector3f translate) {
		position.x += translate.x;
		position.y += translate.y;
	}
	
	public void setBounds(float width, float height) {
		dims.x = width;
		dims.y = height;
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
	
	public Vector3f getCenter() {
		return dims.div(2, new Vector3f()).add(position);
	}
	
	public Vector3f getHalfExtent() {
		return dims.div(2, new Vector3f());
	}
	
	public void set(float x, float y, float width, float height) {
		position.x = x;
		position.y = y;
		dims.x = width;
		dims.y = height;
	}
	
	public void sync(Vector3f position, Vector3f dims) {
		this.position = position;
		this.dims = dims;
	}
}

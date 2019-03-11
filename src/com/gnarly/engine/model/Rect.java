package com.gnarly.engine.model;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.gnarly.engine.display.Camera;

public class Rect {

	protected static Vao vao;

	protected Camera camera;
	
	protected Vector3f dims;
	protected Vector3f position;
	protected float rotation;
	protected boolean gui;
	
	protected Rect(Camera camera, float x, float y, float z, float width, float height, float rotation, boolean gui) {
		this.camera = camera;
		dims = new Vector3f(width, height, 1);
		position = new Vector3f(x, y, z);
		this.rotation = rotation;
		this.gui = gui;
		if(vao == null) {
			float vertices[] = {
				1, 0, 0, // Top left
				1, 1, 0, // Bottom left
				0, 1, 0, // Bottom right
				0, 0, 0  // Top right
			};
			int indices[] = {
				0, 1, 3,
				1, 2, 3
			};
			float[] texCoords = {
				1, 0,
				1, 1,
				0, 1,
				0, 0
			};
			vao = new Vao(vertices, indices);
			vao.addAttrib(texCoords, 2);
		}
	}	
	
	public float getX() {
		return position.x;
	}
	
	public float getY() {
		return position.y;
	}
	
	public Vector3f getPosition() {
		return new Vector3f(position);
	}
	
	public float getWidth() {
		return dims.x;
	}
	
	public float getHeight() {
		return dims.y;
	}
	
	public void set(float x, float y, float width, float height) {
		position.x = x;
		position.y = y;
		dims.x = width;
		dims.y = height;
	}
	
	public void setWidth(float width) {
		dims.x = width;
	}
	
	public void setHeight(float height) {
		dims.y = height;
	}
	
	public void setPosition(float x, float y, float z) {
		position.set(x, y, z);
	}
	
	public void setPosition(Vector2f position) {
		this.position.x = position.x;
		this.position.y = position.y;
	}
	
	public void setPosition(Vector3f position) {
		this.position.set(position);
	}
	
	public void translate(float x, float y, float z) {
		position.add(x, y, z);
	}
	
	public void setRotation(float angle) {
		rotation = angle;
	}
	
	public void rotate(float angle) {
		rotation += angle;
	}
	
	public void sync(Vector3f position, Vector3f dims) {
		this.position = position;
		this.dims = dims;
	}
}

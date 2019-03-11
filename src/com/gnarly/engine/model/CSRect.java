package com.gnarly.engine.model;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.shaders.Shader;
import com.gnarly.engine.shaders.Shader2cs;
import com.gnarly.engine.texture.Spritesheet;

public class CSRect extends Rect {

	private Spritesheet texture;
	private Shader2cs shader = Shader.SHADER2TM;
	
	private int frame = 0;
	private float r, g, b, a;
	
	public CSRect(Camera camera, int fWidth, int fHeight, String path, float x, float y, float z, float width, float height, float rotation, boolean gui) {
		super(camera, x, y, z, width, height, rotation, gui);
		texture = new Spritesheet(fWidth, fHeight, path);
		r = 1;
		g = 1;
		b = 1;
		a = 1;
	}
	
	public void render() {
		texture.bind();
		shader.enable();
		Matrix4f cmat = gui ? camera.getProjection() : camera.getMatrix();
		shader.setMVP(cmat.translate(position.add(dims.x / 2, dims.y / 2, 0, new Vector3f())).rotateZ(rotation * 3.1415927f / 180).scale(dims).translate(-0.5f, -0.5f, 0));
		shader.setFrame(frame, texture);
		shader.setColor(r, g, b, a);
		vao.render();
		shader.disable();
		texture.unbind();
	}
	
	public void setFrame(int frame) {
		this.frame = frame;
	}
	
	public void setColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public void setColor(float[] colors) {
		this.r = colors[0];
		this.g = colors[1];
		this.b = colors[2];
		this.a = colors[3];
	}
}

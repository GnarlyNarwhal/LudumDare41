package com.gnarly.engine.model;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.shaders.Shader;
import com.gnarly.engine.shaders.Shader2c;

public class ColRect extends Rect {
	
	private Shader2c shader;
	
	private float r, g, b, a;
	
	public ColRect(Camera camera, float x, float y, float z, float width, float height, float r, float g, float b, float a, boolean gui) {
		super(camera, x, y, z, width, height, 0, gui);
		shader = Shader.SHADER2C;
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public void render() {
		shader.enable();
		shader.setColor(r, g, b, a);
		Matrix4f cmat = gui ? camera.getProjection() : camera.getMatrix();
		shader.setMVP(cmat.translate(position.add(dims.x / 2, dims.y / 2, 0, new Vector3f())).rotateZ(rotation * 3.1415927f / 180).scale(dims).translate(-0.5f, -0.5f, 0));
		vao.render();
		shader.disable();
	}
	
	public void setOpacity(float opacity) {
		a = opacity;
	}
}

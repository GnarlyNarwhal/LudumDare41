package com.gnarly.engine.model;

import org.joml.Vector3f;

import com.gnarly.engine.display.Camera;
import com.gnarly.engine.shaders.Shader;
import com.gnarly.engine.shaders.Shader2c;

public class Line {

	private static Vao cap, rect;

	private Camera camera;
	private Shader2c shader;
	
	private Vector3f position;
	private float angle, length, thickness;
	private float r, g, b, a;
	
	public Line(Camera camera, float x1, float y1, float x2, float y2, float depth, float thickness) {
		this.camera = camera;
		shader = Shader.SHADER2C;
		if(cap == null)
			initVaos();
		this.thickness = thickness;
		position = new Vector3f(x1, y1, depth);
		setPoints(x1, y1, x2, y2);
		r = 1;
		g = 1;
		b = 1;
		a = 1;
	}
	
	private void initVaos() {
		float[] rVertices = {
			0,  0.5f, 0,
			0, -0.5f, 0,
			1, -0.5f, 0,
			1,  0.5f, 0
		};
		int rIndices[] = {
			0, 1, 3,
			1, 2, 3
		};
		rect = new Vao(rVertices, rIndices);
		final int NUM_POINTS = 10;
		float[] cVertices = new float[NUM_POINTS * 3];
		int[] cIndices = new int[(NUM_POINTS - 2) * 3];
		for (int i = 0; i < cVertices.length; i += 3) {
			double angle = Math.PI * i / (NUM_POINTS * 3 - 3) + Math.PI / 2;
			cVertices[i    ] = (float) Math.cos(angle) / 2;
			cVertices[i + 1] = (float) Math.sin(angle) / 2;
			cVertices[i + 2] = 0;
		}
		for (int i = 0; i < cIndices.length; i += 3) {
			cIndices[i    ] = 0;
			cIndices[i + 1] = i / 3 + 1;
			cIndices[i + 2] = i / 3 + 2;
		}
		cap = new Vao(cVertices, cIndices);
	}
	
	public void render() {
		shader.enable();
		shader.setColor(r, g, b, a);
		shader.setMVP(camera.getMatrix().translate(position).rotateZ(angle).scale(thickness));
		cap.render();
		shader.setMVP(camera.getMatrix().translate(position).rotateZ(angle).scale(length, thickness, 1));
		rect.render();
		shader.setMVP(camera.getMatrix().translate(position.add((float) (Math.cos(angle) * length), (float) (-Math.sin(Math.PI + angle) * length), 0, new Vector3f())).rotateZ((float) Math.PI).rotateZ(angle).scale(thickness));
		cap.render();
		shader.disable();
	}
	
	public void setAngle(float x, float y, float angle, float length) {
		position.x = x;
		position.y = y;
		this.angle = angle;
		this.length = length;
	}
	
	public void setPoints(float x1, float y1, float x2, float y2) {
		float xl = x2 - x1;
		float yl = y2 - y1;
		length = (float) Math.sqrt(xl * xl + yl * yl);
		if(x1 != x2) {
			angle = (float) Math.atan(yl / xl);
			if(xl < 0)
				angle += Math.PI;
			setAngle(x1, y1, angle, length);
		}
		else if(y1 > y2)
			setAngle(x1, y1, (float) Math.PI * 1.5f, length);
		else if(y1 < y2)
			setAngle(x1, y1, (float) Math.PI * 0.5f, length);
		else
			setAngle(x1, y1, 0, 0);
	}
	
	public void setPoints(Vector3f p1, Vector3f p2) {
		setPoints(p1.x, p1.y, p2.x, p2.y);
	}
	
	public void setThickness(float thickness) {
		this.thickness = thickness;
	}
	
	public void setDepth(float z) {
		position.z = z;
	}
	
	public void setColor(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
}

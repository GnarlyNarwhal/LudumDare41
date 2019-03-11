package com.gnarly.engine.shaders;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform4f;

public class Shader2t extends Shader {

	int colorLoc;
	
	protected Shader2t() {
		super("res/shaders/s2t/vert.gls", "res/shaders/s2t/frag.gls");
		getUniforms();
	}
	
	@Override
	protected void getUniforms() {
		colorLoc = glGetUniformLocation(program, "iColor");
	}
	
	public void setColor(float r, float g, float b, float a) {
		glUniform4f(colorLoc, r, g, b, a);
	}
}

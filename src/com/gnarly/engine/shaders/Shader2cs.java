package com.gnarly.engine.shaders;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL40.glUniform4d;

import com.gnarly.engine.texture.Spritesheet;

public class Shader2cs extends Shader {
	
	int offsetLoc, dimsLoc, colorLoc;
	
	protected Shader2cs() {
		super("res/shaders/s2cs/vert.gls", "res/shaders/s2cs/frag.gls");
		getUniforms();
	}
	
	@Override
	protected void getUniforms() {
		offsetLoc = glGetUniformLocation(program, "offset");
		dimsLoc = glGetUniformLocation(program, "dims");
		colorLoc = glGetUniformLocation(program, "iColor");
	}

	public void setFrame(int frame, Spritesheet map) {
		int x = frame % 16;
		int y = frame / 16;
		glUniform2f(offsetLoc, x * map.getFWidth(), y * map.getFHeight());
		glUniform2f(dimsLoc, map.getFWidth(), map.getFHeight());
	}
	
	public void setColor(float r, float g, float b, float a) {
		glUniform4f(colorLoc, r, g, b, a);
	}
}
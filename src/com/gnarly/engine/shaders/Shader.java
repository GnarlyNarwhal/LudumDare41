package com.gnarly.engine.shaders;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.joml.Matrix4f;

public abstract class Shader {

	public static Shader2c  SHADER2C;
	public static Shader2t  SHADER2T;
	public static Shader2cs SHADER2TM;
	
	protected int program;
	
	protected int mvpLoc;
	
	protected Shader(String vertPath, String fragPath) {
		program = glCreateProgram();
		
		int vert = loadShader(vertPath, GL_VERTEX_SHADER);
		int frag = loadShader(fragPath, GL_FRAGMENT_SHADER);
		
		glAttachShader(program, vert);
		glAttachShader(program, frag);
		
		glLinkProgram(program);
		
		glDetachShader(program, vert);
		glDetachShader(program, frag);
		
		glDeleteShader(vert);
		glDeleteShader(frag);
		
		mvpLoc = glGetUniformLocation(program, "mvp");
	}
	
	private int loadShader(String path, int type) {
		StringBuilder file = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
			String line;
			while((line = reader.readLine()) != null)
				file.append(line + '\n');
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String source = file.toString();
		int shader = glCreateShader(type);
		glShaderSource(shader, source);
		glCompileShader(shader);
		if(glGetShaderi(shader, GL_COMPILE_STATUS) != 1)
			throw new RuntimeException("Failed to compile shader: " + path + "! " + glGetShaderInfoLog(shader));
		return shader;
	}
	
	protected abstract void getUniforms();
	
	public void setMVP(Matrix4f matrix) {
		glUniformMatrix4fv(mvpLoc, false, matrix.get(new float[16]));
	}
	
	public void enable() {
		glUseProgram(program);
	}
	
	public void disable() {
		glUseProgram(0);
	}
	
	public void destroy() {
		glDeleteProgram(program);
	}
	
	public static void init() {
		SHADER2C = new Shader2c();
		SHADER2T = new Shader2t();
		SHADER2TM = new Shader2cs();
	}
}
